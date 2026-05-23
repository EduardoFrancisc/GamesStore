package br.edu.infnet.order.exception;

import java.util.UUID;

public class OrderPaymentTimeoutException extends RuntimeException {
    private final UUID orderId;

    public OrderPaymentTimeoutException(String message, UUID orderId) {
        super(message);
        this.orderId = orderId;
    }
}
