package com.VoyageConnect.AgenceDeVoyage.repository;

import com.VoyageConnect.AgenceDeVoyage.entity.Reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByOfferId(Long offerId);  // Custom method to check if reservations exist for an offer
    long countByOfferId(Long offerId);
    List<Reservation> findByUserId(Long userId);


}
