package br.edu.infnet.product.dto;

import br.edu.infnet.product.domain.enums.Platform;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateProductRequest(
        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 2000, message = "A descrição não pode exceder 2000 caracteres")
        String description,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que zero")
        Double price,

        @NotNull(message = "A plataforma é obrigatória")
        Platform platform,

        @NotNull(message = "A quantidade em estoque é obrigatória")
        @Min(value = 0, message = "O estoque não pode ser negativo")
        Integer stockQuantity,

        @NotNull(message = "A data de lançamento é obrigatória")
        LocalDateTime releaseDate
) {
}