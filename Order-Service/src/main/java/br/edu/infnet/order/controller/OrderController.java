package br.edu.infnet.order.controller;

import br.edu.infnet.order.dto.CreateOrderRequest;
import br.edu.infnet.order.dto.OrderResponse;
import br.edu.infnet.order.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request){
        log.info("Novo pedido de {} recebido no {}", request.getCustomerName(), request.getPaymentMethod());
        return  orderService.create(request);
    }

    @GetMapping
    public List<OrderResponse> findAll(){
        log.info("Listando todos os pedidos.");
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderResponse findByid(@PathVariable UUID id){
        log.info("Procurando por pedido {}", id);
        return orderService.findById(id);
    }
}
