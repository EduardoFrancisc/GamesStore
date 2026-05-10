package br.edu.infnet.order.service;

import br.edu.infnet.order.domain.model.Order;
import br.edu.infnet.order.dto.CreateOrderRequest;
import br.edu.infnet.order.dto.OrderResponse;
import br.edu.infnet.order.repository.OrderRepository;
import br.edu.infnet.order.exception.OrderNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
