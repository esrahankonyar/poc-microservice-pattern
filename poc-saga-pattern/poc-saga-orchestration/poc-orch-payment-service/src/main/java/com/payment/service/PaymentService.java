package com.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.dto.ReservationData;
import com.payment.entity.Payment;
import com.payment.producer.OrchestratorProducer;
import com.payment.repository.PaymentRepository;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrchestratorProducer orchestratorProducer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("payment.request"),
                    exchange = @Exchange("payment.exchange"),
                    key = "payment"
            )
    )
    public void savePaymentReservation(String reservationData) throws JsonProcessingException {
        ReservationData paymentReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentReservationData.getPaymentDto(), payment);
        try {
            Payment entity = paymentRepository.save(payment);
            paymentReservationData.getPaymentDto().setId(entity.getId());
        }catch (Exception e){
            if(Objects.nonNull(paymentReservationData.getPaymentDto().getId())){
                paymentRepository.deleteById(paymentReservationData.getPaymentDto().getId());
            }
            paymentReservationData.setNextStep("abort-rental");
            orchestratorProducer.nextAbortReservationStep(paymentReservationData);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("payment.abort.request"),
                    exchange = @Exchange("payment.exchange"),
                    key = "payment.abort"
            )
    )
    public void abortPaymentReservation(String reservationData) throws JsonProcessingException {
        ReservationData abortPaymentReservationData = new ObjectMapper().readValue(reservationData, ReservationData.class);
        if(Objects.nonNull(abortPaymentReservationData.getPaymentDto().getId())){
            paymentRepository.deleteById(abortPaymentReservationData.getPaymentDto().getId());
        }
        abortPaymentReservationData.setNextStep("abort-rental");
        orchestratorProducer.nextAbortReservationStep(abortPaymentReservationData);
    }

}
