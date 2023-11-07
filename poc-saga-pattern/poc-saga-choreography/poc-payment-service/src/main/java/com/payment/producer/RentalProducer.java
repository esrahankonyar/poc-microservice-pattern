package com.payment.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.dto.ReservationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalProducer {

    private final RabbitTemplate rabbitTemplate;

    public void abortRental(ReservationData reservationData) throws JsonProcessingException {
        log.info("[x] Requesting rantal({})", reservationData);
        String rentalAbortRequest = new ObjectMapper().writeValueAsString(reservationData);
        rabbitTemplate.convertAndSend("rental.exchange", "rental.abort", rentalAbortRequest);
    }

}
