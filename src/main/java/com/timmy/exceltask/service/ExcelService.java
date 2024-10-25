package com.timmy.exceltask.service;

import com.timmy.exceltask.models.Book;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Service
public class ExcelService {

    public ByteArrayInputStream booksToExcel(List<Book> books) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Books");


            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Title");
            headerRow.createCell(2).setCellValue("Author");
            headerRow.createCell(3).setCellValue("Price");


            int rowIdx = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(book.getBookName());
                row.createCell(1).setCellValue(book.getBookName());
                row.createCell(2).setCellValue(book.getIsbn());
                row.createCell(3).setCellValue(book.getPageNumber());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }

    public ByteArrayInputStream convertListToExcel(List<?> objects) {
        if (objects == null || objects.isEmpty()) {
            throw new IllegalArgumentException("The object list is null or empty");
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Data");


            Field[] fields = objects.get(0).getClass().getDeclaredFields();


            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                headerRow.createCell(i).setCellValue(fields[i].getName());
            }


            int rowIdx = 1;
            for (Object obj : objects) {
                Row row = sheet.createRow(rowIdx++);
                for (int colIdx = 0; colIdx < fields.length; colIdx++) {
                    Object value = fields[colIdx].get(obj);
                    row.createCell(colIdx).setCellValue(value != null ? value.toString() : "");
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("Failed to create Excel file", e);
        }
    }

}
