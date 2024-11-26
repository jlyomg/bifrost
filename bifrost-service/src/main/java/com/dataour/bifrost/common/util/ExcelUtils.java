package com.dataour.bifrost.common.util;
import com.dataour.bifrost.common.enums.RespCodeEnums;
import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.common.enums.GroupEnum;
import com.dataour.bifrost.common.module.response.BreakdownDataResp;
import com.dataour.bifrost.common.module.response.BreakdownDataResp.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class ExcelUtils {
    public static final String FAILED_TO_GENERATE_EXCEL_FILE = "Failed to generate excel file.";
    private static ResourceLoader resourceLoader;

    private static final String LOGO_PATH = "classpath:template/csg-3pl-logo-1.png";

    public ExcelUtils(ResourceLoader resourceLoader) {
        ExcelUtils.resourceLoader = resourceLoader;
    }

    /**
     * Generate invoice excel file from breakdown data
     * @param breakdownData breakdown data
     * @return excel file
     */
    public static Resp<ByteArrayResource> generateInvoiceExcel(BreakdownDataResp breakdownData){

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            createCoverSheet(workbook, breakdownData);
            createSummarySheet(workbook, breakdownData);
            createInboundSheet(workbook, breakdownData);
            createStorageSheet(workbook, breakdownData);
            createOutboundSheet(workbook, breakdownData);
            createReturnSheet(workbook, breakdownData);
            createVASSheet(workbook, breakdownData);
            createPostageSheet(workbook, breakdownData);

            ByteArrayResource resource = convertWorkbookToByteArrayResource(workbook);
            if (resource == null) {
                return Resp.error(RespCodeEnums.BIZ_ERROR, FAILED_TO_GENERATE_EXCEL_FILE);
            } else {
                return Resp.success(resource);
            }
        } catch (Exception e) {
            log.error(FAILED_TO_GENERATE_EXCEL_FILE, e);
            return Resp.error(RespCodeEnums.BIZ_ERROR, FAILED_TO_GENERATE_EXCEL_FILE + " Error message: " + e.getMessage());
        }
    }

    /**
     * Create cover sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createCoverSheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData){
        XSSFSheet sheet = workbook.createSheet("Cover");

        Row row2 = sheet.createRow(1);
        row2.setHeightInPoints(80);
        Row row4 = sheet.createRow(3);
        row4.setHeightInPoints(30);
        Row row6 = sheet.createRow(5);
        row6.setHeightInPoints(20);
        Row row7 = sheet.createRow(6);
        row7.setHeightInPoints(40);

        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);

        // company name cell(B2)
        CellStyle companyCellStyle = workbook.createCellStyle();
        companyCellStyle.setAlignment(HorizontalAlignment.CENTER);
        companyCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        companyCellStyle.setWrapText(true);
        XSSFFont fontCompanyName = workbook.createFont();
        fontCompanyName.setFontHeightInPoints((short) 28);
        fontCompanyName.setBold(true);
        companyCellStyle.setFont(fontCompanyName);

        Cell cellB2 = row2.createCell(1);
        cellB2.setCellStyle(companyCellStyle);
        cellB2.setCellValue(breakdownData.getCompanyName());

        // invoice number cell and CSG3PL cell(B4, B7)
        CellStyle invoiceNumberCellStyle = workbook.createCellStyle();
        invoiceNumberCellStyle.setAlignment(HorizontalAlignment.CENTER);
        invoiceNumberCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont fontInvoiceNumber = workbook.createFont();
        fontInvoiceNumber.setFontHeightInPoints((short) 20);
        fontInvoiceNumber.setBold(true);
        invoiceNumberCellStyle.setFont(fontInvoiceNumber);

        Cell cellB4 = row4.createCell(1);
        cellB4.setCellStyle(invoiceNumberCellStyle);
        cellB4.setCellValue("Details of Invoice #" + breakdownData.getInvoiceNo());
        Cell cellB7 = row7.createCell(1);
        cellB7.setCellStyle(invoiceNumberCellStyle);
        cellB7.setCellValue("CSG3PL");

        // "Prepared By" cell (B6)
        CellStyle preparedByCellStyle = workbook.createCellStyle();
        preparedByCellStyle.setAlignment(HorizontalAlignment.CENTER);
        preparedByCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        preparedByCellStyle.setFont(boldFont);
        Cell cellB6 = row6.createCell(1);
        cellB6.setCellStyle(preparedByCellStyle);
        cellB6.setCellValue("Prepared By:");

        // B8 cell
        Cell cellB8 = sheet.createRow(7).createCell(1);
        cellB8.setCellValue("Values in this report are exclusive of GST");
        CellStyle cellB8Style = workbook.createCellStyle();
        cellB8Style.setAlignment(HorizontalAlignment.CENTER);
        cellB8Style.setVerticalAlignment(VerticalAlignment.CENTER);
        cellB8Style.setWrapText(true);
        cellB8.setCellStyle(cellB8Style);

        // last cell
        Cell cellC11 = sheet.createRow(10).createCell(2);
        cellC11.setCellValue("For additional detailed information refer to tabs below");
        CellStyle lastCellStyle = workbook.createCellStyle();
        lastCellStyle.setAlignment(HorizontalAlignment.CENTER);
        lastCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        lastCellStyle.setWrapText(true);
        cellC11.setCellStyle(lastCellStyle);

        // Set the logo
        Resource resource = resourceLoader.getResource(LOGO_PATH);
        byte[] imageBytes;
        try {
            imageBytes = IOUtils.toByteArray(resource.getInputStream());
            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
            CreationHelper helper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(9);
            anchor.setRow1(1);
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize(0.85,1.0);
        } catch (IOException e) {
            log.error("Failed to set logo for cover sheet", e);
        }

        // merge and set borders for cells at rows 1-8
        for (int i=1; i<=8; i++){
            CellRangeAddress cellRangeAddress = new CellRangeAddress(i, i, 1, 7);
            sheet.addMergedRegion(cellRangeAddress);
            switch (i){
                // row 1 for top border, 8 for bottom border, others for middle border
                case 1:
                    setTopBorderForMergedRegion(sheet, cellRangeAddress);
                    break;
                case 8:
                    setBottomBorderForMergedRegion(sheet, cellRangeAddress);
                    break;
                default:
                    setMiddleBorderForMergedRegion(sheet, cellRangeAddress);
                    break;
            }
        }

        // merge regions for the logo and the "additional information" cell
        sheet.addMergedRegion(new CellRangeAddress(10, 14, 2, 6));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 13));
    }

    /**
     * Create summary sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createSummarySheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData){
        XSSFSheet sheet = workbook.createSheet("Summary");
        SummaryData summaryData = breakdownData.getSummaryData();
        generateTitle(sheet, "Summary", summaryData.getTitle());

        int nextRow = 2;
        CellStyle boldCellStyle = getBoldCellStyle(sheet);
        Map<GroupEnum, Map<String, Content>> contentGroupMap = summaryData.getContent();
        for (GroupEnum group : GroupEnum.values()){
            Map<String, Content> contentMap = contentGroupMap.get(group);
            if (contentMap.size() > 0){
                Cell titleCell = sheet.createRow(nextRow++).createCell(0);
                titleCell.setCellValue(group.getName());
                titleCell.setCellStyle(boldCellStyle);

                // fill in the content
                fillInChargeItems(sheet, contentMap, nextRow, 1);
                nextRow += contentMap.size();
            }
        }

        // fill in the totals
        generateTotalWithName(summaryData.getSubTotal(), nextRow++, 3, sheet, "Subtotal:");
        generateTotalWithName(summaryData.getGst(), nextRow++, 3, sheet, "GST:");
        generateTotalWithName(summaryData.getTotal(), nextRow, 3, sheet, "Total(GST):");

        // adjust the column width automatically
        for (int i=0; i<summaryData.getTitle().length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create inbound sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createInboundSheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData){
        InboundData inboundData = breakdownData.getInboundData();
        if (inboundData.getContent().size() == 0){
            return;
        }

        String sheetName = GroupEnum.INBOUND.getName();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        generateTitle(sheet, sheetName, inboundData.getTitle());

        List<InboundData.InboundContent> inboundContent = new ArrayList<>(inboundData.getContent().values());
        sortInboundContent(inboundContent);
        int nextRow = 2;
        for (InboundData.InboundContent content: inboundContent){
            fillInContent(sheet, content.getClosedAtDate(), content.getPoId(), content.getContainerId(),
                    content.getChargeItems(), nextRow);
            nextRow += content.getChargeItems().size();
        }
        generateTotal(inboundData.getTotal(), nextRow, 5, sheet);

        // adjust the column width automatically
        for (int i=0; i<inboundData.getTitle().length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create outbound sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createStorageSheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData){
        StorageData storageData = breakdownData.getStorageData();
        if (storageData.getContent().size() == 0){
            return;
        }

        // set the title for the sheet, title contains all the dates and the required columns
        String sheetName = GroupEnum.STORAGE.getName();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        List<String> titleList = new ArrayList<>(storageData.getTitle());
        titleList.addAll(storageData.getSortedDate());
        titleList.addAll(Arrays.asList(storageData.getTitleLast()));

        String[] title = titleList.toArray(new String[0]);
        generateTitle(sheet, sheetName, title);
        List<String> storageItemNames = new ArrayList<>(storageData.getContent().keySet());
        Collections.sort(storageItemNames);
        int nextRow = 2;
        for (String itemName: storageItemNames){
            StorageData.StorageContent content = storageData.getContent().get(itemName);
            Row row = sheet.createRow(nextRow++);
            row.createCell(0).setCellValue(itemName);

            // fill in the daily data
            for (int i=0; i<storageData.getSortedDate().size(); i++){
                String Date = storageData.getSortedDate().get(i);
                fillInCellCount(row.createCell(i+1), content.getDailyData().getOrDefault(Date, 0));
            }

            // fill in the total data
            fillInCellCount(row.createCell(storageData.getSortedDate().size()+1), content.getCount());
            fillInCellPrice(row.createCell(storageData.getSortedDate().size()+2), content.getUnitPrice());
            fillInCellPrice(row.createCell(storageData.getSortedDate().size()+3), content.getTotal());
        }

        // fill in the total price
        generateTotal(storageData.getTotal(), nextRow, storageData.getSortedDate().size()+2, sheet);

        // adjust the column width automatically
        for (int i=0; i<title.length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create outbound sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createOutboundSheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData){
        OutboundData outboundData = breakdownData.getOutboundData();
        if (outboundData.getContent().size() == 0){
            return;
        }

        // set the title for the sheet
        String sheetName = GroupEnum.OUTBOUND.getName();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        generateTitle(sheet, sheetName, outboundData.getTitle());

        // fill in the content
        List<OutboundData.OutboundContent> outboundContent = new ArrayList<>(outboundData.getContent().values());
        sortOutboundContent(outboundContent);
        int nextRow = 2;
        for (OutboundData.OutboundContent content: outboundContent){
            fillInContent(sheet, content.getClosedAtDate(), content.getOrderId(), content.getClientOrderId(),
                    content.getChargeItems(), nextRow);
            nextRow += content.getChargeItems().size();
        }
        generateTotal(outboundData.getTotal(), nextRow, 5, sheet);

        // adjust the column width automatically
        for (int i=0; i<outboundData.getTitle().length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create return sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createReturnSheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData){
        ReturnData returnData = breakdownData.getReturnData();
        if (returnData.getContent().size() == 0){
            return;
        }

        // set the title for the sheet
        String sheetName = GroupEnum.RETURN.getName();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        generateTitle(sheet, sheetName, returnData.getTitle());

        // fill in the content
        List<ReturnData.ReturnContent> returnContent = new ArrayList<>(returnData.getContent().values());
        sortReturnContent(returnContent);
        int nextRow = 2;
        for (ReturnData.ReturnContent content: returnContent){
            fillInContent(sheet, content.getClosedAtDate(), content.getReturnOrderId(), content.getOrderId(),
                    content.getChargeItems(), nextRow);
            nextRow += content.getChargeItems().size();
        }
        generateTotal(returnData.getTotal(), nextRow, 5, sheet);

        // adjust the column width automatically
        for (int i=0; i<returnData.getTitle().length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create VAS sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createVASSheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData){
        VASData vasData = breakdownData.getVasData();
        if (vasData.getContent().size() == 0){
            return;
        }

        String sheetName = GroupEnum.VAS.getName();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        generateTitle(sheet, sheetName, vasData.getTitle());

        // VAS sheet does not have rows to merge, so fill in the content directly
        fillInChargeItems(sheet, vasData.getContent(), 2, 0);

        generateTotal(vasData.getTotal(), vasData.getContent().size()+2, 2, sheet);

        // adjust the column width automatically
        for (int i=0; i<vasData.getTitle().length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Create transport sheet for the breakdown data
     * @param workbook workbook
     * @param breakdownData breakdown data
     */
    private static void createPostageSheet(XSSFWorkbook workbook, BreakdownDataResp breakdownData) {
        TransportData transportData = breakdownData.getTransportData();
        if (transportData.getContent().size() == 0) {
            return;
        }

        String sheetName = GroupEnum.POSTAGE.getName();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        generateTitle(sheet, sheetName, transportData.getTitle());

        // fill in the content
        sortPostageContent(transportData.getContent());
        int nextRow = 2;
        for (TransportData.TransportContent content: transportData.getContent()) {
            Row row = sheet.createRow(nextRow++);
            row.createCell(0).setCellValue(content.getOrderId());
            row.createCell(1).setCellValue(content.getSuburb());
            row.createCell(2).setCellValue(content.getPostcode());
            row.createCell(3).setCellValue(content.getCarrier());
            row.createCell(4).setCellValue(content.getTrackingNo());
            fillInCellPrice(row.createCell(5), content.getPriceExGst());
            row.createCell(6).setCellValue(content.getOrderItems());
        }

        generateTotal(transportData.getTotal(), nextRow, 4, sheet);

        // adjust the column width automatically
        for (int i=0; i<transportData.getTitle().length; i++){
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Sort the inbound content by closedAtDate, poId and containerId, to make the content in Excel sheet in order
     * @param inboundContent inbound content
     */
    private static void sortInboundContent(List<InboundData.InboundContent> inboundContent){
        SimpleDateFormat dateFormat = DateUtils.dateFormat3;

        inboundContent.sort((content1, content2) -> {
            try {
                Date date1 = dateFormat.parse(content1.getClosedAtDate());
                Date date2 = dateFormat.parse(content2.getClosedAtDate());
                int dateCompare = date1.compareTo(date2);
                if (dateCompare != 0) {
                    return dateCompare;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Error parsing Date", e);
            }

            int poIdCompare = content1.getPoId().compareTo(content2.getPoId());
            if (poIdCompare != 0) {
                return poIdCompare;
            }

            return content1.getContainerId().compareTo(content2.getContainerId());
        });
    }

    /**
     * Sort the outbound content by closedAtDate, orderId and clientOrderId, to make the content in Excel sheet in order
     * @param outboundContent outbound content
     */
    private static void sortOutboundContent(List<OutboundData.OutboundContent> outboundContent){
        SimpleDateFormat dateFormat = DateUtils.dateFormat3;

        outboundContent.sort((content1, content2) -> {
            try {
                Date date1 = dateFormat.parse(content1.getClosedAtDate());
                Date date2 = dateFormat.parse(content2.getClosedAtDate());
                int dateCompare = date1.compareTo(date2);
                if (dateCompare != 0) {
                    return dateCompare;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Error parsing Date", e);
            }

            int orderIdCompare = content1.getOrderId().compareTo(content2.getOrderId());
            if (orderIdCompare != 0) {
                return orderIdCompare;
            }

            return content1.getClientOrderId().compareTo(content2.getClientOrderId());
        });
    }

    /**
     * Sort the return content by closedAtDate, returnOrderId and orderId, to make the content in Excel sheet in order
     * @param returnContent return content
     */
    private static void sortReturnContent(List<ReturnData.ReturnContent> returnContent){
        SimpleDateFormat dateFormat = DateUtils.dateFormat3;

        returnContent.sort((content1, content2) -> {
            try {
                Date date1 = dateFormat.parse(content1.getClosedAtDate());
                Date date2 = dateFormat.parse(content2.getClosedAtDate());
                int dateCompare = date1.compareTo(date2);
                if (dateCompare != 0) {
                    return dateCompare;
                }
            } catch (ParseException e) {
                throw new IllegalArgumentException("Error parsing Date", e);
            }

            int returnIdCompare = content1.getReturnOrderId().compareTo(content2.getReturnOrderId());
            if (returnIdCompare != 0) {
                return returnIdCompare;
            }

            return content1.getOrderId().compareTo(content2.getOrderId());
        });
    }

    /**
     * Sort the postage content by orderId, suburb and postcode, to make the content in Excel sheet in order
     * @param postageContent postage content
     */
    private static void sortPostageContent(List<TransportData.TransportContent> postageContent){
        postageContent.sort((content1, content2) -> {
            int orderIdCompare = content1.getOrderId().compareTo(content2.getOrderId());
            if (orderIdCompare != 0) {
                return orderIdCompare;
            }

            int suburbCompare = content1.getSuburb().compareTo(content2.getSuburb());
            if (suburbCompare != 0) {
                return suburbCompare;
            }

            return content1.getPostcode().compareTo(content2.getPostcode());
        });
    }

    /**
     * Fill in the content for the inbound/outbound/return sheet
     * @param sheet the sheet to fill in
     * @param Date the closedAtDate in the content
     * @param id1 the poId/orderId/returnOrderId in the content
     * @param id2 the containerId/clientOrderId/orderId in the content
     * @param content the charge items in the content
     * @param nextRow the next row to fill in
     */
    private static void fillInContent(XSSFSheet sheet, String Date, String id1, String id2,
                                      Map<String, Content> content, int nextRow){
        // merge the cells for the Date, id1 and id2
        if (content.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(nextRow, nextRow + content.size() - 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(nextRow, nextRow + content.size() - 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(nextRow, nextRow + content.size() - 1, 2, 2));
        }

        CellStyle centerCellStyle = sheet.getWorkbook().createCellStyle();
        centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        centerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // fill in the Date, id1 and id2
        Row row = sheet.createRow(nextRow);
        row.createCell(0).setCellValue(Date);
        row.getCell(0).setCellStyle(centerCellStyle);
        row.createCell(1).setCellValue(id1);
        row.getCell(1).setCellStyle(centerCellStyle);
        row.createCell(2).setCellValue(id2);
        row.getCell(2).setCellStyle(centerCellStyle);
        fillInChargeItems(sheet, content, nextRow, 3);
    }

    /**
     * Fill in the charge items in the sheet, the charge items are sorted in alphabetical order
     * @param sheet the sheet to fill in
     * @param content the charge items map
     * @param nextRow the row number to start filling in
     * @param colNum the column number for the charge item name
     */
    private static void fillInChargeItems(XSSFSheet sheet, Map<String, Content> content, int nextRow, int colNum){
        ArrayList<String> sortedChargeItems = new ArrayList<>(content.keySet());
        Collections.sort(sortedChargeItems);
        for (String chargeItem : sortedChargeItems){
            Row row = sheet.getRow(nextRow);
            if (row == null){
                row = sheet.createRow(nextRow);
            }
            Content item = content.get(chargeItem);
            row.createCell(colNum).setCellValue(chargeItem);
            fillInCellCount(row.createCell(colNum + 1), item.getCount());
            fillInCellPrice(row.createCell(colNum + 2), item.getUnitPrice());
            fillInCellPrice(row.createCell(colNum + 3), item.getTotal());
            nextRow++;
        }
    }

    /**
     * Fill in the cell with the price, formatted as $2.00
     * @param cell the cell to fill in
     * @param price the price to fill in
     */
    private static void fillInCellPrice(Cell cell, BigDecimal price){
        CellStyle currencyStyle = cell.getSheet().getWorkbook().createCellStyle();
        DataFormat df = cell.getSheet().getWorkbook().createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("\"$\"#,##0.00"));
        cell.setCellStyle(currencyStyle);
        cell.setCellValue(BigDecimalUtils.formatPrice(price).doubleValue());
    }

    /**
     * Fill in the cell with the count, formatted as 2 if the float only has its integer part,
     * or 2.00 if the float has its decimal part
     * @param cell the cell to fill in
     * @param count the count to fill in
     */
    private static void fillInCellCount(Cell cell, float count){
        double roundedCount = Math.round(count * 100.0) / 100.0;
        CellStyle countStyle = cell.getSheet().getWorkbook().createCellStyle();
        DataFormat df = cell.getSheet().getWorkbook().createDataFormat();
        boolean isInteger = count == (int) count;
        if (isInteger){
            countStyle.setDataFormat(df.getFormat("0"));
            cell.setCellValue((int) count);
        } else {
            countStyle.setDataFormat(df.getFormat("0.00"));
            cell.setCellValue(roundedCount);
        }
        cell.setCellStyle(countStyle);
    }

    /**
     * Generate the title for the sheet
     * @param sheet the sheet to fill in
     * @param header the header name for the sheet
     * @param title the title name list for the sheet
     */
    private static void generateTitle(XSSFSheet sheet, String header, String[] title){
        // sheet header
        Row row = sheet.createRow(0);
        CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
        XSSFFont font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 22);
        headerCellStyle.setFont(font);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Cell cell = row.createCell(0);
        cell.setCellStyle(headerCellStyle);
        cell.setCellValue(header);

        // sheet title
        row = sheet.createRow(1);
        CellStyle titleCellStyle = getBoldCellStyle(sheet);
        for (int i=0; i<title.length; i++){
            Cell cellTitle = row.createCell(i);
            cellTitle.setCellStyle(titleCellStyle);
            cellTitle.setCellValue(title[i]);
        }
    }

    /**
     * Generate the total price for the sheet
     * @param total the total price to fill in
     * @param rowNum the row number to fill in
     * @param colNum the column number to fill in
     * @param sheet the sheet to fill in
     */
    private static void generateTotal(BigDecimal total, int rowNum, int colNum, XSSFSheet sheet){
        generateTotalWithName(total, rowNum, colNum, sheet, "Total(ex. GST):");
    }

    /**
     * Generate the total price for the sheet with a specified name
     * @param total the total price to fill in
     * @param rowNum the row number to fill in
     * @param colNum the column number to fill in
     * @param sheet the sheet to fill in
     * @param name the name for the total price
     */
    private static void generateTotalWithName(BigDecimal total, int rowNum, int colNum, XSSFSheet sheet, String name){
        Row row = sheet.createRow(rowNum);
        CellStyle greyCellStyle = sheet.getWorkbook().createCellStyle();
        greyCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        greyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell cell = row.createCell(colNum);
        cell.setCellStyle(greyCellStyle);
        cell.setCellValue(name);
        cell = row.createCell(colNum+1);

        fillInCellPrice(cell, total);
        CellStyle currencyStyle = cell.getCellStyle();
        currencyStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        currencyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    }

    /**
     * Set the top border(left, top, right) for the merged region
     * @param sheet the sheet to set the border for
     * @param region the region to set the border for
     */
    private static void setTopBorderForMergedRegion(Sheet sheet, CellRangeAddress region){
        RegionUtil.setBorderTop(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THICK, region, sheet);
    }

    /**
     * Set the middle border(left, right) for the merged region
     * @param sheet the sheet to set the border for
     * @param region the region to set the border for
     */
    private static void setMiddleBorderForMergedRegion(Sheet sheet, CellRangeAddress region){
        RegionUtil.setBorderLeft(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THICK, region, sheet);
    }

    /**
     * Set the bottom border(left, bottom, right) for the merged region
     * @param sheet the sheet to set the border for
     * @param region the region to set the border for
     */
    private static void setBottomBorderForMergedRegion(Sheet sheet, CellRangeAddress region){
        RegionUtil.setBorderLeft(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THICK, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THICK, region, sheet);
    }

    /**
     * Get the bold cell style
     * @param sheet the sheet to get the cell style from
     * @return the bold cell style
     */
    private static CellStyle getBoldCellStyle(XSSFSheet sheet) {
        CellStyle titleCellStyle = sheet.getWorkbook().createCellStyle();
        XSSFFont titleFont = sheet.getWorkbook().createFont();
        titleFont.setBold(true);
        titleCellStyle.setFont(titleFont);
        return titleCellStyle;
    }

    /**
     * Convert the workbook to a ByteArrayResource
     * @param workbook the workbook to convert
     * @return the ByteArrayResource
     */
    private static ByteArrayResource convertWorkbookToByteArrayResource(XSSFWorkbook workbook) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos); // Write the workbook to the ByteArrayOutputStream
            byte[] bytes = bos.toByteArray(); // Convert the output stream to a byte array
            return new ByteArrayResource(bytes); // Create and return the ByteArrayResource
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
