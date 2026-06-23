package br.edu.infnet.order.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
        log.error(message);
    }
}
