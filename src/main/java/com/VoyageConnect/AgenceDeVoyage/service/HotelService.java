package com.VoyageConnect.AgenceDeVoyage.service;

import com.VoyageConnect.AgenceDeVoyage.entity.Hotel;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.repository.HotelRepository;
import com.VoyageConnect.AgenceDeVoyage.repository.OfferRepository;
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

    public Hotel saveHotel(Hotel hotel) {
        Hotel savedHotel = hotelRepository.save(hotel);
        Offer offer = savedHotel.getOffer();

        // Update the related Offer's hotel_id
        if (offer != null) {
            offerService.updateHotelInOffer(offer.getId(), savedHotel.getId());
        }

        return savedHotel;
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
    public List<Hotel> getHotelsForOffer(Long offerId) {
        return hotelRepository.findByOfferId(offerId);
    }

}