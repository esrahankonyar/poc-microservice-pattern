package com.flight.repository;

import com.flight.entity.FlightReservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightReservationRepository extends CrudRepository<FlightReservation, Long> {
}
