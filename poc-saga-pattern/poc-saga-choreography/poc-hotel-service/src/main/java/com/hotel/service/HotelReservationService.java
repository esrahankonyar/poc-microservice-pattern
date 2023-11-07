package com.hotel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.dto.HotelReservationDto;
import com.hotel.dto.ReservationData;
import com.hotel.entity.HotelReservation;
import com.hotel.producer.FlightReservationProducer;
import com.hotel.repository.HotelReservationRepository;
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
public class HotelReservationService {

    private final HotelReservationRepository hotelReservationRepository;
    private final FlightReservationProducer flightReservationProducer;

    public void createHotelReservation(ReservationData reservationData ){
        HotelReservationDto hotelReservationDto = reservationData.getHotelReservationDto();
        HotelReservation hotelReservation = new HotelReservation();
        BeanUtils.copyProperties(hotelReservationDto, hotelReservation);
        HotelReservation reservation = new HotelReservation();
        try {
            reservation = hotelReservationRepository.save(hotelReservation);
            BeanUtils.copyProperties(hotelReservation, hotelReservationDto);
            reservationData.setHotelReservationDto(hotelReservationDto);
            flightReservationProducer.flightReservationSave(reservationData);
        }catch (Exception exception){
            if(Objects.nonNull(reservation.getId())){
                hotelReservationRepository.deleteById(reservation.getId());
            }
        }
    }

    @RabbitListener(
            bindings =
                    @QueueBinding(
                            value = @Queue(value = "hotel.abort.request"),
                            exchange = @Exchange(value = "hotel.exchange"),
                            key = "hotel.abort"
                    )
    )
    public void abortHotelReservation(String reservationData) throws JsonProcessingException {
        log.info("Hotel reservation is deleted cause of flight reservation aborted");
        ReservationData abortHotelRequest = new ObjectMapper().readValue(reservationData, ReservationData.class);
        if (Objects.nonNull(abortHotelRequest.getHotelReservationDto())
                && Objects.nonNull(abortHotelRequest.getHotelReservationDto().getHotelId())){
            hotelReservationRepository.deleteById(abortHotelRequest.getHotelReservationDto().getId());
        }
    }

}
