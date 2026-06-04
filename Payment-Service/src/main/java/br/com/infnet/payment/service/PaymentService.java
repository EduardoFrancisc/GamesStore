package br.com.infnet.payment.service;

import br.com.infnet.payment.domain.enums.PaymentStatus;
import br.com.infnet.payment.domain.model.Payment;
import br.com.infnet.payment.dto.PaymentRequest;
import br.com.infnet.payment.dto.PaymentResponse;
import br.com.infnet.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          KafkaTemplate<String, String> kafkaTemplate,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public PaymentResponse create(PaymentRequest request) {

        try {
            long tempoAleatorio = ThreadLocalRandom.current().nextLong(3000, 8000);
            Thread.sleep(tempoAleatorio);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        PaymentResponse response = toResponse(savedPayment);

        String jsonMessage = null;
        try {
            jsonMessage = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send("payment-processed", savedPayment.getOrderId().toString(), jsonMessage);
        System.out.println("Mensagem enviada com sucesso pelo KafkaConfig: " + jsonMessage);

        return response;
    }

    public PaymentResponse getById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para o ID: " + id));
        return toResponse(payment);
    }

    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PaymentResponse updateStatus(UUID id, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para o ID: " + id));

        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        return toResponse(updatedPayment);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getOrderId(),
                p.getAmount(),
                p.getStatus(),
                p.getPaymentMethod(),
                p.getCreatedAt()
        );
    }
}
