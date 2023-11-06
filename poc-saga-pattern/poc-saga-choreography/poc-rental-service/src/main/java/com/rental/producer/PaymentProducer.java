package com.rental.producer;

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

    public void savePayment(ReservationData reservationData){
        log.info("[x] Requesting payment reservation request({})", reservationData);
        rabbitTemplate.convertAndSend("payment.exchange", "payment.request", reservationData);
    }

}
