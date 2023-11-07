package com.flight.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @RabbitListener(
            bindings =
                @QueueBinding(
                        value = @Queue(value = "flight.request"),
                        exchange = @Exchange(value = "flight.exchange"),
                        key = "flight"
                )
    )
    public void createFlightReservation (String reservationData) throws JsonProcessingException {
        ReservationData flightReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        FlightReservationDto flightReservationDto = flightReservationData.getFlightReservationDto();
        FlightReservation entity = new FlightReservation();
        BeanUtils.copyProperties(flightReservationDto, entity);
        try {
            FlightReservation flightReservation = flightReservationRepository.save(entity);
            flightReservationData.getFlightReservationDto().setId(flightReservation.getId());
            rentalProducer.rentalSave(flightReservationData);
        }catch (Exception exception){
            if(Objects.nonNull(flightReservationData.getFlightReservationDto().getFlightId())){
                flightReservationRepository.deleteById(flightReservationDto.getId());
            }
            hotelReservationProducer.abortHotelReservation(flightReservationData);
        }

    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("flight.abort.request"),
                    exchange = @Exchange("flight.exchange"),
                    key = "flight.abort"
            )
    )
    public void abortFlight(String reservationData) throws JsonProcessingException {
        ReservationData abortFlightRequest = new ObjectMapper().readValue(reservationData, ReservationData.class);
        if(Objects.nonNull(abortFlightRequest.getFlightReservationDto().getId())){
            flightReservationRepository.deleteById(abortFlightRequest.getFlightReservationDto().getId());
        }
        hotelReservationProducer.abortHotelReservation(abortFlightRequest);
    }


}
