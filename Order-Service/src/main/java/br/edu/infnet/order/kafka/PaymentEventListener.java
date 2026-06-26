package br.edu.infnet.order.kafka;

import br.edu.infnet.order.domain.enums.OrderStatus;
import br.edu.infnet.order.dto.OrderItemDTO;
import br.edu.infnet.order.integration.payment.dto.PaymentResponse;
import br.edu.infnet.order.integration.product.client.ProductClient;
import br.edu.infnet.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class PaymentEventListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final ProductClient productClient;

    public PaymentEventListener(OrderRepository orderRepository, ObjectMapper objectMapper, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
        this.productClient = productClient;
    }

    @Transactional
    @KafkaListener(topics = "pagamentos.aprovados", groupId = "order-payment-confirmation")
    public void handlePaymentProcessed(String messageJson) throws Exception {

        log.info("Mensagem Kafka recebida: {}", messageJson);

        PaymentResponse payment = objectMapper.readValue(messageJson, PaymentResponse.class);

        orderRepository.findById(payment.orderId()).ifPresent(order -> {

            if (order.getOrderStatus() == OrderStatus.PENDING) {
                switch (payment.status()) {
                    case APPROVED -> {
                        order.setOrderStatus(OrderStatus.CONFIRMED);

                        List<OrderItemDTO> dtos = order.getItems().stream()
                                .map(item -> new OrderItemDTO(item.getProductId(), item.getQuantity()))
                                .toList();
                        
                        try {
                            productClient.reduceProductQuantityStock(dtos);
                            log.info("Estoque reduzido via Kafka com sucesso para o pedido: {}", order.getId());
                        } catch (Exception e) {
                            log.error("Erro ao reduzir estoque no processamento assíncrono do pedido: {}", e.getMessage());
                        }
                    }
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