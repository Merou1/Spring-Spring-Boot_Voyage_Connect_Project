package com.VoyageConnect.AgenceDeVoyage.service;

import com.VoyageConnect.AgenceDeVoyage.entity.Hotel;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.repository.HotelRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.OfferRepository;

import jakarta.transaction.Transactional;

import com.VoyageConnect.AgenceDeVoyage.Dtos.HotelDTO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private OfferService offerService;


    public HotelDTO mapToHotelDTO(Hotel hotel) {
        return new HotelDTO(
            hotel.getId(),
            hotel.getName(),
            hotel.getLocation(),
            hotel.getStars(),
            hotel.getPricePerNight(),
            hotel.getOffer() != null ? hotel.getOffer().getId() : null,
            hotel.getImageUrl()
        );
    }

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                              .map(this::mapToHotelDTO)
                              .collect(Collectors.toList());
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    @Transactional
    public Hotel saveHotel(Hotel hotel) {
        // Validate before saving
        if (hotel.getLocation() == null || hotel.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }
        
        // Save the hotel first
        Hotel savedHotel = hotelRepository.save(hotel);
        
        // Update the offer without triggering another hotel update
        if (savedHotel.getOffer() != null) {
            offerService.updateHotelInOfferWithoutCascade(savedHotel.getOffer().getId(), savedHotel.getId());
        }

        return savedHotel;
    }
    @Transactional
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
    public List<Hotel> getHotelsForOffer(Long offerId) {
        return hotelRepository.findByOfferId(offerId);
    }
    
}