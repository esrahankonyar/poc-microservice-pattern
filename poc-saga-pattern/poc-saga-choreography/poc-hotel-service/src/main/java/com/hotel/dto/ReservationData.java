package com.hotel.dto;

import lombok.Data;


@Data
public class ReservationData {
    private HotelReservationDto hotelReservationDto;
    private FlightReservationDto flightReservationDto;
    private RentalDto rentalDto;
    private PaymentDto paymentDto;
}
