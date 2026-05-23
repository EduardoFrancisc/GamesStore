package br.edu.infnet.order.handler;

import br.edu.infnet.order.exception.OrderNotFoundException;
import br.edu.infnet.order.exception.OrderPaymentTimeoutException;
import br.edu.infnet.order.integration.product.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.net.http.HttpTimeoutException;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleOrderNotFoundException(OrderNotFoundException ex){
        return Map.of("timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", ex.getMessage()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex){
        return Map.of("timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, Object> handleProductNotFoundException(ProductNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "error", "Unprocessable Entity",
                "message", ex.getMessage()
        );
    }
/*
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    public Map<String, Object> handleHttpClientError(HttpClientErrorException ex) {
        return Map.of(
                "message", ex.getMessage()
        );
    }
*/
    @ExceptionHandler(HttpTimeoutException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    public Map<String, String> handlerTimeout(HttpTimeoutException ex) {
        return Map.of(
                "message", "Sistema temporariamente indisponível."
        );
    }

    @ExceptionHandler(OrderPaymentTimeoutException.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public Map<String, Object> handleOrderPaymentTimeout( OrderPaymentTimeoutException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.GATEWAY_TIMEOUT.value(),
                "error", "Gateway Timeout",
                "message", ex.getMessage()
        );
    }
}
