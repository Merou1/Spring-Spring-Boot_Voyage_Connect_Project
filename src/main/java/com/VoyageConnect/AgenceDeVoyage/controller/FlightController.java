package com.VoyageConnect.AgenceDeVoyage.controller;

import com.VoyageConnect.AgenceDeVoyage.entity.Flight;
import com.VoyageConnect.AgenceDeVoyage.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping
    public List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        Optional<Flight> flight = flightService.getFlightById(id);
        return flight.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/offer/{offerId}")
    public List<Flight> getFlightsForOffer(@PathVariable Long offerId) {
        return flightService.getFlightsForOffer(offerId);
    }

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        Flight savedFlight = flightService.saveFlight(flight);
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
            flightService.saveFlight(flight);
            return ResponseEntity.ok(flight);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFlight(@PathVariable Long id) {
        if (!flightService.getFlightById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flight not found.");
        }
        else {
        	flightService.deleteFlight(id);
            return ResponseEntity.ok("Flight with ID " + id + " has been deleted.");
        }
        
    }
}
