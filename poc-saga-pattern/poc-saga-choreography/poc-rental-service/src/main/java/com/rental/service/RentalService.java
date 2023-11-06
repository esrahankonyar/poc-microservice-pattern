package com.rental.service;

import com.rental.dto.RentalDto;
import com.rental.dto.ReservationData;
import com.rental.entity.Rental;
import com.rental.producer.FlightReservationProducer;
import com.rental.producer.PaymentProducer;
import com.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {

    private final RentalRepository rentalRepository;

    private final PaymentProducer paymentProducer;

    private final FlightReservationProducer flightReservationProducer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "rental.request"),
                    exchange = @Exchange(value = "rental.exchange"),
                    key = "rental.request"
            )
    )
    public void saveRental(ReservationData reservationData){
        RentalDto rentalDto = reservationData.getRentalDto();
        Rental entity = new Rental();
        BeanUtils.copyProperties(rentalDto, entity);
        try {
            Rental rental = rentalRepository.save(entity);
            reservationData.getRentalDto().setId(rental.getId());
            paymentProducer.savePayment(reservationData);
        }catch (Exception ex){
            if(Objects.nonNull(reservationData.getRentalDto().getId())){
                rentalRepository.deleteById(reservationData.getRentalDto().getId());
            }
            flightReservationProducer.abortFlightReservation(reservationData);
        }
    }

}
