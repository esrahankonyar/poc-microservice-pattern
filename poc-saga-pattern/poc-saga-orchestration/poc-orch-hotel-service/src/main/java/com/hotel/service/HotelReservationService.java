package com.hotel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.dto.HotelReservationDto;
import com.hotel.dto.ReservationData;
import com.hotel.entity.HotelReservation;
import com.hotel.producer.OrchestratorProducer;
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
    private final OrchestratorProducer orchestratorProducer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("hotel.request"),
                    exchange = @Exchange("hotel.exchange"),
                    key = "hotel"
            )
    )
    public void saveHotelReservation(String reservationData) throws JsonProcessingException {
        ReservationData hotelReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        HotelReservationDto hotelReservationDto = hotelReservationData.getHotelReservationDto();
        HotelReservation hotelReservation = new HotelReservation();
        BeanUtils.copyProperties(hotelReservationDto, hotelReservation);
        try{
            HotelReservation entity = hotelReservationRepository.save(hotelReservation);
            hotelReservationData.getHotelReservationDto().setId(entity.getId());
            hotelReservationData.setNextStep("flight-reservation");
            orchestratorProducer.nextSaveReservationStep(hotelReservationData);
        }catch (Exception exception){
            if(Objects.nonNull(hotelReservationData.getHotelReservationDto().getId())){
                hotelReservationRepository.deleteById(hotelReservationData.getHotelReservationDto().getId());
            }
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("hotel.abort.request"),
                    exchange = @Exchange("hotel.exchange"),
                    key = "hotel.abort"
            )
    )
    public void abortHotelReservation(String abortReservationData) throws JsonProcessingException {
        ReservationData reservationData = new ObjectMapper().readValue(abortReservationData, ReservationData.class);
        if (Objects.nonNull(reservationData.getFlightReservationDto().getId())){
            hotelReservationRepository.deleteById(reservationData.getFlightReservationDto().getId());
        }
    }
}
