package br.edu.infnet.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductClientConfig {
    @Bean
    public RestClient productRestClient(
            @Value("${integration.product.base-url}") String baseUrl,
            @LoadBalanced RestClient.Builder builder
    ) {
        return builder.baseUrl(baseUrl).build();
    }
}
