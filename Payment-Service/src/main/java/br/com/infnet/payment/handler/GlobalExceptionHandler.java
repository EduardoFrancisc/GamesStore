package br.com.infnet.payment.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    public Map<String, Object> handleHttpClientError(HttpClientErrorException ex) {
        return Map.of(
                "message", ex.getMessage()
        );
    }
}
