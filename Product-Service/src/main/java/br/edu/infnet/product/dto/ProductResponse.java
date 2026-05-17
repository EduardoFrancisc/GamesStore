package br.edu.infnet.product.dto;

// ... outros imports ...
import br.edu.infnet.product.domain.enums.Platform;

import java.time.LocalDateTime;

public record ProductResponse(
        String id,
        String title,
        String description,
        Double price,
        Platform platform, // Mudou aqui
        Integer stockQuantity,
        LocalDateTime releaseDate
) {
}