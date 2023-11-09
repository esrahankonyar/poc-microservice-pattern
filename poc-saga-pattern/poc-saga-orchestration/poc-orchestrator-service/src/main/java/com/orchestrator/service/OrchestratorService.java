package com.orchestrator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchestrator.dto.ReservationData;
import com.orchestrator.producer.FlightReservationProducer;
import com.orchestrator.producer.HotelReservationProducer;
import com.orchestrator.producer.PaymentProducer;
import com.orchestrator.producer.RentalProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final HotelReservationProducer hotelReservationProducer;
    private final FlightReservationProducer flightReservationProducer;
    private final RentalProducer rentalProducer;
    private final PaymentProducer paymentProducer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("reservation.operation.request"),
                    exchange = @Exchange("reservation.operation"),
                    key = "reservation"
            )
    )
    public void saveReservationOperation (String reservationRequest) throws JsonProcessingException {
        ReservationData reservationData = new ObjectMapper().readValue(reservationRequest, ReservationData.class);
        String nextStep = reservationData.getNextStep();
        if(Objects.equals("hotel-reservation", nextStep)){
            hotelReservationProducer.saveHotelReservationRequest(reservationRequest);
            return;
        }
        if (Objects.equals("flight-reservation", nextStep)
                && Objects.nonNull(reservationData.getHotelReservationDto().getId())) {
            flightReservationProducer.saveFlightReservation(reservationRequest);
            return;
        }
        if (Objects.equals("rental", nextStep)
                && Objects.nonNull(reservationData.getHotelReservationDto().getId())
                && Objects.nonNull(reservationData.getFlightReservationDto().getId())) {
            rentalProducer.saveRentalRequest(reservationRequest);
            return;
        }
        if (Objects.equals("payment", nextStep)
                && Objects.nonNull(reservationData.getHotelReservationDto().getId())
                && Objects.nonNull(reservationData.getFlightReservationDto().getId())
                && Objects.nonNull(reservationData.getRentalDto().getId())) {
            paymentProducer.savePaymentRequest(reservationRequest);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("abort.reservation.operation.request"),
                    exchange = @Exchange("reservation.operation"),
                    key = "abort.reservation"
            )
    )
    public void abortReservationOperation (String abortReservationRequest) throws JsonProcessingException {
        ReservationData abortReservationData = new ObjectMapper().readValue(abortReservationRequest, ReservationData.class);
        String nextStep = abortReservationData.getNextStep();
        if(Objects.equals("abort-hotel-reservation", nextStep)){
            hotelReservationProducer.abortHotelReservationRequest(abortReservationRequest);
            return;
        }
        if (Objects.equals("abort-flight-reservation", nextStep)) {
            flightReservationProducer.abortFlightReservation(abortReservationRequest);
            return;
        }
        if (Objects.equals("abort-rental", nextStep)) {
            rentalProducer.abortRentalRequest(abortReservationRequest);
        }
    }

}
