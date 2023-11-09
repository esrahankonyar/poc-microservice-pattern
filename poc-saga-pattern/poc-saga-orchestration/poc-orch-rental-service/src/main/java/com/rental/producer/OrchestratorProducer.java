package com.rental.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.dto.ReservationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrchestratorProducer {

    private final RabbitTemplate rabbitTemplate;

    public void nextSaveReservationStep(ReservationData reservationData) throws JsonProcessingException {
        log.info("[x] Requesting next reservation ({})", reservationData);
        String nextStep = new ObjectMapper().writeValueAsString(reservationData);
        rabbitTemplate.convertAndSend("reservation.operation", "reservation", nextStep);
    }

    public void nextAbortReservationStep(ReservationData aborteservationData) throws JsonProcessingException {
        log.info("[x] Requesting next abort ({})", aborteservationData);
        String nextAbortStep = new ObjectMapper().writeValueAsString(aborteservationData);
        rabbitTemplate.convertAndSend("reservation.operation", "abort.reservation", nextAbortStep);
    }

}
