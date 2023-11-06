package com.hotel.repository;

import com.hotel.entity.HotelReservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelReservationRepository extends CrudRepository<HotelReservation, Long> {
}
