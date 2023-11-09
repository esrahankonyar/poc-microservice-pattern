package com.orchestrator.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightReservationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void saveFlightReservation(String flightReservationRequest){
        log.info("[x] Requesting flight reservation ({})", flightReservationRequest);
        rabbitTemplate.convertAndSend("flight.exchange", "flight", flightReservationRequest);
    }

    public void abortFlightReservation(String abortFlightReservationRequest){
        log.info("[x] Requesting abort flight reservation ({})", abortFlightReservationRequest);
        rabbitTemplate.convertAndSend("flight.exchange", "flight.abort", abortFlightReservationRequest);
    }

}
