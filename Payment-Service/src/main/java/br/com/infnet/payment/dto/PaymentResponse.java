package br.com.infnet.payment.dto;

import br.com.infnet.payment.domain.enums.PaymentMethod;
import br.com.infnet.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod paymentMethod,
        LocalDateTime createdAt
) {}
