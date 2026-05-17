package br.edu.infnet.order.integration.product.dto;

import br.edu.infnet.order.integration.product.enums.Platform;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        String id,
        String title,
        String description,
        BigDecimal price,
        Platform platform, // Mudou aqui
        Integer stockQuantity,
        LocalDateTime releaseDate
) {
}
