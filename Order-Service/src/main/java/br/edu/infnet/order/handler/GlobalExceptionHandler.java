package br.edu.infnet.order.handler;

import br.edu.infnet.order.exception.OrderNotFoundException;
import br.edu.infnet.order.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleOrderNotFoundException(OrderNotFoundException ex){
        Map<String, Object> body = Map.of("timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
                );
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex){
        Map<String, Object> body = Map.of("timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
        );
        return body;
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // Status 422 é ideal aqui, pois a entidade (pedido) não pode ser processada devido a um erro de semântica.
    public Map<String, Object> handleProductNotFoundException(ProductNotFoundException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 422,
                "error", "Unprocessable Entity",
                "message", ex.getMessage()
        );
    }
}
