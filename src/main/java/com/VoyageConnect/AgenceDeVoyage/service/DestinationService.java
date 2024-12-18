package com.VoyageConnect.AgenceDeVoyage.service;

import com.VoyageConnect.AgenceDeVoyage.entity.Destination;
import com.VoyageConnect.AgenceDeVoyage.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinationService {

    @Autowired
    private DestinationRepository destinationRepository;

    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    public Optional<Destination> getDestinationById(Long id) {
        return destinationRepository.findById(id);
    }

    public Destination saveDestination(Destination destination) {
        return destinationRepository.save(destination);
    }

    public void deleteDestination(Long id) {
        destinationRepository.deleteById(id);
    }
}
