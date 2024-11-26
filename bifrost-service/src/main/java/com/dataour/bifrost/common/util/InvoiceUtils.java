package com.dataour.bifrost.common.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.dataour.bifrost.common.constant.BillCategoryConstants;
import com.dataour.bifrost.common.enums.GroupEnum;
import com.dataour.bifrost.common.enums.RespCodeEnums;
import com.dataour.bifrost.common.enums.SAPGroupEnum;
import com.dataour.bifrost.common.module.param.ClientUserInfo;
import com.dataour.bifrost.common.module.response.BreakdownDataResp;
import com.dataour.bifrost.common.module.response.BreakdownDataResp.*;
import com.dataour.bifrost.common.module.response.CSVLinerResp;
import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.domain.BillCategoryDO;
import com.dataour.bifrost.domain.InvoiceDO;
import com.dataour.bifrost.domain.InvoiceItemTransactionDO;
import com.dataour.bifrost.mapper.TransactionDOMapper;
import com.dataour.bifrost.processor.erp.ERPBaseProcessor;
import com.dataour.bifrost.processor.erp.ERPUserProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.alibaba.fastjson2.util.TypeUtils.toFloatValue;
import static com.dataour.bifrost.processor.erp.ERPBaseProcessor.CONSIGNMENT_URL_TENANT;

@Component
public class InvoiceUtils {

    private static final String CSV_HEADER = "DocNum,DocType,HandWritten,Printed,DocDate,DocDueDate,CardCode," + "CardName,Address,NumAtCard,DocCurrency,DocRate,DocTotal,Reference1,Reference2,Comments,JournalMemo\n" + "DocNum,DocType,Handwrtten,Printed,DocDate,DocDueDate,CardCode,CardName,Address,NumAtCard,DocCur," + "DocRate,DocTotal,Ref1,Ref2,Comments,JrnlMemo\n";

    private static final String CSV_LINER = "ParentKey,LineNum,ItemCode,ItemDescription,Quantity,ShipDate,Price,PriceAfterVAT,Currency,Rate,DiscountPercent,VendorNum,SerialNum,WarehouseCode,SalesPersonCode,CommisionPercent,LineTotal,AccountCode,UseBaseUnits,SupplierCatNum,CostingCode,ProjectCode,BarCode,VatGroup,U_INE_Cosutmerref\n" + "DocNum,LineNum,ItemCode,Dscription,Quantity,ShipDate,Price,PriceAfVAT,Currency,Rate,DiscPrcnt,VendorNum,SerialNum,WhsCode,SlpCode,Commission,LineTotal,AcctCode,UseBaseUn,SubCatNum,OcrCode,Project,CodeBars,VatGroup,U_INE_Cosutmerref\n";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String INVOICE_TEMPLATE_PATH = "classpath:template/service-invoice.html";

    private static ResourceLoader resourceLoader;

    public InvoiceUtils(ResourceLoader resourceLoader) {
        InvoiceUtils.resourceLoader = resourceLoader;
    }

    /**
     * @param invoices         The invoices to generate CSV
     * @param erpUserProcessor Utility class to get client user information
     * @return A string representation of the CSV header
     */
    public static String generateCSVHeader(List<InvoiceDO> invoices, ERPUserProcessor erpUserProcessor) {
        StringBuilder sb = new StringBuilder();
        sb.append(CSV_HEADER);
        int index = 1;

        for (InvoiceDO invoice : invoices) {
            String issueDate = invoice.getIssueDate() != null ? DATE_FORMAT.format(invoice.getIssueDate()) : "";
            String dueDate = invoice.getDueDate() != null ? DATE_FORMAT.format(invoice.getDueDate()) : "";
            String BPCode = erpUserProcessor.getClientUser(invoice.getClientCode()).getBpCode();
            String currencySymbol = erpUserProcessor.getClientUser(invoice.getClientCode()).getCurrencySymbol();

            sb.append(index++).append(",,") // DocType is blank
                    .append(",") // HandWritten is blank
                    .append(",") // Printed is blank
                    .append(issueDate).append(",").append(dueDate).append(",").append(BPCode).append(",").append(",") // CardName is blank
                    .append(",") // Address is blank
                    .append(invoice.getInvoiceNo()).append(",") // NumAtCard
                    .append(currencySymbol).append(",") // DocCurrency is AUD
                    .append(",")// DocRate is blank
                    .append(invoice.getTotalAmount()).append(",") // DocTotal is total_amount
                    .append(",") // Reference1 is blank
                    .append(",") // Reference2 is blank
                    .append(invoice.getInvoiceNo()).append(",") // Comments
                    .append(invoice.getInvoiceNo()).append("\n"); // JournalMemo
        }
        return sb.toString();
    }

    /**
     * Generate the data required to generate the csv liner
     *
     * @param transactions     The transactions to generate the csv liner
     * @param erpUserProcessor Utility class to get client user information
     * @param billCategoryMap  a map of bill category code to its sap group
     * @return A map of invoice id to csv liner response <br>
     * For the outer map, the key is the invoice id <br>
     * For the inner map, the key is the bill category code <br>
     * e.g. <br>
     * { <br>
     * 1: {"billCategoryCode1": {csvLinerResp1},"billCategoryCode2": {csvLinerResp2}}, <br>
     * 2: {"billCategoryCode1": {csvLinerResp3},"billCategoryCode2": {csvLinerResp4}} <br>
     * ... <br>
     * }
     */
    public static Map<Long, Map<String, CSVLinerResp>> generateCSVLinerRespMap(List<InvoiceItemTransactionDO> transactions, ERPUserProcessor erpUserProcessor, Map<String, BillCategoryDO> billCategoryMap) {
        Map<Long, Map<String, CSVLinerResp>> csvLinerRespIdMap = new HashMap<>();
        for (InvoiceItemTransactionDO transaction : transactions) {
            // get the csvLinerRespMap using the invoice id and the bill category code
            Long invoiceId = transaction.getInvoiceId();
            if (!csvLinerRespIdMap.containsKey(invoiceId)) {
                csvLinerRespIdMap.put(invoiceId, new HashMap<>());
            }
            Map<String, CSVLinerResp> csvLinerRespMap = csvLinerRespIdMap.get(invoiceId);
            if (!csvLinerRespMap.containsKey(transaction.getBillCategoryCode())) {
                csvLinerRespMap.put(transaction.getBillCategoryCode(), new CSVLinerResp());
            }

            // update the csvLinerRespMap with the transaction details
            CSVLinerResp csvLinerResp = csvLinerRespMap.get(transaction.getBillCategoryCode());
            csvLinerResp.setInvoiceId(invoiceId);
            csvLinerResp.setBillCategoryCode(transaction.getBillCategoryCode());
            csvLinerResp.setQuantity(transaction.getQuantity() + csvLinerResp.getQuantity());
            csvLinerResp.setCurrency(erpUserProcessor.getClientUser(transaction.getClientCode()).getCurrencySymbol());
            csvLinerResp.setTotalPrice(transaction.getTotalPrice().add(csvLinerResp.getTotalPrice()));
            SAPGroupEnum sapGroup = SAPGroupEnum.getFromName(billCategoryMap.get(transaction.getBillCategoryCode()).getSapGroup());

            // set the gl account based on the sap group
            if (sapGroup != null) {
                csvLinerResp.setGlAccount(sapGroup.getGlAccount());
            } else {
                csvLinerResp.setGlAccount("");
            }
        }
        return csvLinerRespIdMap;
    }

    /**
     * Generate the string representation of the csv liner
     *
     * @param invoice_ids       invoice id list
     * @param csvLinerRespIdMap a map of invoice id to csv liner response
     * @return A string representation of the csv liner
     */
    public static String generateCSVLiner(List<Long> invoice_ids, Map<Long, Map<String, CSVLinerResp>> csvLinerRespIdMap) {
        StringBuilder sb = new StringBuilder(CSV_LINER);
        int docNum = 1;
        for (Long invoiceId : invoice_ids) {
            if (!csvLinerRespIdMap.containsKey(invoiceId)) {
                docNum++;
                continue;
            }
            Map<String, CSVLinerResp> csvLinerRespMap = csvLinerRespIdMap.get(invoiceId);

            // sort the bill category codes to ensure the csv liner is in order
            List<String> sortedBillCategoryCodes = new ArrayList<>(csvLinerRespMap.keySet());
            Collections.sort(sortedBillCategoryCodes);
            for (String billCategoryCode : sortedBillCategoryCodes) {
                CSVLinerResp csvLinerResp = csvLinerRespMap.get(billCategoryCode);
                sb.append(docNum).append(",") // ParentKey
                        .append(",") // LineNum
                        .append(StringUtils.nvl(csvLinerResp.getBillCategoryCode(), "")).append(",") // ItemCode
                        .append(",") // ItemDescription
                        .append(StringUtils.formatFloat(csvLinerResp.getQuantity())).append(",") // Quantity
                        .append(",") // ShipDate
                        .append("0").append(",") // Price
                        .append(",") // PriceAfterVAT
                        .append(csvLinerResp.getCurrency()).append(",") // Currency
                        .append(",") // Rate
                        .append(",") // DiscountPercent
                        .append(",") // VendorNum
                        .append(",") // SerialNum
                        .append(",") // WarehouseCode
                        .append(",") // SalesPersonCode
                        .append(",") // CommisionPercent
                        .append(csvLinerResp.getTotalPrice()).append(",") // LineTotal
                        .append(StringUtils.nvl(csvLinerResp.getGlAccount(), "")).append(",") // AccountCode
                        .append(",") // UseBaseUnits
                        .append(",") // SupplierCatNum
                        .append(",") // CostingCode
                        .append(",") // ProjectCode
                        .append(",") // BarCode
                        .append("S1,") // VatGroup
                        .append(invoiceId).append("\n"); // U_INE_Cosutmerref
            }
            docNum++;
        }
        return sb.toString();
    }

    /**
     * Create a byte array representation using the csv liner and header
     *
     * @param csvHeader The csv header string
     * @param csvLiner  The csv liner string
     * @return A byte array representation of the zip file
     */
    public static byte[] createZipWithCsv(String csvHeader, String csvLiner) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry headerEntry = new ZipEntry("Header.csv");
            zos.putNextEntry(headerEntry);
            zos.write(csvHeader.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            ZipEntry linerEntry = new ZipEntry("Liner.csv");
            zos.putNextEntry(linerEntry);
            zos.write(csvLiner.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();
        }
    }

    /**
     * @param invoice          The invoice to generate the html
     * @param summary          A map of group enum to its total price
     * @param erpUserProcessor Utility class to get client user information
     * @return A string representation of the html file
     */
    public static String generateHtml(InvoiceDO invoice, Map<GroupEnum, BigDecimal> summary, ERPUserProcessor erpUserProcessor) throws Exception {
        String htmlTemplate = getHtmlTemplate();
        ClientUserInfo clientUserInfo = erpUserProcessor.getClientUser(invoice.getClientCode());

        // generate barcode for invoice id
        String invoiceId = invoice.getId().toString();
        String barcodePNG = generateBarcodePNG(invoice.getInvoiceNo());

        // generate invoice item table
        String itemsHtml = generateItemHtml(summary);

        htmlTemplate = htmlTemplate.replace("{{image}}", barcodePNG)

                // invoice information
                .replace("{{invoice.invoiceId}}", StringUtils.nvl(invoiceId, "")).replace("{{invoice.startDate}}", DateUtils.formatDate3(invoice.getPeriodFrom())).replace("{{invoice.endDate}}", DateUtils.formatDate3(invoice.getPeriodTo())).replace("{{invoice.invoiceDate}}", DateUtils.formatDate3(invoice.getIssueDate())).replace("{{invoice.dueDate}}", DateUtils.formatDate3(invoice.getDueDate())).replace("{{invoice.week}}", StringUtils.nvl(invoice.getWeek().toString(), "")).replace("{{invoice.year}}", StringUtils.nvl(invoice.getYear().toString(), ""))

                // client information
                .replace("{{profile.companyName}}", StringUtils.nvl(clientUserInfo.getCompanyName(), "")).replace("{{profile.bifrostAddress.address1}}", StringUtils.nvl(clientUserInfo.getbifrostAddress1(), "")).replace("{{profile.bifrostAddress.address2}}", StringUtils.nvl(clientUserInfo.getbifrostAddress2(), "")).replace("{{profile.bifrostAddress.unit}}", StringUtils.nvl(clientUserInfo.getbifrostAddressUnit(), "")).replace("{{profile.bifrostAddress.city}}", StringUtils.nvl(clientUserInfo.getbifrostAddressCity(), "")).replace("{{profile.bifrostAddress.state}}", StringUtils.nvl(clientUserInfo.getbifrostAddressState(), "")).replace("{{profile.bifrostAddress.country}}", StringUtils.nvl(clientUserInfo.getbifrostAddressCountry(), "")).replace("{{profile.bifrostAddress.postcode}}", StringUtils.nvl(clientUserInfo.getbifrostAddressPostcode(), "")).replace("{{user.fullName}}", StringUtils.nvl(clientUserInfo.getClientFullName(), "")).replace("{{profile.bifrostAddress.phone}}", StringUtils.nvl(clientUserInfo.getbifrostAddressPhone(), "")).replace("{{profile.bifrostAddress.email}}", StringUtils.nvl(clientUserInfo.getbifrostAddressEmail(), ""))

                // invoice items and price
                .replace("<!--{{items}}-->", itemsHtml).replace("{{total}}", StringUtils.nvl((invoice.getTotalPrice()).toString(), "")).replace("{{gst}}", StringUtils.nvl(invoice.getGst().toString(), "")).replace("{{totalGst}}", StringUtils.nvl(invoice.getTotalAmount().toString(), ""));

        return htmlTemplate;

    }

    /**
     * @param htmlContent HTML content in string
     * @return A byte array representing the PDF file
     */
    public static byte[] convertHtmlToPdfBytes(String htmlContent) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();

            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Convert the invoice to PDF ByteArrayResource
     *
     * @param invoice             The invoice to generate PDF
     * @param transactionDOMapper Mapper to fetch invoice items
     * @param erpUserProcessor    Utility class to get client user information
     * @param billCategoryMap     Map of bill category code to group
     * @return A ByteArrayResource representing the PDF file
     */
    public static ByteArrayResource getPDFResource(InvoiceDO invoice, TransactionDOMapper transactionDOMapper, ERPUserProcessor erpUserProcessor, Map<String, BillCategoryDO> billCategoryMap) throws Exception {
        // Fetch invoice transactions
        List<InvoiceItemTransactionDO> transactions = transactionDOMapper.getInvoiceItemTransactionDOByInvoiceId(invoice.getId());
        Map<GroupEnum, BigDecimal> summary = generateInvoiceGroupMap(transactions, billCategoryMap);

        // Generate HTML and convert to PDF bytes
        String invoiceHtml = InvoiceUtils.generateHtml(invoice, summary, erpUserProcessor);
        byte[] invoicePdfBytes = InvoiceUtils.convertHtmlToPdfBytes(invoiceHtml);
        return new ByteArrayResource(invoicePdfBytes);
    }

    /**
     * Create a zip file containing all the invoices in PDF format
     *
     * @param invoices            The invoices to generate PDF
     * @param transactionDOMapper Mapper to fetch invoice items
     * @param erpUserProcessor    Utility class to get client user information
     * @param billCategoryMap     Map of bill category code to group
     * @return A byte array representing the zip file
     */
    public static byte[] createZipWithPDFs(List<InvoiceDO> invoices, TransactionDOMapper transactionDOMapper, ERPUserProcessor erpUserProcessor, Map<String, BillCategoryDO> billCategoryMap) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (InvoiceDO invoice : invoices) {
                ByteArrayResource pdfResource = getPDFResource(invoice, transactionDOMapper, erpUserProcessor, billCategoryMap);
                zos.putNextEntry(new ZipEntry("invoice#" + invoice.getId() + ".pdf"));
                zos.write(pdfResource.getByteArray());
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Note: The HTML template is located in bifrost-service/src/main/resources/template/service-invoice.html
     *
     * @return A string representing the HTML template content
     */
    private static String getHtmlTemplate() throws IOException {
        Resource resource = resourceLoader.getResource(INVOICE_TEMPLATE_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * @param barcodeText barcode number
     * @return A string representing the barcode image in the format of data:image/png;base64,${base64Image}
     */
    private static String generateBarcodePNG(String barcodeText) throws Exception {
        Code128Bean barcodeGenerator = new Code128Bean();
        barcodeGenerator.setHeight(7.5f);
        barcodeGenerator.setModuleWidth(0.3);

        // disable the number below the barcode
        barcodeGenerator.setMsgPosition(HumanReadablePlacement.HRP_NONE);

        barcodeGenerator.doQuietZone(false);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(baos, "image/x-png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            barcodeGenerator.generateBarcode(canvasProvider, barcodeText);
            canvasProvider.finish();

            // Convert ByteArrayOutputStream to byte array
            byte[] byteArray = baos.toByteArray();

            // Convert byte array to Base64 string
            String base64Image = Base64.getEncoder().encodeToString(byteArray);

            return "data:image/png;base64," + base64Image;
        }
    }

    /**
     * @param summary Item list in the invoice
     * @return A string representing the HTML table rows for the invoice items
     */
    private static String generateItemHtml(Map<GroupEnum, BigDecimal> summary) {
        StringBuilder sb = new StringBuilder();

        // loop through invoice items and generate html table rows
        for (GroupEnum group : GroupEnum.values()) {
            if (summary.containsKey(group) && summary.get(group).compareTo(BigDecimal.ZERO) > 0) {
                String groupName = StringEscapeUtils.escapeHtml(group.getName());
                sb.append("<tr class=\"details\">").append("<td style=\"width: 66.66%;\">").append(groupName).append("</td>").append("<td style=\"text-align: right;\">$").append(BigDecimalUtils.formatPrice(summary.get(group))).append("</td>").append("</tr>");
            }
        }
        return sb.toString();
    }

    /**
     * Generate a map of invoice group with its total price
     *
     * @param transactions    list of invoice item transactions
     * @param billCategoryMap map of bill category code to group name
     * @return map of group to total price
     */
    public static Map<GroupEnum, BigDecimal> generateInvoiceGroupMap(List<InvoiceItemTransactionDO> transactions, Map<String, BillCategoryDO> billCategoryMap) {
        Map<GroupEnum, BigDecimal> summary = new HashMap<>();

        for (InvoiceItemTransactionDO transaction : transactions) {
            // admin group
            if (transaction.getBillCategoryCode().equals(GroupEnum.ADMIN.getCode())) {
                BigDecimal total = summary.getOrDefault(GroupEnum.ADMIN, BigDecimal.ZERO);
                summary.put(GroupEnum.ADMIN, total.add(transaction.getTotalPrice()));
                continue;
            }

            GroupEnum currentGroup = GroupEnum.getByDbName(billCategoryMap.get(transaction.getBillCategoryCode()).getGroup());
            if (currentGroup == null) {
                continue;
            }

            // only vas group can belong to other groups
            // if it has returnOrderId, it belongs to return group
            // if it has orderId, it belongs to outbound group
            // if it has poId, it belongs to inbound group
            if (currentGroup == GroupEnum.VAS) {
                String returnId = getAttributeFromTransaction(transaction, "returnOrderId");
                String orderId = transaction.getOrderId();
                String poId = transaction.getPoId();

                if (StringUtils.isNotEmpty(returnId)) {
                    currentGroup = GroupEnum.RETURN;
                } else if (StringUtils.isNotEmpty(orderId)) {
                    currentGroup = GroupEnum.OUTBOUND;
                } else if (StringUtils.isNotEmpty(poId)) {
                    currentGroup = GroupEnum.INBOUND;
                }
            }
            BigDecimal total = summary.getOrDefault(currentGroup, BigDecimal.ZERO);
            summary.put(currentGroup, total.add(transaction.getTotalPrice()));
        }
        return summary;
    }

    /**
     * Generate breakdown data for the invoice
     *
     * @param transactions    List of invoice item transactions
     * @param billCategoryMap Map of bill category code to bill category
     * @return A Resp object containing the breakdown data
     */
    public static Resp<BreakdownDataResp> generateBreakdownData(List<InvoiceItemTransactionDO> transactions, Map<String, BillCategoryDO> billCategoryMap) {
        BreakdownDataResp breakdownDataResp = new BreakdownDataResp();
        SummaryData summaryData = breakdownDataResp.getSummaryData();
        InboundData inboundData = breakdownDataResp.getInboundData();
        OutboundData outboundData = breakdownDataResp.getOutboundData();
        StorageData storageData = breakdownDataResp.getStorageData();
        ReturnData returnData = breakdownDataResp.getReturnData();
        VASData vasData = breakdownDataResp.getVasData();
        TransportData transportData = breakdownDataResp.getTransportData();
        List<String> consignmentIds = new ArrayList<>();
        boolean isAdminFeeAdded = false;
        String clientCode = null;
        for (InvoiceItemTransactionDO transaction : transactions) {
            if (clientCode == null) {
                clientCode = transaction.getClientCode();
            }
            if (transaction.getBillCategoryCode().equals(GroupEnum.ADMIN.getCode())) {
                processAdminData(summaryData, transaction, isAdminFeeAdded);
                isAdminFeeAdded = true;
                continue;
            }

            GroupEnum group = GroupEnum.getByDbName(billCategoryMap.get(transaction.getBillCategoryCode()).getGroup());
            if (group == null) {
                continue;
            }

            switch (group) {
                case INBOUND:
                    processInboundData(inboundData, summaryData, transaction);
                    break;
                case OUTBOUND:
                    processOutboundData(outboundData, summaryData, transaction);
                    break;
                case STORAGE:
                    processStorageData(storageData, transaction);
                    break;
                case RETURN:
                    processReturnData(returnData, summaryData, transaction, getAttributeFromTransaction(transaction, "returnOrderId"));
                    break;
                case VAS:
                    /*
                     VAS data can be either inbound, outbound, return, or vas data
                     if vas data has returnOrderId, it is a return data
                     else if it has orderId, it is outbound data
                     else if it has poId, it is inbound data
                     otherwise, it is vas data
                     priority: return > order > po > vas
                     */
                    String returnId = getAttributeFromTransaction(transaction, "returnOrderId");
                    String orderId = StringUtils.nvl(transaction.getOrderId(), "");
                    String poId = StringUtils.nvl(transaction.getPoId(), "");

                    if (!Objects.equals(returnId, "")) {
                        processReturnData(returnData, summaryData, transaction, returnId);
                    } else if (!Objects.equals(orderId, "")) {
                        processOutboundData(outboundData, summaryData, transaction);
                    } else if (!Objects.equals(poId, "")) {
                        processInboundData(inboundData, summaryData, transaction);
                    } else {
                        processVASData(vasData, transaction);
                    }
                    break;
                case POSTAGE:
                    String consignmentNo = transaction.getConsignmentNo();
                    if (consignmentNo != null && !consignmentNo.isEmpty()) {
                        consignmentIds.add(consignmentNo);
                    }
                    break;
            }
        }
        // process transport data if the consignmentIds is not empty
        if (!consignmentIds.isEmpty()) {
            Resp<Boolean> resp = processTransportData(summaryData, transportData, consignmentIds, clientCode);
            if (!resp.isSuccess()) {
                return Resp.error(resp.getCode(), resp.getMessage());
            }
        }
        // group the storage data to summaryData
        groupStorageData(storageData, summaryData);
        // update vasData to summaryData
        summaryData.getContent().put(GroupEnum.VAS, vasData.getContent());
        return Resp.success(breakdownDataResp);
    }

    /**
     * Process the admin data
     *
     * @param summaryData     the summary data
     * @param transaction     one record of transaction
     * @param isAdminFeeAdded boolean value to indicate whether the admin fee is in the summary data or not
     */
    private static void processAdminData(SummaryData summaryData, InvoiceItemTransactionDO transaction, boolean isAdminFeeAdded) {
        // admin data content only need to be added once, after that we only need to update the count and total
        if (!isAdminFeeAdded) {
            Content content = new Content();
            content.setUnitPrice(transaction.getUnitPrice());
            content.setItemName(BillCategoryConstants.ADMIN_ITEM_NAME);
            summaryData.getContent().get(GroupEnum.ADMIN).put(BillCategoryConstants.ADMIN_ITEM_NAME, content);
        }

        Content adminContent = summaryData.getContent().get(GroupEnum.ADMIN).get(BillCategoryConstants.ADMIN_ITEM_NAME);
        adminContent.setCount(adminContent.getCount() + toFloatValue(transaction.getQuantity()));
        adminContent.setTotal(adminContent.getTotal().add(transaction.getTotalPrice()));
    }

    /**
     * Process the inbound data by saving the transaction information in the inbound data and summary data's content map
     *
     * @param inboundData the inbound data
     * @param summaryData the summary data
     * @param transaction one record of transaction
     */
    private static void processInboundData(InboundData inboundData, SummaryData summaryData, InvoiceItemTransactionDO transaction) {
        Map<String, InboundData.InboundContent> content = inboundData.getContent();

        // If the content does not contain the PO ID, create a new InboundContent
        String poId = StringUtils.nvl(transaction.getPoId(), "");
        if (!content.containsKey(poId)) {
            InboundData.InboundContent inboundContent = new InboundData.InboundContent();
            String containerId = ParamUtils.getAttributeFromJson(transaction.getAttributes(), "containerId");
            inboundContent.setContainerId(containerId);
            inboundContent.setPoId(poId);
            inboundContent.setClosedAtDate(DateUtils.formatDate3(transaction.getGmtCreate()));
            content.put(poId, inboundContent);
        }

        Map<String, Content> chargeItems = content.get(poId).getChargeItems();
        updateChargeItems(transaction, chargeItems, summaryData.getContent().get(GroupEnum.INBOUND));

        inboundData.setTotal(inboundData.getTotal().add(transaction.getTotalPrice()));
    }

    /**
     * Process the outbound data
     *
     * @param outboundData the outbound data
     * @param summaryData  the summary data
     * @param transaction  one record of transaction
     */
    private static void processOutboundData(OutboundData outboundData, SummaryData summaryData, InvoiceItemTransactionDO transaction) {
        Map<String, OutboundData.OutboundContent> content = outboundData.getContent();

        // If the content does not contain the order ID, create a new OutboundContent
        if (!content.containsKey(transaction.getOrderId())) {
            OutboundData.OutboundContent outboundContent = new OutboundData.OutboundContent();
            String clientOrderId = ParamUtils.getAttributeFromJson(transaction.getAttributes(), "clientOrderId");

            outboundContent.setClientOrderId(clientOrderId);
            outboundContent.setOrderId(transaction.getOrderId());
            outboundContent.setClosedAtDate(DateUtils.formatDate3(transaction.getGmtCreate()));
            content.put(transaction.getOrderId(), outboundContent);
        }

        Map<String, Content> chargeItems = content.get(transaction.getOrderId()).getChargeItems();
        updateChargeItems(transaction, chargeItems, summaryData.getContent().get(GroupEnum.OUTBOUND));

        outboundData.setTotal(outboundData.getTotal().add(transaction.getTotalPrice()));
    }

    /**
     * Process the return data
     *
     * @param returnData  the return data
     * @param summaryData the summary data
     * @param transaction one record of transaction
     * @param returnId    the return id in the attributes
     */
    private static void processReturnData(ReturnData returnData, SummaryData summaryData, InvoiceItemTransactionDO transaction, String returnId) {
        Map<String, ReturnData.ReturnContent> content = returnData.getContent();
        String orderId = StringUtils.nvl(transaction.getOrderId(), "");

        // If the content does not contain the return ID, create a new ReturnContent
        if (!content.containsKey(returnId)) {
            ReturnData.ReturnContent returnContent = new ReturnData.ReturnContent();

            returnContent.setOrderId(orderId);
            returnContent.setReturnOrderId(returnId);
            returnContent.setClosedAtDate(DateUtils.formatDate3(transaction.getGmtCreate()));
            content.put(returnId, returnContent);
        }

        Map<String, Content> chargeItems = content.get(returnId).getChargeItems();
        updateChargeItems(transaction, chargeItems, summaryData.getContent().get(GroupEnum.RETURN));

        returnData.setTotal(returnData.getTotal().add(transaction.getTotalPrice()));
    }

    /**
     * Use the transaction information to update the chargeItems dictionary
     *
     * @param transaction        one record of transaction
     * @param chargeItems        a map of charge item name to its content
     * @param summaryChargeItems the charge item map in summary data
     */
    private static void updateChargeItems(InvoiceItemTransactionDO transaction, Map<String, Content> chargeItems, Map<String, Content> summaryChargeItems) {
        // If the chargeItems does not contain the bill category name, create a new Content
        if (!chargeItems.containsKey(transaction.getBillCategoryEnName())) {
            Content chargeItem = new Content();
            chargeItem.setItemName(transaction.getBillCategoryEnName());
            chargeItem.setUnitPrice(transaction.getUnitPrice());
            chargeItems.put(transaction.getBillCategoryEnName(), chargeItem);
        }
        if (!summaryChargeItems.containsKey(transaction.getBillCategoryEnName())) {
            Content chargeItem = new Content();
            chargeItem.setItemName(transaction.getBillCategoryEnName());
            chargeItem.setUnitPrice(transaction.getUnitPrice());
            summaryChargeItems.put(transaction.getBillCategoryEnName(), chargeItem);
        }

        // update the chargeItem
        Content chargeItem = chargeItems.get(transaction.getBillCategoryEnName());
        chargeItem.setTotal(chargeItem.getTotal().add(transaction.getTotalPrice()));
        chargeItem.setCount(chargeItem.getCount() + transaction.getQuantity());

        Content summaryChargeItem = summaryChargeItems.get(transaction.getBillCategoryEnName());
        summaryChargeItem.setTotal(summaryChargeItem.getTotal().add(transaction.getTotalPrice()));
        summaryChargeItem.setCount(summaryChargeItem.getCount() + transaction.getQuantity());
    }

    /**
     * Process the storage data by only saving them, grouping data will be performed by method groupStorageData
     *
     * @param storageData the storage data
     * @param transaction one record of transaction
     */
    private static void processStorageData(StorageData storageData, InvoiceItemTransactionDO transaction) {
        Map<String, StorageData.StorageContent> content = storageData.getContent();
        String itemName = transaction.getBillCategoryEnName();

        // if storageData does not contain the item name, create a new StorageContent
        if (!content.containsKey(itemName)) {
            StorageData.StorageContent storageContent = new StorageData.StorageContent();
            storageContent.setItemName(itemName);
            storageContent.setUnitPrice(transaction.getUnitPrice());
            content.put(itemName, storageContent);
        }

        StorageData.StorageContent storageContent = content.get(itemName);
        Map<String, Integer> dailyData = storageContent.getDailyData();
        String Date = DateUtils.formatDate3(transaction.getGmtCreate());
        if (!dailyData.containsKey(Date)) {
            dailyData.put(Date, 0);
        }

        // update the count in the dailyData and the total in the storage and summary data
        dailyData.put(Date, (int) (dailyData.get(Date) + transaction.getQuantity()));
        storageContent.setTotal(storageContent.getTotal().add(transaction.getTotalPrice()));
        storageData.setTotal(storageData.getTotal().add(transaction.getTotalPrice()));
    }

    /**
     * Process the vas data
     *
     * @param vasData     the vas data
     * @param transaction one record of transaction
     */
    private static void processVASData(VASData vasData, InvoiceItemTransactionDO transaction) {
        Map<String, Content> content = vasData.getContent();
        String itemName = transaction.getBillCategoryEnName();

        // if vasData does not contain the item name, create a new Content
        if (!content.containsKey(itemName)) {
            Content vasContent = new Content();
            vasContent.setItemName(itemName);
            vasContent.setUnitPrice(transaction.getUnitPrice());
            content.put(itemName, vasContent);
        }

        // update the count and total in the content
        Content vasContent = content.get(itemName);
        vasContent.setTotal(vasContent.getTotal().add(transaction.getTotalPrice()));
        vasContent.setCount(vasContent.getCount() + transaction.getQuantity());

        // update the total in the vasData
        vasData.setTotal(vasData.getTotal().add(transaction.getTotalPrice()));
    }

    /**
     * Process the transport(postage) data by searching transport information via ERP api
     *
     * @param summaryData    the summary data
     * @param transportData  the transport data
     * @param consignmentIds A list of consignment ids
     * @return a Resp object with a boolean value indicating whether the process is successful
     */
    private static Resp<Boolean> processTransportData(SummaryData summaryData, TransportData transportData, List<String> consignmentIds, String clientCode) {
        String params = getConsignmentIdStr(consignmentIds);
        ERPBaseProcessor erpBaseProcessor = (ERPBaseProcessor) SpringContextUtils.getBean("erpBaseProcessor");
        if (erpBaseProcessor == null) {
            return Resp.error(RespCodeEnums.BIZ_ERROR, "Failed to get consignment data from ERP's API");
        }
        Resp<String> response = HttpClientUtils.post(erpBaseProcessor.getFullUrlWithTenant(CONSIGNMENT_URL_TENANT, clientCode), params, "UTF-8");
        HashSet<String> consignmentIdSet = new HashSet<>(consignmentIds);
        HashSet<String> consignmentIdRespSet = new HashSet<>();
        if (!response.isSuccess()) {
            return Resp.error(RespCodeEnums.BIZ_ERROR, "Failed to get consignment data from ERP's API");
        }
        String responseMessage = response.getMessage();
        if (responseMessage.startsWith("{")) {
            // response is a JSON object with error message
            JSONObject jsonObject = JSONObject.parseObject(responseMessage);
            String message = jsonObject.getString("message");
            if (message != null) {
                return Resp.error(RespCodeEnums.BIZ_ERROR, "Error message from ERP API: " + message);
            } else {
                return Resp.error(RespCodeEnums.BIZ_ERROR, "Unknown error from ERP API");
            }
        }
        String data = response.getData();
        if (DataUtils.dataEmpty(data)) {
            return Resp.success(true);
        }
        // response is a JSON array with consignment data
        JSONArray jsonArray = JSONArray.parseArray(data);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject consignment = jsonArray.getJSONObject(i);
            String orderId = StringUtils.nvl(consignment.getString("orderNo"), "");
            String suburb = StringUtils.nvl(consignment.getString("suburb"), "");
            String postcode = StringUtils.nvl(consignment.getString("postCode"), "");
            String carrier = StringUtils.nvl(consignment.getString("carrier"), "");
            String trackingNo = StringUtils.nvl(consignment.getString("trackingNo"), "");
            String priceExGsrStr = StringUtils.nvl(consignment.getString("priceExGST"), "0");
            JSONArray items = consignment.getJSONArray("item");

            StringBuilder itemSb = new StringBuilder();
            for (int j = 0; j < items.size(); j++) {
                JSONObject item = items.getJSONObject(j);
                String sku = StringUtils.nvl(item.getString("sku"), "");
                String quantityStr = StringUtils.nvl(item.getString("quantity"), "0");
                itemSb.append(sku).append(" * ").append(quantityStr).append(" | ");
            }
            // remove the last " | "
            if (items.size() > 0) {
                itemSb.delete(itemSb.length() - 3, itemSb.length());
            }

            String orderItems = itemSb.toString();

            TransportData.TransportContent transportContent = new TransportData.TransportContent();
            transportContent.setOrderId(orderId);
            transportContent.setSuburb(suburb);
            transportContent.setPostcode(postcode);
            transportContent.setCarrier(carrier);
            transportContent.setTrackingNo(trackingNo);
            transportContent.setOrderItems(orderItems);

            BigDecimal priceExGst = BigDecimalUtils.toBigDecimal(priceExGsrStr);
            transportContent.setPriceExGst(BigDecimalUtils.formatPrice(priceExGst));

            // save the transportContent to transportData and update the summaryData
            transportData.getContent().add(transportContent);
            consignmentIdRespSet.add(trackingNo);
            transportData.setTotal(transportData.getTotal().add(priceExGst));

            Map<String, Content> chargeItems = summaryData.getContent().get(GroupEnum.POSTAGE);
            String itemName = carrier + " Freight Charge";

            if (!chargeItems.containsKey(itemName)) {
                Content chargeItem = new Content();
                chargeItem.setItemName(itemName);
                chargeItem.setCount(1f);
                chargeItem.setUnitPrice(BigDecimal.ZERO);
                chargeItems.put(itemName, chargeItem);
            }

            Content chargeItem = chargeItems.get(itemName);
            chargeItem.setTotal(chargeItem.getTotal().add(priceExGst));
            chargeItem.setUnitPrice(chargeItem.getUnitPrice().add(priceExGst));
        }

        // get the difference between consignmentIds and consignmentIdRespSet,
        // then add these consignmentIds without information to the transportData
        consignmentIdSet.removeAll(consignmentIdRespSet);

        if (consignmentIdSet.size() > 0) {
            for (String consignmentId : consignmentIdSet) {
                TransportData.TransportContent transportContent = new TransportData.TransportContent();
                transportContent.setOrderId("");
                transportContent.setSuburb("");
                transportContent.setPostcode("");
                transportContent.setCarrier("");
                transportContent.setTrackingNo(consignmentId);
                transportContent.setOrderItems("");
                transportContent.setPriceExGst(BigDecimal.ZERO);
                transportData.getContent().add(transportContent);
            }
        }

        return Resp.success(true);
    }

    /**
     * Convert the consignmentIds to a string
     *
     * @param consignmentIds the consignmentId list to be converted
     * @return the string of consignmentIds
     */
    private static String getConsignmentIdStr(List<String> consignmentIds) {
        StringBuilder sb = new StringBuilder("[");
        for (String consignmentId : consignmentIds) {
            sb.append("\"").append(consignmentId).append("\" ,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Process the storageData and update the summaryData
     *
     * @param storageData the storageData to be processed
     * @param summaryData the summaryData to be updated
     */
    private static void groupStorageData(StorageData storageData, SummaryData summaryData) {
        // get all the days from the dailyData in storageData
        HashSet<String> daySet = new HashSet<>();
        for (StorageData.StorageContent storageContent : storageData.getContent().values()) {
            daySet.addAll(storageContent.getDailyData().keySet());
        }

        // calculate the average count in dailyData
        if (!daySet.isEmpty()) {
            for (StorageData.StorageContent storageContent : storageData.getContent().values()) {
                float averageCount = (float) storageContent.getDailyData().values().stream().mapToInt(Integer::intValue).sum() / daySet.size();
                storageContent.setCount(averageCount);
                summaryData.getContent().get(GroupEnum.STORAGE).put(storageContent.getItemName(), storageContent);
            }
        }

        List<String> dayList = new ArrayList<>(daySet);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Sort the dayList
        dayList.sort((date1, date2) -> {
            LocalDate localDate1 = LocalDate.parse(date1, formatter);
            LocalDate localDate2 = LocalDate.parse(date2, formatter);
            return localDate1.compareTo(localDate2);
        });
        storageData.setSortedDate(dayList);
    }

    /**
     * Get the attribute from the transaction, return empty string if the attribute is not found
     *
     * @param transaction the transaction to get the attribute from
     * @param field       the field name of the attribute
     * @return the value of the attribute, or empty string if the attribute is not found
     */
    private static String getAttributeFromTransaction(InvoiceItemTransactionDO transaction, String field) {
        String attributes = transaction.getAttributes();
        if (attributes == null || attributes.length() == 0) {
            return "";
        }

        JSONObject jsonObject = JSONObject.parseObject(attributes);
        return StringUtils.nvl(jsonObject.getString(field), "");
    }

}
