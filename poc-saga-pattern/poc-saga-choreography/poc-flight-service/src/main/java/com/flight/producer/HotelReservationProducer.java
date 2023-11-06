package com.flight.producer;

import com.flight.dto.ReservationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelReservationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void abortHotelReservation(ReservationData reservationData){
        log.info("[x] Requesting hotel reservation abort({})", reservationData);
        rabbitTemplate.convertAndSend("hotel.reservation.exchange",
                "hotel.reservation.abort", reservationData);
    }

}
