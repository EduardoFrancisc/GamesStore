package br.edu.infnet.order.dto;

import br.edu.infnet.order.domain.enums.OrderStatus;
import br.edu.infnet.order.domain.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String customerName,
        LocalDateTime orderDate,
        OrderStatus orderStatus,
        BigDecimal totalAmount,
        List<OrderItem> Items
) {
}
