package com.VoyageConnect.AgenceDeVoyage.Dtos;

import java.time.LocalDateTime;

import com.VoyageConnect.AgenceDeVoyage.entity.Destination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOfferDTO {
    private Long id;
    // Destination details
    private String destinationName;
    private String country;

    // Hotel details
    private Long hotelId;  
    private String hotelName;
    private Integer hotelStars;
    private Double pricePerNight;

    // Flight details
    private String departureDate;        // Changed from LocalDateTime
    private String returnDate;           // Changed from arrivalTime
    private String airline;              // Changed from flightNumber
    private String departure;            // Added
    private Destination flightDestination; // Added
    private Double flightPrice;          // Added

    // Offer details
    private String offerDetails;
    private Double offerPrice;
    private boolean isAvailable;

    public ClientOfferDTO(Long id, String destinationName, String country, String hotelName, 
                         Integer hotelStars, Double pricePerNight, String departureDate, 
                         String returnDate, String airline, String departure, 
                         Destination flightDestination, Double flightPrice,
                         String offerDetails, Double offerPrice, boolean isAvailable) {
        super();
        this.id = id;
        this.destinationName = destinationName;
        this.country = country;
        this.hotelName = hotelName;
        this.hotelStars = hotelStars;
        this.pricePerNight = pricePerNight;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.airline = airline;
        this.departure = departure;
        this.flightDestination = flightDestination;
        this.flightPrice = flightPrice;
        this.offerDetails = offerDetails;
        this.offerPrice = offerPrice;
        this.isAvailable = isAvailable;
    }
    

	public ClientOfferDTO() {
		super();
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public Integer getHotelStars() {
		return hotelStars;
	}

	public void setHotelStars(Integer hotelStars) {
		this.hotelStars = hotelStars;
	}

	public Double getPricePerNight() {
		return pricePerNight;
	}

	public void setPricePerNight(Double pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	public String getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public Destination getFlightDestination() {
		return flightDestination;
	}

	public void setFlightDestination(Destination flightDestination) {
		this.flightDestination = flightDestination;
	}

	public Double getFlightPrice() {
		return flightPrice;
	}

	public void setFlightPrice(Double flightPrice) {
		this.flightPrice = flightPrice;
	}

	public String getOfferDetails() {
		return offerDetails;
	}

	public void setOfferDetails(String offerDetails) {
		this.offerDetails = offerDetails;
	}

	public Double getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(Double offerPrice) {
		this.offerPrice = offerPrice;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}


	public Long getHotelId() {
		return hotelId;
	}


	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}
	

    
}