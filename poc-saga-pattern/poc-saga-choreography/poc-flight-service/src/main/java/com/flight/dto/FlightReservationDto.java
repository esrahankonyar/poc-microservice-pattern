package com.flight.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class FlightReservationDto {
    private Long id;
    private Long flightId;
    private Long clientId;
    private Long seatId;
}
