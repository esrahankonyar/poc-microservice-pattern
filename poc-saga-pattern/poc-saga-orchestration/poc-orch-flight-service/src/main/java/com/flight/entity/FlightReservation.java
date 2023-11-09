package com.flight.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "flight_reservation")
@Data
public class FlightReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_id")
    private Long flightId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "seat_id")
    private Long seatId;

}
