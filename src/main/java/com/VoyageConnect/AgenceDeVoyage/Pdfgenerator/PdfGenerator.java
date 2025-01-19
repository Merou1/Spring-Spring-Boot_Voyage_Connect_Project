package com.VoyageConnect.AgenceDeVoyage.Pdfgenerator;

import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayOutputStream;


public class PdfGenerator {
    public static byte[] generateReceipt(String userName, String offerDetails, String reservationDate) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

            document.add(new Paragraph("Reservation Receipt"));
            document.add(new Paragraph("User: " + userName));
            document.add(new Paragraph("Offer: " + offerDetails));
            document.add(new Paragraph("Date: " + reservationDate));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
