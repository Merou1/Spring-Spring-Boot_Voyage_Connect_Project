package com.VoyageConnect.AgenceDeVoyage.controller;

import com.VoyageConnect.AgenceDeVoyage.Dtos.FlightDTO;
import com.VoyageConnect.AgenceDeVoyage.entity.Destination;
import com.VoyageConnect.AgenceDeVoyage.entity.Flight;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.repository.DestinationRepository;
import com.VoyageConnect.AgenceDeVoyage.service.FlightService;
import com.VoyageConnect.AgenceDeVoyage.service.OfferService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "http://localhost:3000")
public class FlightController {

	@Autowired
	private FlightService flightService;

	@Autowired
	private OfferService offerService;
	
	@Autowired
	private DestinationRepository destinationRepository;

	@GetMapping
	public List<FlightDTO> getAllFlights() {
		return flightService.getAllFlights();
	}

	@GetMapping("/{id}")
	public ResponseEntity<FlightDTO> getFlightById(@PathVariable Long id) {
		Optional<Flight> flight = flightService.getFlightById(id);
		return flight.map(f -> ResponseEntity.ok(flightService.mapToFlightDTO(f)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/offer/{offerId}")
	public List<Flight> getFlightsForOffer(@PathVariable Long offerId) {
		return flightService.getFlightsForOffer(offerId);
	}

	@PostMapping
	public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
	    // Validate required fields
	    if (flight.getPrice() == null || flight.getDestination() == null || flight.getDestination().getId() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }

	    // Validate destination
	    Long destinationId = flight.getDestination().getId();
	    Optional<Destination> destinationOptional = destinationRepository.findById(destinationId);
	    if (destinationOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }

	    // Set the destination for the flight
	    flight.setDestination(destinationOptional.get());

	    // Save the flight first
	    Flight savedFlight = flightService.saveFlight(flight);

	    // If an offer ID is provided, link it to the flight
	    if (flight.getOfferId() != null) {
	        Optional<Offer> offerOptional = offerService.getOfferById(flight.getOfferId());
	        if (offerOptional.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	        }

	        // Link the Offer to the Flight
	        Offer offer = offerOptional.get();
	        offer.setFlight(savedFlight); // Link the saved Flight to the Offer
	        offer.calculateOfferPrice(); // Recalculate the offer price

	        // Save the updated Offer
	        Offer updatedOffer = offerService.saveOffer(offer);

	        // Update the flight_id in the offers table
	        savedFlight.setOffer(updatedOffer); // Link the Offer to the Flight
	        flightService.saveFlight(savedFlight); // Save the updated Flight
	    }

	    return ResponseEntity.ok(savedFlight);
	}
	@PutMapping("/{id}")
	public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight flightDetails) {
	    Optional<Flight> existingFlight = flightService.getFlightById(id);
	    if (existingFlight.isPresent()) {
	        Flight flight = existingFlight.get();
	        flight.setAirline(flightDetails.getAirline());
	        flight.setDeparture(flightDetails.getDeparture());
	        flight.setDestination(flightDetails.getDestination());
	        flight.setDepartureDate(flightDetails.getDepartureDate());
	        flight.setReturnDate(flightDetails.getReturnDate());
	        flight.setPrice(flightDetails.getPrice());
	        flight.setOffer(flightDetails.getOffer());

	        // Update the associated offer's price
	        Offer offer = flight.getOffer();
	        if (offer != null) {
	            offer.calculateOfferPrice();
	            offerService.saveOffer(offer);
	        }

	        flightService.saveFlight(flight);
	        return ResponseEntity.ok(flight);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteFlight(@PathVariable Long id) {
	    Optional<Flight> flightOptional = flightService.getFlightById(id);
	    if (flightOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flight not found.");
	    }

	    // Check if the flight is referenced by any offer
	    if (offerService.isFlightReferencedByOffer(id)) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Cannot delete flight because it is referenced by an offer.");
	    }

	    // Delete the flight
	    flightService.deleteFlight(id);
	    return ResponseEntity.ok("Flight with ID " + id + " has been deleted.");
	}
}
