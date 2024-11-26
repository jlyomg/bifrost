package com.dataour.bifrost.common.util;

import com.dataour.bifrost.common.module.param.ClientUserInfo;
import com.dataour.bifrost.domain.InvoiceDO;
import com.dataour.bifrost.processor.erp.ERPUserProcessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class InvoiceStatementUtils {

    private static ResourceLoader resourceLoader;

    public InvoiceStatementUtils(ResourceLoader resourceLoader) {
        InvoiceStatementUtils.resourceLoader = resourceLoader;
    }

    private static final String INVOICE_STATEMENT_TEMPLATE_PATH = "classpath:template/service-invoiceStatement.html";

    public static BigDecimal totalOutstandingAmount;

    /**
     * Note: The HTML template is located in bifrost-service/src/main/resources/template/service-invoiceStatement.html
     * @return A string representing the HTML template content
     */
    private static String getHtmlTemplate() throws IOException {
        Resource resource = resourceLoader.getResource(INVOICE_STATEMENT_TEMPLATE_PATH);
        try (InputStream inputStream = resource.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * Generate the HTML content for the invoice statement table
     * @param invoices A list of invoices
     * @return A string representing the HTML content for the invoice statement table
     */
    private static String generateInvoiceHtml(List<InvoiceDO> invoices){
        StringBuilder sb = new StringBuilder();
        totalOutstandingAmount = BigDecimal.ZERO;
        for (InvoiceDO invoice : invoices) {
            // outstanding amount = total amount - paid amount
            BigDecimal totalAmount = StringUtils.nvl(invoice.getTotalAmount(), BigDecimal.ZERO);
            BigDecimal paidAmount = StringUtils.nvl(invoice.getPaidAmount(), BigDecimal.ZERO);
            BigDecimal outstandingAmount = totalAmount.subtract(paidAmount);
            totalOutstandingAmount = totalOutstandingAmount.add(outstandingAmount);

            sb.append("<tr class=\"details\">");
            sb.append("<td style=\"text-align: center;\">#").append(StringUtils.nvl(invoice.getInvoiceNo(), "")).append("</td>");
            sb.append("<td style=\"text-align: center;\">").append(DateUtils.formatDate3(invoice.getIssueDate())).append("</td>");
            sb.append("<td style=\"text-align: center;\">").append(DateUtils.formatDate3(invoice.getDueDate())).append("</td>");
            sb.append("<td style=\"text-align: right;\">$").append(totalAmount).append("</td>");
            sb.append("<td style=\"text-align: right;\">$").append(outstandingAmount).append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }

    /**
     * Generate the HTML content for the invoice statement
     * @param invoices A list of invoices
     * @param clientCode The client code
     * @param erpUserProcessor The client user util
     * @return A string representing the HTML content for the invoice statement
     */
    public static String generateHtml(List<InvoiceDO> invoices, String clientCode, ERPUserProcessor erpUserProcessor) throws IOException {
        String htmlTemplate = getHtmlTemplate();
        String invoiceHtml = generateInvoiceHtml(invoices);
        ClientUserInfo clientUserInfo = erpUserProcessor.getClientUser(clientCode);
        String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        htmlTemplate = htmlTemplate
                .replace("{{statement.statementDate}}", today)
                .replace("{{profile.companyName}}", StringUtils.nvl(clientUserInfo.getCompanyName(), ""))
                .replace("{{CurrencySymbol}}", StringUtils.nvl(clientUserInfo.getCurrencySymbol(), ""))

                .replace("{{profile.bifrostAddress.unit}}", StringUtils.nvl(clientUserInfo.getbifrostAddressUnit(), ""))
                .replace("{{profile.bifrostAddress.address1}}", StringUtils.nvl(clientUserInfo.getbifrostAddress1(), ""))
                .replace("{{profile.bifrostAddress.address2}}", StringUtils.nvl(clientUserInfo.getbifrostAddress2(), ""))
                .replace("{{profile.bifrostAddress.city}}", StringUtils.nvl(clientUserInfo.getbifrostAddressCity(), ""))
                .replace("{{profile.bifrostAddress.state}}", StringUtils.nvl(clientUserInfo.getbifrostAddressState(), ""))
                .replace("{{profile.bifrostAddress.country}}", StringUtils.nvl(clientUserInfo.getbifrostAddressCountry(), ""))
                .replace("{{profile.bifrostAddress.postcode}}", StringUtils.nvl(clientUserInfo.getbifrostAddressPostcode(), ""))
                .replace("{{user.fullName}}", StringUtils.nvl(clientUserInfo.getClientFullName(), ""))
                .replace("{{profile.bifrostAddress.phone}}", StringUtils.nvl(clientUserInfo.getbifrostAddressPhone(), ""))
                .replace("{{profile.bifrostAddress.email}}", StringUtils.nvl(clientUserInfo.getbifrostAddressEmail(), ""))

                .replace("{{#each invoices}}", invoiceHtml)
                .replace("{{statement.totalAmount}}", totalOutstandingAmount.toString());

        return htmlTemplate;
    }

    /**
     * Accepts a list of pdf bytes and generate a zip file containing the pdf files
     * @param pdfBytesList A list of pdf bytes
     * @param clientCodeList A list of client codes
     * @return A byte array representing the zip file
     */
    public static byte[] generateZipPdfBytes(List<byte[]> pdfBytesList, List<String> clientCodeList){
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (int i = 0; i < pdfBytesList.size(); i++) {
                byte[] pdfBytes = pdfBytesList.get(i);
                String clientCode = clientCodeList.get(i);
                ZipEntry zipEntry = new ZipEntry("Statement#" + clientCode + ".pdf");
                zos.putNextEntry(zipEntry);
                zos.write(pdfBytes);
                zos.closeEntry();
            }
            zos.finish();
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
