package br.edu.infnet.order.integration.payment.client;

import br.edu.infnet.order.integration.payment.dto.PaymentRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {
    private final RestClient PaymentRestClient;

    public PaymentClient(RestClient PaymentRestClient) {this.PaymentRestClient = PaymentRestClient;}

    public void create(PaymentRequest paymentRequest) {
        PaymentRestClient.post()
                .uri("/payments")
                .body(paymentRequest);
    }








}
