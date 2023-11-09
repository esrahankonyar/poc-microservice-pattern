package com.flight.repository;

import com.flight.entity.FlightReservation;
import org.springframework.data.repository.CrudRepository;

public interface FlightReservationRepository extends CrudRepository<FlightReservation, Long> {
}
