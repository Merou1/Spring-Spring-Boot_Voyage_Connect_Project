package com.VoyageConnect.AgenceDeVoyage.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.VoyageConnect.AgenceDeVoyage.Dtos.OfferDTO;
import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
import com.VoyageConnect.AgenceDeVoyage.service.OfferService;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000") 
public class SearchController {
	@Autowired
	private OfferService offerService;

    @GetMapping
    public ResponseEntity<List<OfferDTO>> searchOffers(@RequestParam(required = false) String country,
            @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice) {
        
        List<OfferDTO> offerDTOs = offerService.searchOffers(country, minPrice, maxPrice).stream()
                                                 .map(offerService::mapToOfferDTO)
                                                 .collect(Collectors.toList());
        
        return ResponseEntity.ok(offerDTOs);
    }

	@GetMapping("/availability/{offerId}")
	public ResponseEntity<Boolean> checkOfferAvailability(@PathVariable Long offerId) {
		boolean isAvailable = offerService.checkAvailability(offerId);
		return ResponseEntity.ok(isAvailable);
	}
}