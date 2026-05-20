package br.com.infnet.payment.dto;

import br.com.infnet.payment.domain.enums.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID orderId,
        @NotNull @Min(0) BigDecimal amount,
        @NotNull PaymentMethod paymentMethod
) {}
