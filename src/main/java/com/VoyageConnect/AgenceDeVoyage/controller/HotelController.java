	package com.VoyageConnect.AgenceDeVoyage.controller;
	
	import com.VoyageConnect.AgenceDeVoyage.Dtos.HotelDTO;
	import com.VoyageConnect.AgenceDeVoyage.entity.Hotel;
	import com.VoyageConnect.AgenceDeVoyage.entity.Offer;
	import com.VoyageConnect.AgenceDeVoyage.service.HotelService;
	import com.VoyageConnect.AgenceDeVoyage.service.OfferService;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.http.HttpStatus;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.*;
	import org.springframework.web.multipart.MultipartFile;
	
	import java.io.IOException;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.util.List;
	import java.util.Optional;
	
	@RestController
	@RequestMapping("/api/hotels")
	@CrossOrigin(origins = "http://localhost:5173")
	public class HotelController {
	
		@Autowired
		private HotelService hotelService;
		
		@Autowired
		private OfferService offerService;
	
		@GetMapping
		public List<HotelDTO> getAllHotels() {
			return hotelService.getAllHotels();
		}
	
		@GetMapping("/{id}")
		public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long id) {
			Optional<Hotel> hotel = hotelService.getHotelById(id);
			return hotel.map(h -> ResponseEntity.ok(hotelService.mapToHotelDTO(h)))
					.orElseGet(() -> ResponseEntity.notFound().build());
		}
	
		@GetMapping("/offer/{offerId}")
		public List<Hotel> getHotelsForOffer(@PathVariable Long offerId) {
			return hotelService.getHotelsForOffer(offerId);
		}
	
		@PostMapping
	    public ResponseEntity<?> createHotel(@RequestBody Hotel hotel) {
	        try {
	            // Validate required fields
	            if (hotel.getName() == null || hotel.getName().trim().isEmpty()) {
	                return ResponseEntity.badRequest().body("Name is required");
	            }
	            if (hotel.getLocation() == null || hotel.getLocation().trim().isEmpty()) {
	                return ResponseEntity.badRequest().body("Location is required");
	            }
	            if (hotel.getPricePerNight() == null) {
	                return ResponseEntity.badRequest().body("Price per night is required");
	            }
	            if (hotel.getOffer() == null || hotel.getOffer().getId() == null) {
	                return ResponseEntity.badRequest().body("Offer ID is required");
	            }

	            // Check if the offer exists
	            Optional<Offer> offerOptional = offerService.getOfferById(hotel.getOffer().getId());
	            if (offerOptional.isEmpty()) {
	                return ResponseEntity.badRequest().body("Offer not found");
	            }

	            // Save the hotel
	            Hotel savedHotel = hotelService.saveHotel(hotel);

	            // Update the offer price
	            Offer offer = offerOptional.get();
	            offer.setHotel(savedHotel);
	            offer.calculateOfferPrice(); // Recalculate the offer price
	            offerService.saveOffer(offer); // Save the updated offer

	            return ResponseEntity.ok(savedHotel);
	        } catch (Exception e) {
	            System.err.println("Error creating hotel: " + e.getMessage());
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error creating hotel: " + e.getMessage());
	        }
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
		        hotel.setImageUrl(hotelDetails.getImageUrl());

		        // Validate and update the associated offer
		        if (hotelDetails.getOffer() != null && hotelDetails.getOffer().getId() != null) {
		            Optional<Offer> offerOptional = offerService.getOfferById(hotelDetails.getOffer().getId());
		            if (offerOptional.isPresent()) {
		                Offer offer = offerOptional.get();
		                hotel.setOffer(offer);

		                // Recalculate the offer price
		                offer.calculateOfferPrice();
		                offerService.saveOffer(offer);
		            } else {
		                return ResponseEntity.badRequest().body(null); // Offer not found
		            }
		        } else {
		            return ResponseEntity.badRequest().body(null); // Offer ID is required
		        }

		        hotelService.saveHotel(hotel);
		        return ResponseEntity.ok(hotel);
		    } else {
		        return ResponseEntity.notFound().build();
		    }
		}		
		@DeleteMapping("/{id}")
		public ResponseEntity<String> deleteHotel(@PathVariable Long id) {
		    Optional<Hotel> hotelOptional = hotelService.getHotelById(id);
		    if (hotelOptional.isEmpty()) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hotel not found.");
		    }

		    // Check if the hotel is referenced by any offer
		    if (offerService.isHotelReferencedByOffer(id)) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                .body("Cannot delete hotel because it is referenced by an offer.");
		    }

		    // Delete the hotel
		    hotelService.deleteHotel(id);
		    return ResponseEntity.ok("Hotel with ID " + id + " has been deleted.");
		}
	
		@PostMapping("/upload-image")
		public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile imageFile) {
			try {
				String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
				Path filePath = Paths.get("uploads/" + fileName);
	
				if (!Files.exists(filePath.getParent())) {
					Files.createDirectories(filePath.getParent());
				}
	
				Files.copy(imageFile.getInputStream(), filePath);
				
	
				// Return the relative path as the response
				return ResponseEntity.ok(fileName);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image.");
			}
		}
	
	}
