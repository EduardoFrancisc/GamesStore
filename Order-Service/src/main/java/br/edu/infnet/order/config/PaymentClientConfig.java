package br.edu.infnet.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class PaymentClientConfig {
    @Bean
    public RestClient paymentRestClient(
            @Value("${integration.payment.base-url}") String baseUrl) {

        HttpClient client = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(client);
        factory.setReadTimeout(Duration.ofSeconds(4));

        return RestClient
                .builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}
