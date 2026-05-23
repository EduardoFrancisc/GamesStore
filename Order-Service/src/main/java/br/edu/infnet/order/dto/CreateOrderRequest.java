package br.edu.infnet.order.dto;

import br.edu.infnet.order.integration.payment.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "O nome do cliente é obrigatório.")
    @Size(max = 100, message = "O nome do cliente não pode ser tão grande.")
    String customerName;
    @NotNull(message = "O pedido deve ter o método de pagamento.")
    PaymentMethod paymentMethod;
    @NotEmpty(message = "O pedido deve conter pelo menos um item.")
    @Valid
    List<OrderItemDTO> items;
}


