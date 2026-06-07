package br.edu.infnet.product.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ProductMetrics {
    public final Counter produtosDecrescidos;

    public ProductMetrics(MeterRegistry registry) {
        this.produtosDecrescidos = Counter.builder("gamesstore_produto_decrescidos_total")
                .description("Total de produtos decrescidos do stock.")
                .tag("service", "product-service")
                .register(registry);
    }

    public void incrementarProdutosDecrescidos() {
        this.produtosDecrescidos.increment();
    }

}
