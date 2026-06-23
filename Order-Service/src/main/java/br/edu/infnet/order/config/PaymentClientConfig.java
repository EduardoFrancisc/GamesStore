package br.edu.infnet.order.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
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
            @Value("${integration.payment.base-url}") String baseUrl,
            ObjectProvider<RestClientCustomizer> customizerProvider,
            LoadBalancerInterceptor loadBalancerInterceptor
    ) {

        HttpClient client = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(4))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(client);
        factory.setReadTimeout(Duration.ofSeconds(4));

        RestClient.Builder builder = RestClient.builder();
        customizerProvider.orderedStream().forEach(customizer -> customizer.customize(builder));

        return builder
                .baseUrl(baseUrl)
                .requestInterceptor(loadBalancerInterceptor)
                .requestFactory(factory)
                .build();
    }
}
