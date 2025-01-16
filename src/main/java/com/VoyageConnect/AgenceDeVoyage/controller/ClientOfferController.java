package com.VoyageConnect.AgenceDeVoyage.controller;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.VoyageConnect.AgenceDeVoyage.Dtos.ClientOfferDTO;
import com.VoyageConnect.AgenceDeVoyage.Dtos.OfferDTO;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.service.DestinationService;
import com.VoyageConnect.AgenceDeVoyage.service.FlightService;
import com.VoyageConnect.AgenceDeVoyage.service.HotelService;
import com.VoyageConnect.AgenceDeVoyage.service.OfferService;

@RestController
@RequestMapping("/client/offers")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientOfferController {
    @Autowired
    private OfferService offerService;
    @Autowired
    private DestinationService destinationService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private FlightService flightService;

    // Get all available offers with details
    @GetMapping
    public ResponseEntity<List<ClientOfferDTO>> getAllOffers() {
        List<OfferDTO> offers = offerService.getAllOffers();
        List<ClientOfferDTO> clientOffers = offers.stream()
            .map(this::enrichOfferWithDetails)
            .collect(Collectors.toList());
        return ResponseEntity.ok(clientOffers);
    }

    // Get specific offer details
    @GetMapping("/{id}")
    public ResponseEntity<ClientOfferDTO> getOfferDetails(@PathVariable Long id) {
        Optional<OfferDTO> offerOpt = offerService.getOfferByIddto(id);
        if (offerOpt.isPresent()) {
            ClientOfferDTO enrichedOffer = enrichOfferWithDetails(offerOpt.get());
            return ResponseEntity.ok(enrichedOffer);
        }
        return ResponseEntity.notFound().build();
    }

    // Search offers with filters
    @GetMapping("/search")
    public ResponseEntity<List<ClientOfferDTO>> searchOffers(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        List<Offer> offers = offerService.searchOffers(country, minPrice, maxPrice);
        List<ClientOfferDTO> clientOffers = offers.stream()
            .map(offer -> enrichOfferWithDetails(offerService.mapToOfferDTO(offer)))
            .collect(Collectors.toList());
        return ResponseEntity.ok(clientOffers);
    }

    // Check offer availability
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable Long id) {
        boolean isAvailable = offerService.checkAvailability(id);
        return ResponseEntity.ok(isAvailable);
    }

    private ClientOfferDTO enrichOfferWithDetails(OfferDTO offerDTO) {
        ClientOfferDTO clientOffer = new ClientOfferDTO();
        
        // Basic offer details
        clientOffer.setId(offerDTO.getId());
        clientOffer.setOfferDetails(offerDTO.getOfferDetails());
        clientOffer.setOfferPrice(offerDTO.getOfferPrice());
        
        // Enrich with destination details
        if (offerDTO.getDestinationId() != null) {
            destinationService.getDestinationById(offerDTO.getDestinationId())
                .ifPresent(destination -> {
                    clientOffer.setDestinationName(destination.getName());
                    clientOffer.setCountry(destination.getCountry());
                });
        }
        
        // Enrich with hotel details
        if (offerDTO.getHotelId() != null) {
        	clientOffer.setHotelId(offerDTO.getHotelId());
            hotelService.getHotelById(offerDTO.getHotelId())
                .ifPresent(hotel -> {
                    clientOffer.setHotelName(hotel.getName());
                    clientOffer.setHotelStars(hotel.getStars());
                    clientOffer.setPricePerNight(hotel.getPricePerNight());
                });
        }
        
        if (offerDTO.getFlightId() != null) {
            flightService.getFlightById(offerDTO.getFlightId())
                .ifPresent(flight -> {
                    // Basic flight details
                    clientOffer.setDepartureDate(flight.getDepartureDate());
                    clientOffer.setReturnDate(flight.getReturnDate());
                    clientOffer.setAirline(flight.getAirline());
                    
                    // Additional flight details
                    clientOffer.setDeparture(flight.getDeparture());
                    clientOffer.setFlightDestination(flight.getDestination());
                    clientOffer.setFlightPrice(flight.getPrice());
                });
        }
        return clientOffer;
    }
}