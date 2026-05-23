package br.edu.infnet.order.integration.payment.dto;

import br.edu.infnet.order.integration.payment.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull
        UUID orderId,
        @Min(0)
        @NotNull
        BigDecimal amount,
        @NotNull(message = "O pedido deve ter o método de pagamento")
        PaymentMethod paymentMethod
) {}
