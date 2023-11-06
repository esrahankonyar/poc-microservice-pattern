package com.flight.service;

import com.flight.dto.FlightReservationDto;
import com.flight.dto.ReservationData;
import com.flight.entity.FlightReservation;
import com.flight.producer.HotelReservationProducer;
import com.flight.producer.RentalProducer;
import com.flight.repository.FlightReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightReservationService {

    private final FlightReservationRepository flightReservationRepository;
    private final HotelReservationProducer hotelReservationProducer;
    private final RentalProducer rentalProducer;

    @RabbitListener(bindings =
        @QueueBinding(
            value = @Queue(value = "Flight.reservation.request"),
            exchange = @Exchange(value = "flight.reservation.exchange"),
                key = "flight.reservation"
        )
    )
    public void createFlightReservation (ReservationData reservationData){
        FlightReservationDto flightReservationDto = reservationData.getFlightReservationDto();
        FlightReservation entity = new FlightReservation();
        BeanUtils.copyProperties(flightReservationDto, entity);
        try {
            FlightReservation flightReservation = flightReservationRepository.save(entity);
            reservationData.getFlightReservationDto().setId(flightReservation.getId());
            rentalProducer.rentalSave(reservationData);
        }catch (Exception exception){
            if(Objects.nonNull(flightReservationDto.getFlightId())){
                flightReservationRepository.deleteById(flightReservationDto.getId());
            }
            hotelReservationProducer.abortHotelReservation(reservationData);
        }

    }


}
