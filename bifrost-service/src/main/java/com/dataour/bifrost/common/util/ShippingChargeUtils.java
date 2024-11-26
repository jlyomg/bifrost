package com.dataour.bifrost.common.util;

import com.alibaba.fastjson.JSONObject;
import com.dataour.bifrost.common.enums.RespCodeEnums;
import com.dataour.bifrost.common.module.request.FreightChargeReq;
import com.dataour.bifrost.common.module.request.ShippingChargeReq;
import com.dataour.bifrost.common.module.request.SurchargeTransactionReq;
import com.dataour.bifrost.common.module.response.*;
import com.dataour.bifrost.common.module.response.ShippingChargeResp.MatchedItem;
import com.dataour.bifrost.common.module.response.ShippingChargeResp.UnmatchedItem;
import com.dataour.bifrost.service.TransactionService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShippingChargeUtils {
    private static final List<String> COMPANY_NAME_LIST = new ArrayList<String>(
            Arrays.asList("AUSP", "HUNT", "ALLI", "COPL")
    );
    private static final HashMap<String, ArrayList<String>> COMPANY_FIELD_DICT = new HashMap<String, ArrayList<String>>() {
        {
            put("AUSP", new ArrayList<>(Arrays.asList("Assignment No.", "Invoice Total", "Material", "Amount excl Tax")));
            put("HUNT", new ArrayList<>(Arrays.asList("ConsignmentNumber", "PriceGrandTotal", "PriceAdditionalServiceTotal")));
            put("ALLI", new ArrayList<>(Arrays.asList("Docket No.", "Total Charges", "Charges Inc. GST")));
            put("COPL", new ArrayList<>(Arrays.asList("Consignment Ref.", "Ex GST", "GST")));
        }
    };

    /**
     * Get the actual price and surcharge dictionary from the csv file
     *
     * @param csvFile     The input csv file containing the shipping charge information
     * @param companyName The company name of the csv file
     * @return An ActualAndSurchargeDicResp object containing the actual price dictionary, surcharge dictionary,
     * and the record quantity: <br>
     * actualPriceDic: {ConsignmentNo: actualPrice} <br>
     * surchargeDic: {ConsignmentNo: [{surchargePrice1, surchargeDetail1, surchargeItemName1},...]}<br>
     * recordQty: the number of non-empty records in the csv file
     */
    public static Resp<ActualAndSurchargeDicResp> getActualPriceAndSurchargeDic(MultipartFile csvFile, String companyName) {

        // check whether the file name matches the company name
        String filename = csvFile.getOriginalFilename();
        Resp<Boolean> fileNameResp = checkFilename(companyName, filename);
        if (!fileNameResp.isSuccess()) return Resp.error(fileNameResp.getCode(), fileNameResp.getMessage());

        HashMap<String, String> actualPriceDic = new HashMap<>();
        HashMap<String, List<SurchargeTransactionReq>> surchargeDic = new HashMap<>();
        int recordQty = 0;
        ActualAndSurchargeDicResp actualAndSurchargeDicResp = new ActualAndSurchargeDicResp();

        try (InputStreamReader reader = new InputStreamReader(csvFile.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                String invoiceNo = "";
                String actualPrice = "0";
                boolean hasSurcharge = false;
                String surchargePrice = "";
                String surchargeDetail = "";
                String surchargeItemName = "";

                switch (companyName) {
                    case "AUSP":
                        invoiceNo = csvRecord.get("Assignment No.");
                        actualPrice = csvRecord.get("Invoice Total");

                        // AusPost surcharge only on BM1 and BM2 materials
                        if (csvRecord.get("Material").startsWith("BM1") || csvRecord.get("Material").startsWith("BM2")) {
                            surchargePrice = csvRecord.get("Amount excl Tax");
                            if (BigDecimalUtils.toBigDecimal(surchargePrice).compareTo(BigDecimal.ZERO) > 0) {
                                hasSurcharge = true;
                                surchargeDetail = csvRecord.get("Material") + ": $" + surchargePrice;
                                surchargeItemName = "AUSP_" + csvRecord.get("Material");
                            }
                        }
                        break;

                    case "HUNT":
                        invoiceNo = csvRecord.get("ConsignmentNumber");
                        actualPrice = csvRecord.get("PriceGrandTotal");
                        surchargePrice = csvRecord.get("PriceAdditionalServiceTotal");
                        if (BigDecimalUtils.toBigDecimal(surchargePrice).compareTo(BigDecimal.ZERO) > 0) {
                            hasSurcharge = true;
                            surchargeDetail = "Additional Service";
                            surchargeItemName = "HUNT_PriceAdditionalServiceTotal";
                        }
                        break;

                    case "ALLI":
                        invoiceNo = csvRecord.get("Docket No.");
                        actualPrice = csvRecord.get("Total Charges").replace("$", "");
                        String surchargeText = csvRecord.get("Charges Inc. GST");

                        // ALLI surcharge does not include "Job", "Dangerous goods surcharge" and "Home delivery"
                        if (surchargeText.length() > 0) {
                            String[] chargeArray = Arrays.stream(surchargeText.split(", "))
                                    .filter(charge -> !charge.startsWith("Job") &&
                                            !charge.startsWith("Dangerous goods surcharge") &&
                                            !charge.startsWith("Home delivery")).toArray(String[]::new);

                            // calculate sum of surcharge
                            BigDecimal surcharge = Arrays.stream(chargeArray)
                                    .map(charge -> charge.split("\\$")[1])    // Get the price part
                                    .map(BigDecimalUtils::toBigDecimal)    // Convert the string to BigDecimal
                                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up all the BigDecimal values

                            if (surcharge.compareTo(BigDecimal.ZERO) > 0) {
                                hasSurcharge = true;
                                surchargePrice = surcharge.toString();
                                surchargeDetail = String.join(";", chargeArray);
                                surchargeItemName = "AllI_ALL_BUT_RETURN";
                            }
                        }
                        break;

                    case "COPL":
                        BigDecimal exGST = BigDecimalUtils.toBigDecimal(csvRecord.get("Ex GST"));
                        BigDecimal gst = BigDecimalUtils.toBigDecimal(csvRecord.get("GST"));
                        invoiceNo = csvRecord.get("Consignment Ref.");
                        actualPrice = exGST.add(gst).toString();
                        // COPL has no surcharge
                        break;
                }

                // make sure the invoice number and actual price are not empty
                if (invoiceNo.length() == 0 || actualPrice.length() == 0) {
                    continue;
                }

                recordQty++;

                // Update the actual price for the shipping charge
                if (!actualPriceDic.containsKey(invoiceNo)) {
                    actualPriceDic.put(invoiceNo, actualPrice);
                } else {
                    actualPriceDic.put(invoiceNo, BigDecimalUtils.toBigDecimal(actualPriceDic.get(invoiceNo)).add(BigDecimalUtils.toBigDecimal(actualPrice)).toString());
                }

                // Update the surcharge for the shipping charge
                if (hasSurcharge) {
                    SurchargeTransactionReq surchargeTransactionReq = new SurchargeTransactionReq();
                    surchargeTransactionReq.setSurchargeAmount(surchargePrice);
                    surchargeTransactionReq.setSurchargeDetail(surchargeDetail);
                    surchargeTransactionReq.setSurchargeName(surchargeItemName);
                    surchargeTransactionReq.setConsignmentID(invoiceNo);
                    if (!surchargeDic.containsKey(invoiceNo)) {
                        surchargeDic.put(invoiceNo, new ArrayList<>());
                    }
                    surchargeDic.get(invoiceNo).add(surchargeTransactionReq);
                }
            }
        } catch (FileNotFoundException e) {
            return Resp.error(RespCodeEnums.PARAM_ERROR, "CSV File <" + filename + "> not found");
        } catch (IOException e) {
            return Resp.error(RespCodeEnums.PARAM_ERROR, "CSV File <" + filename + "> read error");
        } catch (IllegalArgumentException e) {
            return Resp.error(RespCodeEnums.PARAM_ERROR, "CSV File <" + filename +
                    "> format error. It may be because of the file is not in CSV format, or the csv file does not " +
                    "contain the required columns. For companyName <" + companyName +
                    ">, make sure it contains the following columns: " + COMPANY_FIELD_DICT.get(companyName));
        } catch (Exception e) {
            return Resp.error(RespCodeEnums.PARAM_ERROR, "CSV File <" + filename + "> unknown error");
        }

        actualAndSurchargeDicResp.setActualPriceDic(actualPriceDic);
        actualAndSurchargeDicResp.setSurchargeDic(surchargeDic);
        actualAndSurchargeDicResp.setRecordQty(Integer.toString(recordQty));
        return Resp.success(actualAndSurchargeDicResp);
    }

    /**
     * Check the file name is valid or not
     *
     * @param companyName company name
     * @param filename    file name
     * @return error message if the file name is invalid, success if the file name is valid
     */
    private static Resp<Boolean> checkFilename(String companyName, String filename) {
        if (!COMPANY_NAME_LIST.contains(companyName)) {
            return Resp.error(RespCodeEnums.PARAM_ERROR, "Company name <" + companyName + "> should be one of the following: " + COMPANY_NAME_LIST);
        }

        if (!Objects.requireNonNull(filename).startsWith(companyName + "_INV_")) {
            return Resp.error(RespCodeEnums.PARAM_ERROR, "File name <" + filename + "> should start with " + companyName + "_INV_");
        }

        if (!Objects.requireNonNull(filename).endsWith(".csv")) {
            return Resp.error(RespCodeEnums.PARAM_ERROR, "File name <" + filename + "> should end with .csv");
        }
        return Resp.success(true);
    }


    /**
     * Partition a list into sublists with given size to reduce DB burden
     *
     * @param list the list to be partitioned
     * @param size the size of each sublist
     * @return a list of sublists
     */
    public static List<List<String>> partitionList(List<String> list, int size) {
        return IntStream.iterate(0, i -> i + size)
                .limit((list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i, Math.min(i + size, list.size())))
                .collect(Collectors.toList());
    }

    /**
     * Get the shipping transactions from the DB using the consignmentID
     *
     * @param partitionConsignmentIDList the list of sublists of consignmentID to be queried
     * @param transactionService         the transaction service
     * @return a response of dictionary of shipping transactions with consignmentID as the key
     */
    public static Resp<Map<String, ShippingTransactionResp>> getShippingTransactionMap(List<List<String>> partitionConsignmentIDList, TransactionService transactionService) {
        Map<String, ShippingTransactionResp> shippingTransactionRespMap = new HashMap<>();

        // query the DB for each sublist of consignmentID and concatenate the results
        for (List<String> consignmentIDList : partitionConsignmentIDList) {
            Resp<Map<String, ShippingTransactionResp>> shippingTransactionRespListResp =
                    transactionService.getShippingTransactions(consignmentIDList);
            if (shippingTransactionRespListResp.isSuccess()) {
                shippingTransactionRespMap.putAll(shippingTransactionRespListResp.getData());
            } else {
                return Resp.error(RespCodeEnums.BIZ_ERROR, "Error while getting shipping transactions from DB using consignmentID");
            }
        }
        return Resp.success(shippingTransactionRespMap);
    }

    /**
     * Match the transactions from the CSV file with the transactions from the DB
     *
     * @param shippingTransactionRespMap the dictionary of shipping transaction information from the DB
     * @param actualPriceDic             the dictionary of actual price from the CSV file
     * @return the response containing the matched and unmatched transactions
     */
    public static ShippingChargeResp matchTransactions(Map<String, ShippingTransactionResp> shippingTransactionRespMap, Map<String, String> actualPriceDic) {

        ShippingChargeResp shippingChargeResp = new ShippingChargeResp();
        List<UnmatchedItem> unmatchedItems = new ArrayList<>();
        List<MatchedItem> matchedItems = new ArrayList<>();
        int unmatchedIndex = 0;
        int matchIndex = 0;
        BigDecimal totalActualPrice = BigDecimal.ZERO;
        BigDecimal totalRatingPrice = BigDecimal.ZERO;

        // Iterate through the actual price dictionary
        for (Map.Entry<String, String> entry : actualPriceDic.entrySet()) {
            String consignmentID = entry.getKey();
            String actualPriceStr = entry.getValue();
            ShippingTransactionResp shippingTransactionResp = shippingTransactionRespMap.get(consignmentID);

            // If the consignmentID is not found in the DB, add it to the unmatched list
            if (shippingTransactionResp == null) {
                BigDecimal actualPrice = BigDecimalUtils.toBigDecimal(actualPriceStr);
                UnmatchedItem unmatchedItem = new UnmatchedItem();
                unmatchedItem.setIndex(++unmatchedIndex);
                unmatchedItem.setReferenceID(consignmentID);
                unmatchedItem.setActualPrice(BigDecimalUtils.formatPrice(actualPrice).toString());
                unmatchedItems.add(unmatchedItem);

                // If the consignmentID is found in the DB, add it and its matched information to the matched list
            } else {
                MatchedItem matchedItem = new MatchedItem();
                matchedItem.setIndex(++matchIndex);
                matchedItem.setOrderID(shippingTransactionResp.getOrderId());
                matchedItem.setCompanyName(shippingTransactionResp.getClientCode());
                matchedItem.setConsignmentID(consignmentID);

                // calculate the difference and percentage between the actual price and rating price
                BigDecimal actualPrice = BigDecimalUtils.toBigDecimal(actualPriceStr);
                BigDecimal ratingPrice = shippingTransactionResp.getTotalPrice();
                BigDecimal diff = ratingPrice.subtract(actualPrice);
                BigDecimal percentage = diff.divide(actualPrice, 2, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100));

                totalActualPrice = totalActualPrice.add(actualPrice);
                totalRatingPrice = totalRatingPrice.add(ratingPrice);

                matchedItem.setRatingPrice(BigDecimalUtils.formatPrice(ratingPrice).toString());
                matchedItem.setActualPrice(BigDecimalUtils.formatPrice(actualPrice).toString());
                matchedItem.setDifference(BigDecimalUtils.formatPrice(diff).toString());
                matchedItem.setDifferencePercent(BigDecimalUtils.formatPrice(percentage).toString());
                matchedItems.add(matchedItem);
            }
        }

        // calculate the total difference and percentage
        BigDecimal totalDiff = totalRatingPrice.subtract(totalActualPrice);
        BigDecimal totalPercentage = totalDiff.divide(totalActualPrice, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));

        shippingChargeResp.setMatchedItems(matchedItems);
        shippingChargeResp.setUnmatchedItems(unmatchedItems);
        shippingChargeResp.setTotalActualPrice(BigDecimalUtils.formatPrice(totalActualPrice).toString());
        shippingChargeResp.setTotalRatingPrice(BigDecimalUtils.formatPrice(totalRatingPrice).toString());
        shippingChargeResp.setTotalDiff(BigDecimalUtils.formatPrice(totalDiff).toString());
        shippingChargeResp.setTotalDiffPercent(BigDecimalUtils.formatPrice(totalPercentage).toString());

        return shippingChargeResp;
    }

    /**
     * Get the matched surcharge list
     *
     * @param surchargeDic               the surcharge dictionary consists of consignmentID and surcharge list
     * @param shippingTransactionRespMap the shipping transaction dictionary from DB
     * @param isCharged                  whether the surcharge is charged or not, fetched from DB
     * @return the matched surcharge list
     */
    public static List<SurchargeTransactionReq> getSurchargeList(
            HashMap<String, List<SurchargeTransactionReq>> surchargeDic, Map<String, ShippingTransactionResp> shippingTransactionRespMap, boolean isCharged) {
        List<SurchargeTransactionReq> surchargeTransactionReqList = new ArrayList<>();
        int index = 0;

        // Iterate through the surcharge dictionary
        for (Map.Entry<String, List<SurchargeTransactionReq>> entry : surchargeDic.entrySet()) {
            String consignmentID = entry.getKey();
            List<SurchargeTransactionReq> surchargeTransactionReqList1 = entry.getValue();
            ShippingTransactionResp shippingTransactionResp = shippingTransactionRespMap.get(consignmentID);

            // Add the matched surcharge information to the surcharge list
            if (shippingTransactionResp != null) {
                for (SurchargeTransactionReq surchargeTransactionReq : surchargeTransactionReqList1) {
                    surchargeTransactionReq.setIndex(++index);
                    surchargeTransactionReq.setOrderID(shippingTransactionResp.getOrderId());
                    surchargeTransactionReq.setCompanyName(shippingTransactionResp.getClientCode());
                    surchargeTransactionReq.setConsignmentID(consignmentID);

                    // format the surcharge amount
                    surchargeTransactionReq.setSurchargeAmount(BigDecimalUtils.formatPrice(BigDecimalUtils.toBigDecimal(surchargeTransactionReq.getSurchargeAmount())).toString());

                    // surcharge detail and name are already set
                    surchargeTransactionReq.setIsCharged(isCharged ? "Yes" : "No");
                    surchargeTransactionReqList.add(surchargeTransactionReq);
                }
            }
        }
        return surchargeTransactionReqList;
    }

    public static Resp<ShippingChargeDataResp> getChargeData(String filename, String companyName, List<ShippingChargeReq.SurchargeChargeItem> surchargeChargeItems) {

        // check whether the filename matches the company name or not
        Resp<Boolean> filenameResp = checkFilename(companyName, filename);
        if (!filenameResp.isSuccess()) {
            return Resp.error(filenameResp.getCode(), filenameResp.getMessage());
        }

        HashSet<String> surchargedItemSet = new HashSet<>();
        HashMap<String, HashSet<String>> surchargeDetailDic = new HashMap<>();
        List<FreightChargeReq> freightChargeReqList = new ArrayList<>();
        BigDecimal surchargeTotal = BigDecimal.ZERO;
        String categoryCode = "Freight_" + companyName;

        for (ShippingChargeReq.SurchargeChargeItem surchargeChargeItem : surchargeChargeItems) {
            // add the surcharge item to the freight charge request list
            String clientCode = surchargeChargeItem.getClientCode();
            FreightChargeReq freightChargeReq = new FreightChargeReq();
            freightChargeReq.setClientCode(clientCode);
            freightChargeReq.setCategoryCode(categoryCode);
            freightChargeReq.setQuantity(surchargeChargeItem.getSurchargeAmount());
            freightChargeReq.setDescription(surchargeChargeItem.getSurchargeItem());
            freightChargeReqList.add(freightChargeReq);

            // add order id to the surcharge detail dictionary
            if (!surchargeDetailDic.containsKey(clientCode)) {
                surchargeDetailDic.put(clientCode, new HashSet<>());
            }
            surchargeDetailDic.get(clientCode).add(surchargeChargeItem.getOrderID());

            // add surcharge item to the surcharged item set
            surchargedItemSet.add(surchargeChargeItem.getSurchargeItem());

            // calculate the surcharge total
            surchargeTotal = surchargeTotal.add(BigDecimalUtils.toBigDecimal(surchargeChargeItem.getSurchargeAmount()));
        }

        String surchargedItemsStr = String.join(", ", surchargedItemSet);
        String surchargeDetailStr = JSONObject.toJSONString(surchargeDetailDic);

        ShippingChargeDataResp shippingChargeDataResp = new ShippingChargeDataResp();
        shippingChargeDataResp.setFreightChargeReqList(freightChargeReqList);
        shippingChargeDataResp.setSurchargedItems(surchargedItemsStr);
        shippingChargeDataResp.setSurchargedDetail(surchargeDetailStr);
        shippingChargeDataResp.setSurchargedAmount(BigDecimalUtils.formatPrice(surchargeTotal));

        return Resp.success(shippingChargeDataResp);
    }


}
