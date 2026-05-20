package br.edu.infnet.order.service;

import br.edu.infnet.order.domain.enums.OrderStatus;
import br.edu.infnet.order.domain.model.Order;
import br.edu.infnet.order.domain.model.OrderItem;
import br.edu.infnet.order.dto.CreateOrderRequest;
import br.edu.infnet.order.dto.OrderItemDTO;
import br.edu.infnet.order.dto.OrderResponse;
import br.edu.infnet.order.integration.payment.client.PaymentClient;
import br.edu.infnet.order.integration.payment.dto.PaymentRequest;
import br.edu.infnet.order.integration.product.client.ProductClient;
import br.edu.infnet.order.integration.product.dto.ProductResponse;
import br.edu.infnet.order.repository.OrderRepository;
import br.edu.infnet.order.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient  productClient;
    private final PaymentClient paymentClient;

    public OrderService(
            OrderRepository orderRepository,
            ProductClient productClient, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.paymentClient = paymentClient;
    }

    public OrderResponse create(CreateOrderRequest request) {
        Order o = new Order();
        o.setCustomerName(request.getCustomerName());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDTO item : request.getItems()) {
            ProductResponse p = productClient.getProductById(item.productId());

            OrderItem oi = new OrderItem();
            oi.setProductId(p.id());
            oi.setQuantity(item.quantity());
            oi.setUnitPrice(p.price());
            items.add(oi);

            total = total.add(
                    BigDecimal.valueOf(oi.getQuantity())
                            .multiply(oi.getUnitPrice())
            );
        }

        o.setItems(items);
        o.setOrderDate(LocalDateTime.now());
        o.setOrderStatus(OrderStatus.PENDING);
        o.setTotalAmount(total);

        //Retira os itens do stock
        productClient.reduceProductQuantityStock(request.getItems());

        //pagamento
        paymentClient.create(new PaymentRequest(
                o.getId(),
                o.getTotalAmount(),
                o.getPaymentMethod()
        ));

        orderRepository.save(o);

        return toResponse(o);
    }

    public OrderResponse findById(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
        return toResponse(order);
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    private OrderResponse toResponse(Order o){
        return new OrderResponse(
                o.getId(),
                o.getCustomerName(),
                o.getOrderDate(),
                o.getOrderStatus(),
                o.getTotalAmount(),
                o.getItems()
        );
    }

}
