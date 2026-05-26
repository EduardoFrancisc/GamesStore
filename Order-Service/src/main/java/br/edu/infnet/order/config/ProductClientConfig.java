package br.edu.infnet.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductClientConfig {

    @Bean()
    @Primary
    public RestClient.Builder restClientBuilder () {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder () {
        return RestClient.builder();
    }

    @Bean
    public RestClient productRestClient(
            @Value("${integration.product.base-url}")
            String baseUrl
    ) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
