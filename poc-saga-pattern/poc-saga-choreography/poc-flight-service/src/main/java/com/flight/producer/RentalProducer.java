package com.flight.producer;

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

    public void rentalSave(ReservationData reservationData){
        log.info("[x] Requesting rental booking({})", reservationData);
        rabbitTemplate.convertAndSend("rental.exchange", "rental.request", reservationData);
    }

}
