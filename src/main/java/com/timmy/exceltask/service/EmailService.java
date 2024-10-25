package com.timmy.exceltask.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final PdfService pdfService;

    public EmailService(JavaMailSender emailSender, PdfService pdfService) {
        this.emailSender = emailSender;
        this.pdfService = pdfService;
    }

    public void sendEmailWithAttachment(String to, String subject, String text, ByteArrayInputStream pdfStream, String fileName) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        byte[] bytes = pdfStream.readAllBytes();
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

        helper.addAttachment(fileName, byteArrayResource);
        emailSender.send(message);
    }

    public void sendPdfByEmail(String toEmail, List<?> dataList, String sortOption) throws MessagingException, IOException {

        if (sortOption != null && !dataList.isEmpty()) {
            Class<?> clazz = dataList.get(0).getClass();

            dataList.sort((o1, o2) -> {
                try {
                    Field field = clazz.getDeclaredField(sortOption);
                    field.setAccessible(true);
                    Comparable<Object> value1 = (Comparable<Object>) field.get(o1);
                    Comparable<Object> value2 = (Comparable<Object>) field.get(o2);
                    return value1.compareTo(value2);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalArgumentException("Invalid sort option: " + sortOption);
                }
            });
        }

        ByteArrayInputStream pdfStream = pdfService.convertListToPdf(dataList);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject("PDF Report");
        helper.setText("Please find the attached PDF report.");
        helper.addAttachment("report.pdf", new ByteArrayDataSource(pdfStream, "application/pdf"));

        emailSender.send(message);
    }

    private <T> List<T> sortDataList(List<T> dataList, String sortOption) {
        if (sortOption == null || sortOption.isEmpty()) {
            return dataList;
        }

        return dataList.stream()
                .sorted(Comparator.comparing(item -> {
                    try {
                        return item.getClass().getDeclaredField(sortOption).get(item).toString();
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException("Invalid sort option: " + sortOption, e);
                    }
                }))
                .collect(Collectors.toList());
    }

}
