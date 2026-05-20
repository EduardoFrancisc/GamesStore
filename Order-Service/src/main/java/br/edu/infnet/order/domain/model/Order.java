package br.edu.infnet.order.domain.model;

import br.edu.infnet.order.domain.enums.OrderStatus;
import br.edu.infnet.order.integration.payment.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 100)
    private String customerName;
    @Column(nullable = false)
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id") // chave estrangeira na tabela order_items
    private List<OrderItem> items;
}
