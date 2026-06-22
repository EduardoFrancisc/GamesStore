package br.edu.infnet.order.kafka;

import br.edu.infnet.order.domain.enums.OrderStatus;
import br.edu.infnet.order.integration.payment.dto.PaymentResponse;
import br.edu.infnet.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public PaymentEventListener(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "pagamentos.aprovados", groupId = "order-payment-confirmation")
    public void handlePaymentProcessed(String messageJson) throws Exception {

        log.info("Mensagem Kafka recebida: {}", messageJson);

        PaymentResponse payment = objectMapper.readValue(messageJson, PaymentResponse.class);

        orderRepository.findById(payment.orderId()).ifPresent(order -> {

            if (order.getOrderStatus() == OrderStatus.PENDING) {
                switch (payment.status()) {
                    case APPROVED -> order.setOrderStatus(OrderStatus.CONFIRMED);
                    case REJECTED -> order.setOrderStatus(OrderStatus.CANCELED);
                }
                orderRepository.save(order);
                log.info("Status da Order atualizado pelo Kafka para: {}", order.getOrderStatus());
            } else {
                log.info("Order {} já resolvida. Evento ignorado.", order.getId());
            }

        });
    }
}