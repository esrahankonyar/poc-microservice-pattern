package com.flight.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.dto.ReservationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalProducer {

    private final RabbitTemplate rabbitTemplate;

    public void rentalSave(ReservationData reservationData) throws JsonProcessingException {
        String reservationDataEdit = new ObjectMapper().writeValueAsString(reservationData);
        log.info("[x] Requesting rental booking({})", reservationData);
        rabbitTemplate.convertAndSend("rental.exchange", "rental", reservationDataEdit);
    }

}
