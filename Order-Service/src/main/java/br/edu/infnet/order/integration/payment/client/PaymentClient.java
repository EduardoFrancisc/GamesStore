package br.edu.infnet.order.integration.payment.client;

import br.edu.infnet.order.integration.payment.dto.PaymentRequest;
import br.edu.infnet.order.integration.payment.dto.PaymentResponse;
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

    public PaymentResponse create(PaymentRequest paymentRequest) {
        try {
            // Agora retorna corretamente o DTO mapeado!
            return PaymentRestClient.post()
                    .uri("/payments")
                    .body(paymentRequest)
                    .retrieve()
                    .body(PaymentResponse.class);

        } catch (ResourceAccessException e) {
            throw paymentFallback(paymentRequest, e);
        }
    }

    public RuntimeException paymentFallback(PaymentRequest paymentRequest, Throwable t) {
        return new RuntimeException("PAYMENT_TIMEOUT_FALLBACK");
    }
}
