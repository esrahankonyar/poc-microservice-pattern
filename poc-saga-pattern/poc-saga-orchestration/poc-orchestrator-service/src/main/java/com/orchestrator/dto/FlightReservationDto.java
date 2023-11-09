package com.orchestrator.dto;

import lombok.Data;


@Data
public class FlightReservationDto {
    private Long id;
    private Long flightId;
    private Long clientId;
    private Long seatId;
}
