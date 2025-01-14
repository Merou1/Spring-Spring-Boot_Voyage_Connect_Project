package com.VoyageConnect.AgenceDeVoyage.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferDTO {
    private Long id;
    private Long destinationId;
    private Long flightId; 
    private Long hotelId;  
    private String offerDetails;
    private Double offerPrice;
	public OfferDTO() {
		super();
	}
	public OfferDTO(Long id, Long destinationId, Long flightId, Long hotelId, String offerDetails, Double offerPrice) {
		super();
		this.id = id;
		this.destinationId = destinationId;
		this.flightId = flightId;
		this.hotelId = hotelId;
		this.offerDetails = offerDetails;
		this.offerPrice = offerPrice;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(Long destinationId) {
		this.destinationId = destinationId;
	}
	public Long getFlightId() {
		return flightId;
	}
	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}
	public Long getHotelId() {
		return hotelId;
	}
	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
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
    
    
}
