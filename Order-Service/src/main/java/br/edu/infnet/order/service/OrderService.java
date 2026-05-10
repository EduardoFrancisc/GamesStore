package br.edu.infnet.order.service;

import br.edu.infnet.order.domain.enums.OrderStatus;
import br.edu.infnet.order.domain.model.Order;
import br.edu.infnet.order.domain.model.OrderItem;
import br.edu.infnet.order.dto.CreateOrderRequest;
import br.edu.infnet.order.dto.OrderResponse;
import br.edu.infnet.order.repository.OrderRepository;
import br.edu.infnet.order.exception.OrderNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse create(CreateOrderRequest request) {
        ModelMapper modelMapper = new ModelMapper();
        Order order = modelMapper.map(request, Order.class);

        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);

        // No futuro, chamar o Product-Service via RestClient
        BigDecimal total = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setUnitPrice(100.0); // Preço provisório até integrar com o Product-Service
                BigDecimal itemTotal = BigDecimal.valueOf(item.getUnitPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
            }
        }
        order.setTotalAmount(total); // Valor total do pedido

        Order save = orderRepository.save(order);
        return toResponse(save);
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
