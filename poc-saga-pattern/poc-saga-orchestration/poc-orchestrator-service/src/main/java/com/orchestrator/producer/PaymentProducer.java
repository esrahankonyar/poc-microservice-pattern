package com.orchestrator.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final RabbitTemplate rabbitTemplate;

    public void savePaymentRequest(String paymentData){
        log.info("[x] Requesting payment ({})", paymentData);
        rabbitTemplate.convertAndSend("payment.exchange", "payment", paymentData);
    }

}
