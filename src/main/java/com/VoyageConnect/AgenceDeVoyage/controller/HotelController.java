package com.VoyageConnect.AgenceDeVoyage.controller;

import com.VoyageConnect.AgenceDeVoyage.Dtos.HotelDTO;
import com.VoyageConnect.AgenceDeVoyage.entity.Hotel;
import com.VoyageConnect.AgenceDeVoyage.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "http://localhost:3000") 
public class HotelController {

	@Autowired
	private HotelService hotelService;

	@GetMapping
	public List<HotelDTO> getAllHotels() {
		return hotelService.getAllHotels();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Hotel> getHotelById(@PathVariable Long id) {
		Optional<Hotel> hotel = hotelService.getHotelById(id);
		return hotel.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/offer/{offerId}")
	public List<Hotel> getHotelsForOffer(@PathVariable Long offerId) {
		return hotelService.getHotelsForOffer(offerId);
	}

	@PostMapping
	public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
		Hotel savedHotel = hotelService.saveHotel(hotel);
		return ResponseEntity.ok(savedHotel);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel hotelDetails) {
		Optional<Hotel> existingHotel = hotelService.getHotelById(id);
		if (existingHotel.isPresent()) {
			Hotel hotel = existingHotel.get();
			hotel.setName(hotelDetails.getName());
			hotel.setLocation(hotelDetails.getLocation());
			hotel.setStars(hotelDetails.getStars());
			hotel.setPricePerNight(hotelDetails.getPricePerNight());
			hotel.setOffer(hotelDetails.getOffer());
			hotelService.saveHotel(hotel);
			return ResponseEntity.ok(hotel);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteHotel(@PathVariable Long id) {
		if (!hotelService.getHotelById(id).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hotel not found.");
		} else {
			hotelService.deleteHotel(id);
			return ResponseEntity.ok("Hotel with ID " + id + " has been deleted.");
		}
	}

}
