package com.VoyageConnect.AgenceDeVoyage.service;

import com.VoyageConnect.AgenceDeVoyage.entity.Destination;
import com.VoyageConnect.AgenceDeVoyage.entity.Flight;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.repository.DestinationRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.VoyageConnect.AgenceDeVoyage.Dtos.FlightDTO;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;
    @Autowired
    private OfferService offerService;
    @Autowired
    private DestinationRepository destinationRepository;
    

    public FlightDTO mapToFlightDTO(Flight flight) {
        return new FlightDTO(
            flight.getId(),
            flight.getAirline(),
            flight.getDeparture(),
            flight.getDestination() != null ? flight.getDestination().getId() : null,  // Map destination ID
            flight.getDepartureDate(),
            flight.getReturnDate(),
            flight.getPrice(),
            flight.getOffer() != null ? flight.getOffer().getId() : null
        );
    }


    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                               .map(this::mapToFlightDTO)
                               .collect(Collectors.toList());
    }
    public List<FlightDTO> getFlightsForOfferAsDTO(Long offerId) {
        return flightRepository.findByOfferId(offerId).stream()
                               .map(this::mapToFlightDTO)
                               .collect(Collectors.toList());
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public Flight saveFlight(Flight flight) {
        // Validate destination
        if (flight.getDestination() == null || flight.getDestination().getId() == null) {
            throw new RuntimeException("Destination must not be null");
        }
        Long destinationId = flight.getDestination().getId();
        Optional<Destination> destinationOptional = destinationRepository.findById(destinationId);
        if (destinationOptional.isEmpty()) {
            throw new RuntimeException("Destination not found for id " + destinationId);
        }
        flight.setDestination(destinationOptional.get());

        // Validate offer
        if (flight.getOffer() == null || flight.getOffer().getId() == null) {
            throw new RuntimeException("Offer must not be null");
        }

        // Save the flight first
        Flight savedFlight = flightRepository.save(flight);

        // Update the offer's flight reference only after successful save
        if (savedFlight.getOffer() != null) {
            try {
                offerService.updateFlightInOffer(savedFlight.getOffer().getId(), savedFlight.getId());
            } catch (Exception e) {
                // If updating the offer fails, we might want to roll back the flight save
                flightRepository.delete(savedFlight);
                throw new RuntimeException("Failed to update offer with flight: " + e.getMessage());
            }
        }

        return savedFlight;
    }




    

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }
    public List<Flight> getFlightsForOffer(Long offerId) {
        return flightRepository.findByOfferId(offerId);
    }

}