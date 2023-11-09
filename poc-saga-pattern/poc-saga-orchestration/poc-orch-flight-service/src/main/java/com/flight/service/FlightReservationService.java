package com.flight.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.dto.ReservationData;
import com.flight.entity.FlightReservation;
import com.flight.producer.OrchestratorProducer;
import com.flight.repository.FlightReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FlightReservationService {

    private final FlightReservationRepository flightReservationRepository;
    private final OrchestratorProducer orchestratorProducer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("flight.request"),
                    exchange = @Exchange("flight.exchange"),
                    key = "flight"
            )
    )
    public void saveFlightReservation(String reservationData) throws JsonProcessingException {
        ReservationData flightReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        FlightReservation flightReservation = new FlightReservation();
        BeanUtils.copyProperties(flightReservationData.getFlightReservationDto(), flightReservation);
        try {
            FlightReservation entity = flightReservationRepository.save(flightReservation);
            flightReservationData.getFlightReservationDto().setId(entity.getId());
            flightReservationData.setNextStep("rental");
            orchestratorProducer.nextSaveReservationStep(flightReservationData);
        }catch (Exception e){
            if(Objects.nonNull(flightReservationData.getFlightReservationDto().getId())){
                flightReservationRepository.deleteById(flightReservationData.getFlightReservationDto().getId());
            }
            flightReservationData.setNextStep("abort-hotel-reservation");
            orchestratorProducer.nextAbortReservationStep(flightReservationData);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("flight.abort.request"),
                    exchange = @Exchange("flight.exchange"),
                    key = "flight.abort"
            )
    )
    public void abortFlightReservation(String reservationData) throws JsonProcessingException {
        ReservationData abortFlightReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        if(Objects.nonNull(abortFlightReservationData.getFlightReservationDto().getId())){
            flightReservationRepository.deleteById(abortFlightReservationData.getFlightReservationDto().getId());
        }
        abortFlightReservationData.setNextStep("abort-hotel-reservation");
        orchestratorProducer.nextAbortReservationStep(abortFlightReservationData);
    }

}
