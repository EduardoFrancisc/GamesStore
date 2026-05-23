package br.edu.infnet.order.integration.payment.client;

import br.edu.infnet.order.integration.payment.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {
    private final RestClient PaymentRestClient;

    //"Consider using @Qualifier to identify the bean that should be consumed".
    public PaymentClient(@Qualifier("paymentRestClient")RestClient PaymentRestClient) {
        this.PaymentRestClient = PaymentRestClient;
    }

    public void create(PaymentRequest paymentRequest) {
        try {
            PaymentRestClient.post()
                    .uri("/payments")
                    .body(paymentRequest)
                    .retrieve()
                    .toBodilessEntity();

        } catch (ResourceAccessException e) {
            paymentFallback(paymentRequest, e);
        }
    }

    public void paymentFallback(PaymentRequest paymentRequest, Throwable t) {
        throw new RuntimeException("PAYMENT_TIMEOUT_FALLBACK");
    }
}
