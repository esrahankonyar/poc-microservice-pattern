package com.hotel.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.dto.ReservationData;
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

}
