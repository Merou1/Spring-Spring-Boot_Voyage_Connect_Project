package com.VoyageConnect.AgenceDeVoyage.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer stars;

    @Column(nullable = false)
    private Double pricePerNight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;
    
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;
    
    @Column(nullable = true)
    private String imageReference;


	public Hotel(Long id, String name, String location, Integer stars, Double pricePerNight, Offer offer, String imageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.stars = stars;
		this.pricePerNight = pricePerNight;
		this.offer = offer;
        this.imageUrl = imageUrl;
	}
	

	public Hotel(Long id, String name, String location, Integer stars, Double pricePerNight, Offer offer,
			String imageUrl, String imageReference) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.stars = stars;
		this.pricePerNight = pricePerNight;
		this.offer = offer;
		this.imageUrl = imageUrl;
		this.imageReference = imageReference;
	}


	public Hotel() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getStars() {
		return stars;
	}

	public void setStars(Integer stars) {
		this.stars = stars;
	}

	public Double getPricePerNight() {
		return pricePerNight;
	}

	public void setPricePerNight(Double pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
	    Offer oldOffer = this.offer;
	    this.offer = offer;
	    if (oldOffer != null && oldOffer.getHotel() == this) {
	        oldOffer.setHotel(null);
	    }
	    if (offer != null && offer.getHotel() != this) {
	        offer.setHotel(this);
	    }
	}
    
	public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


	public String getImageReference() {
		return imageReference;
	}


	public void setImageReference(String imageReference) {
		this.imageReference = imageReference;
	}
    
    
}
