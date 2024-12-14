package com.VoyageConnect.AgenceDeVoyage.service;

import com.VoyageConnect.AgenceDeVoyage.entity.Flight;
import com.VoyageConnect.AgenceDeVoyage.entity.Hotel;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.repository.FlightRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.HotelRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.OfferRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.ReservationRepository;
import com.VoyageConnect.AgenceDeVoyage.Dtos.OfferDTO;


import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfferService {

	@Autowired
	private OfferRepository offerRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private HotelRepository hotelRepository;

	public OfferDTO mapToOfferDTO(Offer offer) {
	    return new OfferDTO(
	        offer.getId(),
	        offer.getDestination() != null ? offer.getDestination().getId() : null,
	        offer.getFlight() != null ? offer.getFlight().getId() : null,
	        offer.getHotel() != null ? offer.getHotel().getId() : null,
	        offer.getOfferDetails(),
	        offer.getOfferPrice()
	    );
	}
	public Optional<OfferDTO> getOfferByIddto(Long id) {
        return offerRepository.findById(id).map(offer -> {
            OfferDTO offerDTO = new OfferDTO();
            offerDTO.setId(offer.getId());
            offerDTO.setDestinationId(offer.getDestination().getId());
            offerDTO.setFlightId(offer.getFlight().getId());
            offerDTO.setHotelId(offer.getHotel() != null ? offer.getHotel().getId() : null); // Handle null
            offerDTO.setOfferDetails(offer.getOfferDetails());
            offerDTO.setOfferPrice(offer.getOfferPrice());
            return offerDTO;
        });
    }

	public List<OfferDTO> getAllOffers() {
	    return offerRepository.findAll().stream()
	                          .map(this::mapToOfferDTO)
	                          .collect(Collectors.toList());
	}

	public Optional<Offer> getOfferById(Long id) {
		return offerRepository.findById(id);
	}

	public Offer saveOffer(Offer offer) {
		return offerRepository.save(offer);
	}

	public void deleteOffer(Long id) {
		offerRepository.deleteById(id);
	}

	// Check if any offers exist for a specific destination
	public boolean hasOffersForDestination(Long destinationId) {
		return offerRepository.existsByDestinationId(destinationId);
	}

	public List<Offer> searchOffers(String country, Double minPrice, Double maxPrice) {
		if (country != null && minPrice != null && maxPrice != null) {
			return offerRepository.findByDestination_CountryAndOfferPriceBetween(country, minPrice, maxPrice);
		} else if (country != null) {
			return offerRepository.findByDestination_Country(country);
		} else if (minPrice != null && maxPrice != null) {
			return offerRepository.findByOfferPriceBetween(minPrice, maxPrice);
		}
		return offerRepository.findAll();
	}

	// Retrieve flights for an offer
	public List<Flight> getFlightsForOffer(Long offerId) {
		return flightRepository.findByOfferId(offerId);
	}

	// Retrieve hotels for an offer
	public List<Hotel> getHotelsForOffer(Long offerId) {
		return hotelRepository.findByOfferId(offerId);
	}

	// Vérification de disponibilité (à implémenter selon vos besoins spécifiques)
	public boolean checkAvailability(Long offerId) {
		Offer offer = offerRepository.findById(offerId)
				.orElseThrow(() -> new EntityNotFoundException("Offer not found"));

		// Exemple simple : limiter à 10 réservations par offre
		long reservationCount = reservationRepository.countByOfferId(offerId);
		return reservationCount < 10;
	}
	
	public void updateFlightInOffer(Long offerId, Long flightId) {
        Optional<Offer> optionalOffer = offerRepository.findById(offerId);
        if (optionalOffer.isPresent()) {
            Offer offer = optionalOffer.get();
            Flight flight = new Flight();
            flight.setId(flightId); // Only set the ID to avoid unnecessary DB fetch
            offer.setFlight(flight);
            offerRepository.save(offer);
        } else {
            throw new EntityNotFoundException("Offer with ID " + offerId + " not found.");
        }
    }
	public void updateHotelInOffer(Long offerId, Long hotelId) {
        Optional<Offer> optionalOffer = offerRepository.findById(offerId);
        if (optionalOffer.isPresent()) {
            Offer offer = optionalOffer.get();
            Hotel hotel = new Hotel();
            hotel.setId(hotelId); // Only set the ID to avoid unnecessary DB fetch
            offer.setHotel(hotel);
            offerRepository.save(offer);
        } else {
            throw new EntityNotFoundException("Offer with ID " + offerId + " not found.");
        }
    }
}
