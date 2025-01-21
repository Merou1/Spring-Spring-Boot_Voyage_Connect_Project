package com.VoyageConnect.AgenceDeVoyage.service;

import com.VoyageConnect.AgenceDeVoyage.entity.Flight;
import com.VoyageConnect.AgenceDeVoyage.entity.Hotel;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.repository.FlightRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.HotelRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.OfferRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.ReservationRepository;
import com.VoyageConnect.AgenceDeVoyage.Dtos.OfferDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OfferService {

	@Autowired
	private OfferRepository offerRepository;
	@Autowired
	private ReservationRepository reservationRepository;
	@Autowired
	private FlightRepository flightRepository;
	@Autowired
	private HotelRepository hotelRepository;
	
	@Autowired
    private EntityManager entityManager;

	   @Transactional // Add this annotation
	    public Offer saveOffer(Offer offer) {
	        if (offer.getId() == null) {
	            // New entity, use persist
	            entityManager.persist(offer);
	        } else {
	            // Detached entity, use merge
	            offer = entityManager.merge(offer);
	        }
	        return offer;
	    }

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
	
	@Transactional
    public void updateFlightInOffer(Long offerId, Long flightId) {
        Optional<Offer> optionalOffer = offerRepository.findById(offerId);
        if (optionalOffer.isPresent()) {
            Offer offer = optionalOffer.get();
            Flight flight = new Flight();
            flight.setId(flightId);
            offer.setFlight(flight);
            offer.calculateOfferPrice(); // Recalculate the offer price
            offerRepository.save(offer); // Save the updated offer
        } else {
            throw new EntityNotFoundException("Offer with ID " + offerId + " not found.");
        }
    }

    @Transactional
    public void updateHotelInOffer(Long offerId, Long hotelId) {
        Optional<Offer> optionalOffer = offerRepository.findById(offerId);
        if (optionalOffer.isPresent()) {
            Offer offer = optionalOffer.get();
            Hotel hotel = new Hotel();
            hotel.setId(hotelId);
            offer.setHotel(hotel);
            offer.calculateOfferPrice(); // Recalculate the offer price
            offerRepository.save(offer); // Save the updated offer
        } else {
            throw new EntityNotFoundException("Offer with ID " + offerId + " not found.");
        }
    }

	
	@Transactional
    public void updateHotelInOfferWithoutCascade(Long offerId, Long hotelId) {
        // Use native query to update offer without triggering cascading updates
        entityManager.createNativeQuery("UPDATE offers SET hotel_id = ? WHERE id = ?")
            .setParameter(1, hotelId)
            .setParameter(2, offerId)
            .executeUpdate();
    }
	
	public boolean isHotelReferencedByOffer(Long hotelId) {
        return offerRepository.existsByHotelId(hotelId);
    }

    public boolean isFlightReferencedByOffer(Long flightId) {
        return offerRepository.existsByFlightId(flightId);
    }
	
}
