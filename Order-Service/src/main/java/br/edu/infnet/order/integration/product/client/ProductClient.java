package br.edu.infnet.order.integration.product.client;

import br.edu.infnet.order.dto.OrderItemDTO;
import br.edu.infnet.order.integration.product.exception.ProductNotFoundException;
import br.edu.infnet.order.integration.product.dto.ProductResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductClient {
    private final RestClient ProductRestClient;

    public ProductClient(RestClient ProductRestClient) {
        this.ProductRestClient = ProductRestClient;
    }

    public ProductResponse getProductById(String productId) {
        return ProductRestClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new ProductNotFoundException("O produto com ID '" + productId + "' não foi encontrado no catálogo.");
                })
                .body(ProductResponse.class);
    }
    public void reduceProductQuantityStock(List<OrderItemDTO> orderItemDTOs) {
        ProductRestClient.patch()
                .uri("/products/update-stock")
                .body(orderItemDTOs)
                .retrieve()
                .body(OrderItemDTO.class);
    }
}
