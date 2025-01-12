package com.VoyageConnect.AgenceDeVoyage.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {
    private Long id;
    private String name;
    private String location;
    private Integer stars;
    private Double pricePerNight;
    private Long offerId; // Avoid direct Offer reference
    private String imageUrl;

    
	public HotelDTO(Long id, String name, String location, Integer stars, Double pricePerNight, Long offerId,String imageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.location = location;
		this.stars = stars;
		this.pricePerNight = pricePerNight;
		this.offerId = offerId;
        this.imageUrl = imageUrl;
	}
	public HotelDTO() {
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
	public Long getOfferId() {
		return offerId;
	}
	public void setOfferId(Long offerId) {
		this.offerId = offerId;
	}
	public String getImageUrl() {
	        return imageUrl;
	}
	    
	public void setImageUrl(String imageUrl) {
	        this.imageUrl = imageUrl;
	}
    
    
}
