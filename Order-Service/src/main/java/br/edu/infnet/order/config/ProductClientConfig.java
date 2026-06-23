package br.edu.infnet.order.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProductClientConfig {

    @Bean
    public RestClient productRestClient(
            @Value("${integration.product.base-url}") String baseUrl,
            ObjectProvider<RestClientCustomizer> customizerProvider,
            LoadBalancerInterceptor loadBalancerInterceptor
    ) {
        RestClient.Builder builder = RestClient.builder();
        customizerProvider.orderedStream().forEach(customizer -> customizer.customize(builder));

        return builder
                .baseUrl(baseUrl)
                .requestInterceptor(loadBalancerInterceptor)
                .build();
    }
}
