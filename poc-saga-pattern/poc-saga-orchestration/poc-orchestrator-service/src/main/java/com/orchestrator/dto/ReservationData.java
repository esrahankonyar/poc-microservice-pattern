package com.orchestrator.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class ReservationData {
    @NotNull(message = "hotelReservationDto can not be null")
    private HotelReservationDto hotelReservationDto;
    @NotNull(message = "flightReservationDto can not be null")
    private FlightReservationDto flightReservationDto;
    @NotNull(message = "rentalDto can not be null")
    private RentalDto rentalDto;
    @NotNull(message = "paymentDto can not be null")
    private PaymentDto paymentDto;
    @NotNull(message = "nextStep can not be null")
    private String nextStep;
}
