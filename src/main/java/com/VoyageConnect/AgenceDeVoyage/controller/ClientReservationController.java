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
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.entity.Reservation;
import com.VoyageConnect.AgenceDeVoyage.entity.User;
import com.VoyageConnect.AgenceDeVoyage.service.OfferService;
import com.VoyageConnect.AgenceDeVoyage.service.ReservationService;
import com.VoyageConnect.AgenceDeVoyage.service.UserService;

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

 // In ClientReservationController.java
    @PostMapping("/reservation")
    public ResponseEntity<String> createReservation(
            @RequestParam Long userId,
            @RequestParam Long offerId,
            @RequestParam("file") MultipartFile file) {
        try {
            Optional<User> user = userService.getUserById(userId);
            Optional<Offer> offer = offerService.getOfferById(offerId);

            if (!user.isPresent() || !offer.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user or offer ID");
            }

            // Create uploads directory if it doesn't exist
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Save the receipt file
            String fileName = userId + "_" + offerId + "_receipt.pdf";
            Path uploadPath = uploadDir.resolve(fileName);
            Files.write(uploadPath, file.getBytes());

            // Create and save the reservation
            Reservation reservation = new Reservation();
            reservation.setUser(user.get());
            reservation.setOffer(offer.get());
            reservation.setReservationDate(java.time.LocalDate.now().toString());
            reservation.setReceiptPath(uploadPath.toString());

            reservationService.saveReservation(reservation);

            return ResponseEntity.ok("Reservation created successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save receipt: " + e.getMessage());
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
