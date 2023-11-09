package com.orchestrator.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RentalProducer {

    private final RabbitTemplate rabbitTemplate;

    public void saveRentalRequest(String rentalData){
        log.info("[x] Requesting rental ({})", rentalData);
        rabbitTemplate.convertAndSend("rental.exchange", "rental", rentalData);
    }

    public void abortRentalRequest(String abortRentalData){
        log.info("[x] Requesting abort rental ({})", abortRentalData);
        rabbitTemplate.convertAndSend("rental.exchange", "rental.abort", abortRentalData);
    }

}
