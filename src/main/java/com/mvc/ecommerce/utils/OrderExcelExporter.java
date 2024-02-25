package com.mvc.ecommerce.utils;

import com.mvc.ecommerce.entity.Order;
import com.mvc.ecommerce.entity.OrderDetails;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class OrderExcelExporter {
    private List<Order> orderList;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public OrderExcelExporter(List<Order> orderList) {
        this.orderList = orderList;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Accounts");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Id", style);
        createCell(row, 1, "User-Order", style);
        createCell(row, 1, "User-Address", style);
        createCell(row, 2, "Create-Date", style);
        createCell(row, 3, "Order-Details", style);
        createCell(row, 4, "Status", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(13);
        style.setFont(font);

        for (Order order : orderList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, String.valueOf(order.getId()), style);
            createCell(row, columnCount++, order.getAccount().getUsername(), style);
            createCell(row, columnCount++, order.getAccount().getAddress(), style);
            createCell(row, columnCount++, order.getCreateDate().toString(), style);

            StringBuilder orderDetailsStringBuilder = new StringBuilder();
            for (OrderDetails orderDetail : order.getOrderDetails()) {
                orderDetailsStringBuilder.append(orderDetail.getProduct().getName())
                        .append(" - Price: ")
                        .append(orderDetail.getPrice())
                        .append(", Quantity: ")
                        .append(orderDetail.getQuantity())
                        .append("| ");
            }
            String orderDetails = orderDetailsStringBuilder.toString();
            createCell(row, columnCount++, orderDetails, style);

            createCell(row, columnCount++, order.getStatus(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
}
