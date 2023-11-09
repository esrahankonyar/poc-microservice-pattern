package com.orchestrator.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelReservationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void saveHotelReservationRequest(String hotelReservationData){
        log.info("[x] Requesting hotel reservation ({})", hotelReservationData);
        rabbitTemplate.convertAndSend("hotel.exchange", "hotel", hotelReservationData);
    }

    public void abortHotelReservationRequest(String abortHotelReservationData){
        log.info("[x] Requesting abort hotel reservation ({})", abortHotelReservationData);
        rabbitTemplate.convertAndSend("hotel.exchange", "hotel.abort", abortHotelReservationData);
    }

}
