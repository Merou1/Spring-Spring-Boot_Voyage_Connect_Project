package com.VoyageConnect.AgenceDeVoyage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithReceipt(String to, String subject, String body, byte[] pdfAttachment) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            DataSource dataSource = new ByteArrayDataSource(pdfAttachment, "application/pdf");
            helper.addAttachment("ReservationReceipt.pdf", dataSource);

            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace(); // Print the full stack trace for debugging
            throw e; // Re-throw the exception to propagate it
        }
    }
}