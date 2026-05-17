package br.edu.infnet.product.exception;

public class StockQuantityException extends RuntimeException {
    public StockQuantityException(String message) {
        super(message);
    }
}
