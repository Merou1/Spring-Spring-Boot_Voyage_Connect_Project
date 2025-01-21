package com.VoyageConnect.AgenceDeVoyage.controller;

import com.VoyageConnect.AgenceDeVoyage.Dtos.FlightDTO;
import com.VoyageConnect.AgenceDeVoyage.Dtos.HotelDTO;
import com.VoyageConnect.AgenceDeVoyage.Dtos.OfferDTO;
import com.VoyageConnect.AgenceDeVoyage.Dtos.ReservationDTO;
import com.VoyageConnect.AgenceDeVoyage.entity.Destination;
import com.VoyageConnect.AgenceDeVoyage.entity.Flight;
import com.VoyageConnect.AgenceDeVoyage.entity.Hotel;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.entity.Reservation;
import com.VoyageConnect.AgenceDeVoyage.entity.User;
import com.VoyageConnect.AgenceDeVoyage.repository.DestinationRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.OfferRepository;
import com.VoyageConnect.AgenceDeVoyage.service.DestinationService;
import com.VoyageConnect.AgenceDeVoyage.service.FlightService;
import com.VoyageConnect.AgenceDeVoyage.service.HotelService;
import com.VoyageConnect.AgenceDeVoyage.service.OfferService;
import com.VoyageConnect.AgenceDeVoyage.service.ReservationService;
import com.VoyageConnect.AgenceDeVoyage.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController //rest kayred json donc atkhdem bfrawework flfront , controller nishan kayred html atkhdem bih b thymleaf
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000") 
public class AdminController {

	@Autowired
	private DestinationService destinationService;

	@Autowired
	private OfferService offerService;

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private UserService userService;

	@Autowired
	private FlightService flightService;

	@Autowired
	private HotelService hotelService;

	// Destination CRUD
	@PostMapping("/destination")
	public Destination createDestination(@RequestBody Destination destination) {
		return destinationService.saveDestination(destination);
	}

	@GetMapping("/destinations")
	public List<Destination> getAllDestinations() {
		return destinationService.getAllDestinations();
	}

	@GetMapping("/destination/{id}")
	public Optional<Destination> getDestinationById(@PathVariable Long id) {
		return destinationService.getDestinationById(id);
	}

	@PutMapping("/destination/{id}")
	public Destination updateDestination(@PathVariable Long id, @RequestBody Destination destination) {
		destination.setId(id);
		return destinationService.saveDestination(destination);
	}

	@DeleteMapping("/destination/{id}")
	public ResponseEntity<String> deleteDestination(@PathVariable Long id) {
		if (destinationService.getDestinationById(id).isPresent()) {
			destinationService.deleteDestination(id);
			return ResponseEntity.ok("Destination with ID " + id + " has been deleted.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Destination with ID " + id + " not found.");
		}
	}

	// Offer CRUD
	@PostMapping("/offer")
	public ResponseEntity<Offer> createOffer(@RequestBody Offer offer) {
	    // Ensure destinationId is not null or invalid
	    if (offer.getDestination() == null || offer.getDestination().getId() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Destination must be set in the offer
	    }

	    // Retrieve the destination from the database
	    Optional<Destination> destination = destinationService.getDestinationById(offer.getDestination().getId());
	    if (!destination.isPresent()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Destination must exist
	    }

	    // Set the actual Destination object in the Offer
	    offer.setDestination(destination.get());

	    // Validate Flight
	    if (offer.getFlight() != null) {
	        Optional<Flight> flightOptional = flightService.getFlightById(offer.getFlight().getId());
	        if (!flightOptional.isPresent()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Flight not found
	        }
	        offer.setFlight(flightOptional.get()); // Set the actual Flight object
	    }

	    // Validate Hotel
	    if (offer.getHotel() != null) {
	        Optional<Hotel> hotelOptional = hotelService.getHotelById(offer.getHotel().getId());
	        if (!hotelOptional.isPresent()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Hotel not found
	        }
	        offer.setHotel(hotelOptional.get()); // Set the actual Hotel object
	    }

	    // Calculate the offer price
	    offer.calculateOfferPrice();

	    // Save the offer and return the response
	    Offer savedOffer = offerService.saveOffer(offer);
	    return ResponseEntity.status(HttpStatus.CREATED).body(savedOffer);
	}
	@GetMapping("/offers")
	public List<OfferDTO> getAllOffers() {
		return offerService.getAllOffers();
	}

	 @GetMapping("/offer/{id}")
	    public ResponseEntity<OfferDTO> getOfferByIddto(@PathVariable Long id) {
	        Optional<OfferDTO> offerDTO = offerService.getOfferByIddto(id);
	        return offerDTO.isPresent() ? ResponseEntity.ok(offerDTO.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	 @PutMapping("/offer/{id}")
	 public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offer) {
	     // Check if the offer exists
	     Optional<Offer> existingOfferOpt = offerService.getOfferById(id);
	     if (!existingOfferOpt.isPresent()) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	     }

	     Offer existingOffer = existingOfferOpt.get();

	     // Update the existing offer with the new values
	     existingOffer.setOfferDetails(offer.getOfferDetails());

	     // Ensure the destination exists and is valid
	     if (offer.getDestination() == null
	             || !destinationService.getDestinationById(offer.getDestination().getId()).isPresent()) {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	     }

	     // Update the destination
	     Destination destination = offer.getDestination();
	     if (destination != null && destination.getId() != null) {
	         Optional<Destination> existingDestination = destinationService.getDestinationById(destination.getId());
	         if (existingDestination.isPresent()) {
	             Destination updatedDestination = existingDestination.get();
	             if (updatedDestination.getCountry() == null) {
	                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	             }
	             existingOffer.setDestination(updatedDestination);
	         } else {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	         }
	     }

	     // Update flight and hotel if they are provided
	     if (offer.getFlight() != null) {
	         Optional<Flight> flightOptional = flightService.getFlightById(offer.getFlight().getId());
	         if (flightOptional.isPresent()) {
	             existingOffer.setFlight(flightOptional.get());
	         } else {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Flight not found
	         }
	     }

	     if (offer.getHotel() != null) {
	         Optional<Hotel> hotelOptional = hotelService.getHotelById(offer.getHotel().getId());
	         if (hotelOptional.isPresent()) {
	             existingOffer.setHotel(hotelOptional.get());
	         } else {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Hotel not found
	         }
	     }

	     // Recalculate the offer price
	     existingOffer.calculateOfferPrice();

	     // Save the updated offer and return the response
	     Offer savedOffer = offerService.saveOffer(existingOffer);
	     return ResponseEntity.ok(savedOffer);
	 }
	 @DeleteMapping("/offer/{id}")
	 public ResponseEntity<String> deleteOffer(@PathVariable Long id) {
	     Optional<Offer> offerOptional = offerService.getOfferById(id);
	     if (offerOptional.isEmpty()) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Offer not found.");
	     }

	     Offer offer = offerOptional.get();

	     // Delete related reservations first
	     if (!offer.getReservations().isEmpty()) {
	         reservationService.deleteReservationsByOfferId(id);
	     }

	     // Clear the hotel reference in the offer first
	     if (offer.getHotel() != null) {
	         Hotel hotel = offer.getHotel();
	         offer.setHotel(null);
	         offerService.saveOffer(offer); // Save the offer without the hotel reference
	         hotelService.deleteHotel(hotel.getId()); // Now delete the hotel
	     }

	     // Finally delete the offer
	     offerService.deleteOffer(id);
	     return ResponseEntity.ok("Offer with ID " + id + " has been deleted.");
	 }
	// Reservation CRUD
	@PostMapping("/reservation")
	public ResponseEntity<Reservation> createReservation(
	    @RequestParam("userId") Long userId,
	    @RequestParam("offerId") Long offerId,
	    @RequestParam("reservationDate") String reservationDate,
	    @RequestParam(value = "receipt", required = false) MultipartFile receipt) {

	    // Validate offer
	    Optional<Offer> offer = offerService.getOfferById(offerId);
	    if (!offer.isPresent()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }

	    // Validate user
	    Optional<User> user = userService.getUserById(userId);
	    if (!user.isPresent()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }

	    // Save receipt if provided
	    String receiptPath = null;
	    if (receipt != null && !receipt.isEmpty()) {
	        String fileName = System.currentTimeMillis() + "_" + receipt.getOriginalFilename();
	        String uploadDir = "uploads/receipts/";
	        Path filePath = Paths.get(uploadDir, fileName);

	        try {
	            Files.createDirectories(filePath.getParent());
	            Files.write(filePath, receipt.getBytes());
	            receiptPath = filePath.toString();
	        } catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

	    // Create and save reservation
	    Reservation reservation = new Reservation(user.get(), offer.get(), reservationDate, receiptPath);
	    Reservation savedReservation = reservationService.saveReservation(reservation);

	    return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
	}


	@GetMapping("/reservations")
	public ResponseEntity<List<ReservationDTO>> getAllReservations() {
	    List<Reservation> reservations = reservationService.getAllReservations();

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

	@GetMapping("/reservation/{id}")
	public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
	    Optional<Reservation> reservation = reservationService.getReservationById(id);
	    
	    if (reservation.isPresent()) {
	        // Convert Reservation entity to ReservationDTO
	        ReservationDTO reservationDTO = new ReservationDTO(
	            reservation.get().getId(),
	            reservation.get().getReservationDate(),
	            reservation.get().getOffer().getDestination().getName(),
	            reservation.get().getOffer().getHotel() != null ? reservation.get().getOffer().getHotel().getName() : "No Hotel",
	            reservation.get().getOffer().getOfferPrice()
	        );
	        return ResponseEntity.ok(reservationDTO);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	}


	@PutMapping("/reservation/{id}")
	public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
		// Check if the reservation exists in the database
		if (!reservationService.getReservationById(id).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// Check if the offer ID is valid and fetch the full offer with related entities
		if (reservation.getOffer() == null || reservation.getOffer().getId() == null
				|| !offerService.getOfferById(reservation.getOffer().getId()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Offer must exist
		}

		// Fetch the full Offer to ensure all relationships are loaded (e.g.,
		// Destination)
		Optional<Offer> offer = offerService.getOfferById(reservation.getOffer().getId());
		if (!offer.isPresent() || offer.get().getDestination() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Offer must have a valid destination
		}

		// Check if the user ID is valid
		if (reservation.getUser() == null || reservation.getUser().getId() == null
				|| !userService.getUserById(reservation.getUser().getId()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // User must exist
		}

		// Set the offer and user to the reservation
		reservation.setOffer(offer.get());
		reservation.setUser(userService.getUserById(reservation.getUser().getId()).get());

		// Set the ID of the reservation for the update
		reservation.setId(id);

		// Save and return the updated reservation
		return ResponseEntity.ok(reservationService.saveReservation(reservation));
	}

	@DeleteMapping("/reservation/{id}")
	public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
		if (!reservationService.getReservationById(id).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found.");
		}
		reservationService.deleteReservation(id);
		return ResponseEntity.ok("Reservation with ID " + id + " has been deleted.");
	}

	// Add a flight to an offer
	@PostMapping("/offer/{offerId}/flights")
	public ResponseEntity<Flight> addFlightToOffer(@PathVariable Long offerId, @RequestBody Flight flight) {
		Optional<Offer> offer = offerService.getOfferById(offerId);
		if (offer.isPresent()) {
			flight.setOffer(offer.get());
			Flight savedFlight = flightService.saveFlight(flight);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	// Retrieve all flights for an offer
	@GetMapping("/offer/{offerId}/flights")
	public List<FlightDTO> getFlightsForOffer(@PathVariable Long offerId) {
	    return flightService.getFlightsForOfferAsDTO(offerId);
	}

	// Add a hotel to an offer
	@PostMapping("/offer/{offerId}/hotels")
	public ResponseEntity<Hotel> addHotelToOffer(@PathVariable Long offerId, @RequestBody Hotel hotel) {
		Optional<Offer> offer = offerService.getOfferById(offerId);
		if (offer.isPresent()) {
			hotel.setOffer(offer.get());
			Hotel savedHotel = hotelService.saveHotel(hotel);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedHotel);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@GetMapping("/offer/{offerId}/hotels")
    public List<HotelDTO> getHotelsForOffer(@PathVariable Long offerId) {
        return hotelService.getHotelsForOffer(offerId)
                           .stream()
                           .map(hotelService::mapToHotelDTO)
                           .collect(Collectors.toList());
    }

}