package com.VoyageConnect.AgenceDeVoyage.Dtos;

public class ReservationDTO {
	 private Long id;
	    private String reservationDate;
	    private String destinationName;
	    private String hotelName;
	    private Double price;
	    
	    

	    public ReservationDTO() {
			super();
		}

		public ReservationDTO(Long id, String reservationDate, String destinationName, String hotelName, Double price) {
	        this.id = id;
	        this.reservationDate = reservationDate;
	        this.destinationName = destinationName;
	        this.hotelName = hotelName;
	        this.price = price;
	    }

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getReservationDate() {
			return reservationDate;
		}

		public void setReservationDate(String reservationDate) {
			this.reservationDate = reservationDate;
		}

		public String getDestinationName() {
			return destinationName;
		}

		public void setDestinationName(String destinationName) {
			this.destinationName = destinationName;
		}

		public String getHotelName() {
			return hotelName;
		}

		public void setHotelName(String hotelName) {
			this.hotelName = hotelName;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}
	    

}
