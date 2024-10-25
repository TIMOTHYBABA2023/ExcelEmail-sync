package com.timmy.exceltask.controllers;

import com.timmy.exceltask.models.Book;
import com.timmy.exceltask.models.Person;
import com.timmy.exceltask.service.EmailService;
import com.timmy.exceltask.service.ExcelService;
import com.timmy.exceltask.service.PdfService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {


    private final ExcelService excelService;
    private final EmailService emailService;
    private final PdfService pdfService;

    public ExcelController(ExcelService excelService, EmailService emailService, PdfService pdfService) {
        this.excelService = excelService;
        this.emailService = emailService;
        this.pdfService = pdfService;
    }


    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {
        List<Book> dataList = Arrays.asList(
            new Book("Alice", "Maths fundamentals", "iso01", 56),
            new Book("Bob", "Maths essentials", "iso02", 12),
            new Book("Pop", "Maths formulas", "iso03", 4),
            new Book("Henry", "Maths basics", "iso04", 6)
        );

        ByteArrayInputStream in = excelService.booksToExcel(dataList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=data.xlsx");


        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(in.readAllBytes());
    }


    @GetMapping("/downloadObject")
    public ResponseEntity<byte[]> downloadExcelSheet() {
        List<Person> dataList = Arrays.asList(
                new Person("Timothy", "Baba", "tim@gmail.com", 23445),
                new Person("David", "Awodi", "dac@gmail.com", 3465),
                new Person("Glory", "Okay", "glo@gmail.com", 6787),
                new Person("Ojima", "Nice", "oji@gmail.com", 29389)
        );


        ByteArrayInputStream in = excelService.convertListToExcel(dataList);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=books.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());

    }

    @GetMapping("/sendExcelByEmail")
    public ResponseEntity<String> sendExcelByEmail(@RequestParam String email) {

        List<Book> books = Arrays.asList(
                new Book("Alice", "Maths fundamentals", "iso01", 56),
                new Book("Bob", "Maths essentials", "iso02", 12),
                new Book("Pop", "Maths formulas", "iso03", 4),
                new Book("Henry", "Maths basics", "iso04", 6)
        );


        ByteArrayInputStream in = excelService.convertListToExcel(books);


        try {
            emailService.sendEmailWithAttachment(
                    email,
                    "Your Excel File",
                    "Please find the attached Excel file.",
                    in,
                    "books.xlsx"
            );
            return ResponseEntity.ok("Email sent successfully");
        } catch (IOException | MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }

    @GetMapping("/sendPdf")
    public ResponseEntity<String> sendPdfByEmail(@RequestParam String email) {
        List<Book> books = Arrays.asList(
                new Book("Alice", "Maths fundamentals", "iso01", 56),
                new Book("Bob", "Maths essentials", "iso02", 12),
                new Book("Pop", "Maths formulas", "iso03", 4),
                new Book("Henry", "Maths basics", "iso04", 6)
        );

        try {
            ByteArrayInputStream pdfStream = pdfService.convertListToPdf(books);
            emailService.sendEmailWithAttachment(
                    email,
                    "Your PDF File",
                    "Please find the attached PDF file.",
                    pdfStream,
                    "books.pdf"
            );
            return ResponseEntity.ok("Email sent successfully");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/send-pdf")
    public ResponseEntity<String> sendPdfByEmail(
            @RequestParam String toEmail,
            @RequestParam(required = false) String sortOption) {
        try {
            List<Book> dataList = Arrays.asList(
                    new Book("Alice", "Maths fundamentals", "iso01", 56),
                    new Book("Bob", "Maths essentials", "iso088", 12),
                    new Book("Pop", "Maths formulas", "iso036", 4),
                    new Book("Henry", "Maths basics", "iso0304", 6)
            );

            emailService.sendPdfByEmail(toEmail, dataList, sortOption);
            return ResponseEntity.ok("Email sent successfully with the PDF attachment.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid sort option: " + e.getMessage());
        } catch (MessagingException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending email: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }




}
