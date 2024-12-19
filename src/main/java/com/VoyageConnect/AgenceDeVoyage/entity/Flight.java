package com.VoyageConnect.AgenceDeVoyage.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "flights")

public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String airline;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String departureDate;

    @Column(nullable = false)
    private String returnDate;

    @Column(nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

	public Flight() {
		super();
	}

	public Flight(Long id, String airline, String departure, String destination, String departureDate,
			String returnDate, Double price, Offer offer) {
		super();
		this.id = id;
		this.airline = airline;
		this.departure = departure;
		this.destination = destination;
		this.departureDate = departureDate;
		this.returnDate = returnDate;
		this.price = price;
		this.offer = offer;
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

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
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

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}
    
    
}