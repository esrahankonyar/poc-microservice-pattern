package com.rental.producer;

import com.rental.dto.ReservationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightReservationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void abortFlightReservation (ReservationData reservationData){
        log.info("[x] Requesting flight abort request({})", reservationData);
        rabbitTemplate.convertAndSend("flight.exchange", "flight.abort.request", reservationData);
    }

}
