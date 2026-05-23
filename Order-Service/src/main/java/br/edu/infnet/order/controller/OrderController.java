package br.edu.infnet.order.controller;

import br.edu.infnet.order.dto.CreateOrderRequest;
import br.edu.infnet.order.dto.OrderResponse;
import br.edu.infnet.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request){
        return  orderService.create(request);
    }

    @GetMapping
    public List<OrderResponse> findAll(){
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderResponse findByid(@PathVariable UUID id){
        return orderService.findById(id);
    }
}
