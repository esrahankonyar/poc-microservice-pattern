package com.rental.dto;

import lombok.Data;

@Data
public class ReservationData {
    private HotelReservationDto hotelReservationDto;
    private FlightReservationDto flightReservationDto;
    private RentalDto rentalDto;
    private PaymentDto paymentDto;
    private String nextStep;
}
