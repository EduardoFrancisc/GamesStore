package br.edu.infnet.order.dto;

import br.edu.infnet.order.domain.model.OrderItem;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateOrderRequest {
    @NotBlank(message = "O nome do cliente é obrigatório.")
    @Max(value = 100, message = "O nome do cliente não pode ser tão grande.")
    String customerName;
    @NotEmpty(message = "O pedido deve conter pelo menos um item.")
    List<OrderItemRequest> items;
}


