package com.VoyageConnect.AgenceDeVoyage.service;

import com.VoyageConnect.AgenceDeVoyage.entity.Reservation;
import com.VoyageConnect.AgenceDeVoyage.entity.User;
import com.VoyageConnect.AgenceDeVoyage.repository.ReservationRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
    // Check if any reservations exist for a specific offer
    public boolean hasReservationsForOffer(Long offerId) {
        return reservationRepository.existsByOfferId(offerId);
    }
    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUserId(user.getId());
    }
    @Transactional
    public void deleteReservationsByOfferId(Long offerId) {
        reservationRepository.deleteByOfferId(offerId);
    }
    
}
