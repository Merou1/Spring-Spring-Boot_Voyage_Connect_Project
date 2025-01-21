package com.VoyageConnect.AgenceDeVoyage.repository;

import com.VoyageConnect.AgenceDeVoyage.entity.Offer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    boolean existsByDestinationId(Long destinationId);  // Custom method to check if offers exist for a destination
    // Recherche par destination
    List<Offer> findByDestination_Country(String country);
    
    // Recherche par budget
    List<Offer> findByOfferPriceLessThanEqual(Double maxPrice);
    
    // Recherche combinée avec filtres
    List<Offer> findByDestination_CountryAndOfferPriceLessThanEqual(String country, Double maxPrice);
    
    // Recherche par plage de prix
    List<Offer> findByOfferPriceBetween(Double minPrice, Double maxPrice);
    
    List<Offer> findByDestination_CountryAndOfferPriceBetween(String country, Double minPrice, Double maxPrice);
    
    List<Offer> findByFlightId(Long flightId); // Custom query to find offers by flight ID
    
    boolean existsByHotelId(Long hotelId);
    boolean existsByFlightId(Long flightId);


}
