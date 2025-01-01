package com.VoyageConnect.AgenceDeVoyage.Dtos;

import com.VoyageConnect.AgenceDeVoyage.entity.Destination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDTO {
    private Long id;
    private String airline;
    private String departure;
    private Long destinationId;  // This should be a Long referring to destination_id
    private String departureDate;
    private String returnDate;
    private Double price;
    private Long offerId; 

	public FlightDTO(Long id, String airline, String departure, Long destinationId, String departureDate,
			String returnDate, Double price, Long offerId) {
		super();
		this.id = id;
		this.airline = airline;
		this.departure = departure;
		this.destinationId = destinationId;
		this.departureDate = departureDate;
		this.returnDate = returnDate;
		this.price = price;
		this.offerId = offerId;
	}
	public FlightDTO() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	
	public Long getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(Long destinationId) {
		this.destinationId = destinationId;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getOfferId() {
		return offerId;
	}
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}
    
    
}
