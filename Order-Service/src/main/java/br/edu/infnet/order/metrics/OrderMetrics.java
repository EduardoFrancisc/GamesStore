package br.edu.infnet.order.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class OrderMetrics {

    private final Counter pedidoCriado;
    private final Counter pagamentoAprovado;
    private final Counter pagamentoPendente;
    private final Counter pagamentoCancelado;
    private final Timer duracaoPedido;

    public OrderMetrics(MeterRegistry registry) {
        this.pedidoCriado = Counter.builder("gamesstore_criacao_total")
                .description("Total de pedidos criados.")
                .tag("service", "order-service")
                .register(registry);

        this.pagamentoAprovado = Counter.builder("gamesstore_pagamento_aprovado_total")
                .description("Total de pagamentos que foram confirmados via Kafka.")
                .tag("service", "order-service")
                .register(registry);

        this.pagamentoPendente = Counter.builder("gamesstore_pagamento_pendente_total")
                .description("Total de pagamentos que caíram no Fallback e ficaram pendentes.")
                .tag("service", "order-service")
                .register(registry);

        this.pagamentoCancelado = Counter.builder("gamesstore_pagamento_cancelado_total")
                .description("Total de pagamentos que foram cancelados via Kafka.")
                .tag("service", "order-service")
                .register(registry);

        this.duracaoPedido = Timer.builder("gamesstore_duracao_processamento_pedido")
                .description("Tempo gasto para processar a criação de um pedido.")
                .tag("service", "order-service")
                .publishPercentileHistogram()
                .register(registry);
    }

    public void incrementarPedidoCriado() {
        this.pedidoCriado.increment();
    }

    public void incrementarPagamentoAprovado() {
        this.pagamentoAprovado.increment();
    }

    public void incrementarPagamentoPendente() {
        this.pagamentoPendente.increment();
    }

    public void incrementarPagamentoCancelado() {
        this.pagamentoCancelado.increment();
    }

    public <T> T medirTempoDuracaoPeido(Supplier<T> operacao) {
        return duracaoPedido.record(operacao);
    }
}