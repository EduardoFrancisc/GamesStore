package br.edu.infnet.order.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String productId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private Double unitPrice;
}
