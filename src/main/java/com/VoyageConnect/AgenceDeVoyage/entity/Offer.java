package com.VoyageConnect.AgenceDeVoyage.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "offers")
public class Offer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "destination_id", nullable = false)
	private Destination destination;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "flight_id", referencedColumnName = "id")
	private Flight flight;

	@OneToOne
	@JoinColumn(name = "hotel_id", nullable = true)
	private Hotel hotel;

	@Column(nullable = false)
	private String offerDetails;

	@Column(nullable = false)
	private Double offerPrice;

	@Transient
	private static final double COMPANY_MARKUP = 0.10;

	public Offer(Long id, Destination destination, Flight flight, Hotel hotel, String offerDetails, Double offerPrice) {
		super();
		this.id = id;
		this.destination = destination;
		this.flight = flight;
		this.hotel = hotel;
		this.offerDetails = offerDetails;
		this.offerPrice = offerPrice;
	}

	public Offer() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public String getOfferDetails() {
		return offerDetails;
	}

	public void setOfferDetails(String offerDetails) {
		this.offerDetails = offerDetails;
	}

	public void calculateOfferPrice() {
        double flightPrice = (this.flight != null) ? this.flight.getPrice() : 0.0;
        double hotelPrice = (this.hotel != null && this.hotel.getPricePerNight() != null)
                ? this.hotel.getPricePerNight() * 5 // 5 nights in each offer
                : 0.0;

        this.offerPrice = (flightPrice + hotelPrice) * (1 + COMPANY_MARKUP);
    }

	public Double getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(Double offerPrice) {
		this.offerPrice = offerPrice;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
		if (flight != null) {
	        calculateOfferPrice(); // Recalculate the offer price
	    }	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		calculateOfferPrice();
	}

}
