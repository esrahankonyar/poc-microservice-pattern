package com.hotel.producer;

import com.hotel.dto.ReservationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightReservationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void flightReservationSave(ReservationData reservationData){
        log.info("[x] Requesting flight reservation({})", reservationData);
        rabbitTemplate.convertAndSend("flight.reservation.exchange",
                "flight.reservation.request", reservationData);
    }


}
