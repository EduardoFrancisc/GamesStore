package br.com.infnet.payment.service;

import br.com.infnet.payment.domain.enums.PaymentStatus;
import br.com.infnet.payment.domain.model.Payment;
import br.com.infnet.payment.dto.PaymentRequest;
import br.com.infnet.payment.dto.PaymentResponse;
import br.com.infnet.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
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
        log.info("Ordem de pagamento recebida para o pedido: {}", request.orderId());

        // 1. Simulação de processamento bancário (Demora entre 3 e 8 segundos)
        try {
            long tempoAleatorio = ThreadLocalRandom.current().nextLong(3000, 8000);
            log.info("Processando no gateway bancário... (Estimativa: " + (tempoAleatorio / 1000) + "s)");
            Thread.sleep(tempoAleatorio);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage());
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
        log.info("Pagamento realizado com sucesso: {}", response.orderId());

        try {
            String jsonMessage = objectMapper.writeValueAsString(response);

            // Envio com Chave (Order ID) e Valor (JSON) no tópico em português
            kafkaTemplate.send("pagamentos.aprovados", savedPayment.getOrderId().toString(), jsonMessage);
            log.info("[KAFKA] Evento publicado com sucesso para o pedido: {}", response.orderId());

        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException("Erro ao serializar evento Kafka", e);
        }

        return response;
    }

    public PaymentResponse getById(UUID id) {
        log.info("Listando pagamento {}",id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para o ID: " + id));
        return toResponse(payment);
    }

    public List<PaymentResponse> getAll() {
        log.info("Listando todos os pagamentos");
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
