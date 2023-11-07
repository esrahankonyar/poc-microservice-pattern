package com.hotel.controller;

import com.hotel.dto.ReservationData;
import com.hotel.service.HotelReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelReservationController {

    @Autowired
    HotelReservationService hotelReservationService;

    @PostMapping(value = "/reservation")
    public void createHotelReservation (@RequestBody ReservationData reservationData){
        hotelReservationService.createHotelReservation(reservationData);
    }

}
