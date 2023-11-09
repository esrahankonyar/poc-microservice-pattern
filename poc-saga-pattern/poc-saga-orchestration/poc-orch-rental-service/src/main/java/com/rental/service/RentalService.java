package com.rental.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rental.dto.ReservationData;
import com.rental.entity.Rental;
import com.rental.producer.OrchestratorProducer;
import com.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final OrchestratorProducer orchestratorProducer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("rental.request"),
                    exchange = @Exchange("rental.exchange"),
                    key = "rental"
            )
    )
    public void saveRentalReservation(String reservationData) throws JsonProcessingException {
        ReservationData rentalReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        Rental rental = new Rental();
        BeanUtils.copyProperties(rentalReservationData.getRentalDto(), rental);
        try {
            Rental entity = rentalRepository.save(rental);
            rentalReservationData.getRentalDto().setId(entity.getId());
            rentalReservationData.setNextStep("payment");
            orchestratorProducer.nextSaveReservationStep(rentalReservationData);
        }catch (Exception e){
            if(Objects.nonNull(rentalReservationData.getRentalDto().getId())){
                rentalRepository.deleteById(rentalReservationData.getRentalDto().getId());
            }
            rentalReservationData.setNextStep("abort-flight-reservation");
            orchestratorProducer.nextAbortReservationStep(rentalReservationData);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("rental.abort.request"),
                    exchange = @Exchange("rental.exchange"),
                    key = "rental.abort"
            )
    )
    public void abortRentalReservation(String reservationData) throws JsonProcessingException {
        ReservationData abortRentalReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        if(Objects.nonNull(abortRentalReservationData.getRentalDto().getId())){
            rentalRepository.deleteById(abortRentalReservationData.getRentalDto().getId());
        }
        abortRentalReservationData.setNextStep("abort-flight-reservation");
        orchestratorProducer.nextAbortReservationStep(abortRentalReservationData);
    }


}
