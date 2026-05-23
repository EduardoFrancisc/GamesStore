package br.edu.infnet.order.integration.payment.dto;


import br.edu.infnet.order.integration.payment.enums.PaymentMethod;
import br.edu.infnet.order.integration.payment.enums.PaymentStatus;

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
