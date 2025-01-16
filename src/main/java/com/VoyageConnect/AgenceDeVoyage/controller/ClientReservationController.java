package com.VoyageConnect.AgenceDeVoyage.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.VoyageConnect.AgenceDeVoyage.Dtos.ReservationDTO;
import com.VoyageConnect.AgenceDeVoyage.Pdfgenerator.PdfGenerator;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.entity.Reservation;
import com.VoyageConnect.AgenceDeVoyage.entity.User;
import com.VoyageConnect.AgenceDeVoyage.service.EmailService;
import com.VoyageConnect.AgenceDeVoyage.service.OfferService;
import com.VoyageConnect.AgenceDeVoyage.service.ReservationService;
import com.VoyageConnect.AgenceDeVoyage.service.UserService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/client")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/reservation")
    public ResponseEntity<String> createReservation(
            @RequestParam Long userId,
            @RequestParam Long offerId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Fetch user and offer
            Optional<User> userOptional = userService.getUserById(userId);
            Optional<Offer> offerOptional = offerService.getOfferById(offerId);

            if (!userOptional.isPresent() || !offerOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user or offer ID");
            }

            User user = userOptional.get();
            Offer offer = offerOptional.get();

            // Save the receipt file
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = userId + "_" + offerId + "_receipt.pdf";
            Path uploadPath = uploadDir.resolve(fileName);
            Files.write(uploadPath, file.getBytes());

            // Create reservation
            Reservation reservation = new Reservation(user, offer, java.time.LocalDate.now().toString(), uploadPath.toString());
            reservationService.saveReservation(reservation);

            // Generate and send PDF receipt
            String userName = user.getFullName();
            String offerDetails = offer.getDestination().getName() + " - " + offer.getOfferPrice();
            byte[] pdfReceipt = PdfGenerator.generateReceipt(userName, offerDetails, reservation.getReservationDate());

            try {
                emailService.sendEmailWithReceipt(
                        user.getUsername(),
                        "Your Reservation Receipt",
                        "Thank you for reserving with us. Please find your receipt attached.",
                        pdfReceipt
                );
            } catch (MessagingException e) {
                System.err.println("Failed to send email: " + e.getMessage());
                // Optionally, log the error or notify the admin
            }

            return ResponseEntity.ok("Reservation created successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save receipt: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/reservations/{userId}")
    public ResponseEntity<List<ReservationDTO>> getClientReservations(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<Reservation> reservations = reservationService.getReservationsByUser(user.get());
        
        // Convert Reservation entities to ReservationDTO
        List<ReservationDTO> reservationDTOs = reservations.stream().map(reservation -> 
            new ReservationDTO(
                reservation.getId(),
                reservation.getReservationDate(),
                reservation.getOffer().getDestination().getName(),
                reservation.getOffer().getHotel() != null ? reservation.getOffer().getHotel().getName() : "No Hotel",
                reservation.getOffer().getOfferPrice()
            )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(reservationDTOs);
    }

}
