package com.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.dto.PaymentDto;
import com.payment.dto.ReservationData;
import com.payment.entity.Payment;
import com.payment.producer.RentalProducer;
import com.payment.repository.PaymentRepository;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final RentalProducer rentalProducer;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue("paymet.request"),
                    exchange = @Exchange("payment.exchange"),
                    key = "payment"
            )
    )
    public void savePayment(String reservationData) throws JsonProcessingException {
        ReservationData paymentRequest = new ObjectMapper().readValue(reservationData, ReservationData.class);
        PaymentDto paymentDto = paymentRequest.getPaymentDto();
        Payment entity = new Payment();
        BeanUtils.copyProperties(paymentDto, entity);
        try {
            Payment payment = paymentRepository.save(entity);
            paymentRequest.getPaymentDto().setId(payment.getId());
        }catch (Exception exception){
            if(Objects.nonNull(paymentRequest.getPaymentDto().getId())){
                paymentRepository.deleteById(paymentRequest.getPaymentDto().getId());
            }
            rentalProducer.abortRental(paymentRequest);
        }
    }

}
