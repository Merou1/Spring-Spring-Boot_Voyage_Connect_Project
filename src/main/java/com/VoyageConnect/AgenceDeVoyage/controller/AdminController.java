package com.VoyageConnect.AgenceDeVoyage.controller;

import com.VoyageConnect.AgenceDeVoyage.Dtos.FlightDTO;
import com.VoyageConnect.AgenceDeVoyage.Dtos.HotelDTO;
import com.VoyageConnect.AgenceDeVoyage.Dtos.OfferDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController //rest kayred json donc atkhdem bfrawework flfront , controller nishan kayred html atkhdem bih b thymleaf
@RequestMapping("/admin")
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

		// Optional: Check for flights or hotels and associate them as needed.
		if (offer.getFlight() != null && !flightService.getFlightById(offer.getFlight().getId()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Flight not found
		}

		if (offer.getHotel() != null && !hotelService.getHotelById(offer.getHotel().getId()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Hotel not found
		}

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
		Optional<Offer> existingOffer = offerService.getOfferById(id);
		if (!existingOffer.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Offer not found
		}

		// Ensure the destination exists and is valid
		if (offer.getDestination() == null
				|| !destinationService.getDestinationById(offer.getDestination().getId()).isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Destination must exist
		}

		// Set the offer id to maintain consistency
		offer.setId(id);

		// Set the destination explicitly to ensure it's not null
		Destination destination = offer.getDestination();
		if (destination != null && destination.getId() != null) {
			Optional<Destination> existingDestination = destinationService.getDestinationById(destination.getId());
			if (existingDestination.isPresent()) {
				// Only update the destination if necessary and all required fields are present
				Destination updatedDestination = existingDestination.get();
				if (updatedDestination.getCountry() == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Missing country field
				}
				offer.setDestination(updatedDestination);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Destination not found
			}
		}

		// Save the updated offer and return the response
		Offer savedOffer = offerService.saveOffer(offer);
		return ResponseEntity.ok(savedOffer);
	}

	@DeleteMapping("/offer/{id}")
	public ResponseEntity<String> deleteOffer(@PathVariable Long id) {
		if (reservationService.hasReservationsForOffer(id)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Cannot delete offer with associated reservations.");
		}
		offerService.deleteOffer(id);
		return ResponseEntity.ok("Offer with ID " + id + " has been deleted.");
	}

	// Reservation CRUD
	@PostMapping("/reservation")
	public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
		// Check if the Offer ID is provided and valid
		if (reservation.getOffer() == null || reservation.getOffer().getId() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		// Fetch the offer from the database
		Optional<Offer> offer = offerService.getOfferById(reservation.getOffer().getId());
		if (!offer.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		reservation.setOffer(offer.get());

		// Check if the user ID is provided in the request
		if (reservation.getUser() == null || reservation.getUser().getId() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		// Fetch the user from the database using the provided user ID
		Optional<User> user = userService.getUserById(reservation.getUser().getId());
		if (!user.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		reservation.setUser(user.get());

		// Save the reservation and return the response
		Reservation savedReservation = reservationService.saveReservation(reservation);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
	}

	@GetMapping("/reservations")
	public List<Reservation> getAllReservations() {
		return reservationService.getAllReservations();
	}

	@GetMapping("/reservation/{id}")
	public ResponseEntity<Optional<Reservation>> getReservationById(@PathVariable Long id) {
		Optional<Reservation> reservation = reservationService.getReservationById(id);
		return reservation.isPresent() ? ResponseEntity.ok(reservation)
				: ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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