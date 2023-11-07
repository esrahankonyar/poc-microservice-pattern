package com.rental.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                    key = "rental"
            )
    )
    public void saveRental(String reservationData) throws JsonProcessingException {
        ReservationData rentalReservationRequest = new ObjectMapper().readValue(reservationData, ReservationData.class);
        RentalDto rentalDto = rentalReservationRequest.getRentalDto();
        Rental entity = new Rental();
        BeanUtils.copyProperties(rentalDto, entity);
        try {
            Rental rental = rentalRepository.save(entity);
            rentalReservationRequest.getRentalDto().setId(rental.getId());
            paymentProducer.savePayment(rentalReservationRequest);
        }catch (Exception ex){
            if(Objects.nonNull(rentalReservationRequest.getRentalDto().getId())){
                rentalRepository.deleteById(rentalReservationRequest.getRentalDto().getId());
            }
            flightReservationProducer.abortFlightReservation(rentalReservationRequest);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("rental.abort.request"),
                    exchange = @Exchange("rental.exchange"),
                    key = "rental.abort"
            )
    )
    public void abortRental(String reservationData) throws JsonProcessingException {
        ReservationData abortRentalRequest = new ObjectMapper().readValue(reservationData, ReservationData.class);
        if(Objects.nonNull(abortRentalRequest.getRentalDto().getId())){
            rentalRepository.deleteById(abortRentalRequest.getRentalDto().getId());
        }
        flightReservationProducer.abortFlightReservation(abortRentalRequest);
    }

}
