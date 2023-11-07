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
public class PaymentProducer {

    private final RabbitTemplate rabbitTemplate;

    public void savePayment(ReservationData reservationData) throws JsonProcessingException {
        log.info("[x] Requesting payment reservation request({})", reservationData);
        String paymentRequest = new ObjectMapper().writeValueAsString(reservationData);
        rabbitTemplate.convertAndSend("payment.exchange", "payment", paymentRequest);
    }

}
