package com.timmy.exceltask.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Service
public class PdfService {

    public ByteArrayInputStream convertListToPdf(List<?> objects) {
        if (objects == null || objects.isEmpty()) {
            throw new IllegalArgumentException("The object list is null or empty");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);


            Field[] fields = objects.get(0).getClass().getDeclaredFields();
            Table table = new Table(fields.length);


            for (Field field : fields) {
                table.addHeaderCell(new Cell().add(new Paragraph(field.getName())));
            }

            for (Object obj : objects) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    table.addCell(new Cell().add(new Paragraph(value != null ? value.toString() : "")));
                }
            }

            document.add(new Paragraph("Data Report").setTextAlignment(TextAlignment.CENTER));
            document.add(table);
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException("Failed to create PDF file", e);
        }
    }
}
