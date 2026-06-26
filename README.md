# Games Store Microservices (Assessment)

Este projeto é um sistema distribuído de e-commerce voltado para a venda de jogos eletrônicos, desenvolvido com Spring Boot para garantir alta disponibilidade, observabilidade e resiliência, seguindo os princípios de Domain-Driven Design (DDD).

## Problema a ser resolvido
O sistema visa resolver a complexidade de gerenciar catálogo, estoque, compras e pagamentos de uma loja de jogos em um ambiente de alto tráfego. A adoção de uma Arquitetura Orientada a Eventos (EDA) com o padrão Saga Coreografada protege o fluxo de compras de falhas ou lentidão, não bloqueando o cliente e garantindo a consistência eventual dos dados entre estoque e pagamento.

## Usuários do sistema
- **Clientes finais (Gamers):** Que acessam a vitrine para buscar e comprar jogos.
- **Administradores da Loja:** Que cadastram novos produtos e acompanham as vendas e métricas operacionais da plataforma.

## Principais funcionalidades
- **Catálogo de Produtos:** Gerenciamento da vitrine de jogos e controle de estoque com buscas rápidas no Elasticsearch.
- **Orquestração de Pedidos:** Carrinho de compras integrado com validação de estoque e processamento financeiro.
- **Processamento de Pagamentos:** Executado de forma isolada e assíncrona, atualizando o status do pedido via mensageria.
- **Monitoramento em Tempo Real:** Acompanhamento completo da saúde do sistema, rastreamento de requisições distribuídas e centralização de logs.

## Microsserviços
O ecossistema é dividido nos seguintes serviços baseados em domínio:
| Serviço / Componente | Responsabilidade | Porta Pública | Banco de Dados / Infra |
| --- | --- | --- | --- |
| **API Gateway** | Roteamento centralizado, balanceamento de carga e ponto único de acesso. | `9999` | N/A |
| **Product-Service** | Gerenciamento do catálogo de jogos e estoque. | Dinâmica | Elasticsearch (Catálogo) |
| **Payment-Service** | Processamento de pagamentos de forma assíncrona. | Dinâmica | PostgreSQL |
| **Order-Service** | Orquestração do checkout. Chama serviços via RestClient e emite/consome eventos no Kafka. | Dinâmica | PostgreSQL |

## Discovery server
- **Netflix Eureka (`eureka-server`):** Atua como a "lista telefônica" do sistema. Os microsserviços registram-se dinamicamente nele na porta `8761` e encontram uns aos outros pelos nomes (ex: `http://PRODUCT-SERVICE`), permitindo o balanceamento de carga interno e alta disponibilidade sem conhecer os IPs reais.

## Comunicação Síncrona e Assíncrona
- **Síncrona (RestClient):** Utilizada no momento do checkout, onde o `Order-Service` comunica-se imediatamente com o `Product-Service` para verificar disponibilidade de estoque e com o `Payment-Service` para criar a intenção de pagamento.
- **Assíncrona (Kafka):** Utilizada para confirmar transações sem acoplamento temporal. O `Payment-Service` processa o pagamento no seu tempo e publica um evento no tópico `pagamentos.aprovados`. O `Order-Service` escuta este tópico para atualizar o status do pedido de pendente para finalizado/rejeitado.

## Desenho do Sistema
<img width="803" height="431" alt="GamesstoreDiagram drawio" src="https://github.com/user-attachments/assets/84f45a93-dbff-4756-9ab5-55e65fd0ece2" />

## Resiliência e Tolerância a Falhas
A arquitetura foi desenhada para suportar instabilidades em serviços dependentes sem comprometer a experiência do usuário:
- **Timeouts Estratégicos:** Utilização estrita de *Timeouts* nas comunicações síncronas (`RestClient`). Isso evita que a lentidão de um microsserviço cause o esgotamento de conexões (falha em cascata) no serviço que o invocou.
- **Fallbacks e Graceful Degradation:** Tratamento avançado de exceções HTTP via interceptadores e `.onStatus`. Quando o serviço de catálogo falha ou demora, respostas amigáveis são retornadas. Se o pagamento sofre timeout síncrono, a compra não é abortada bruscamente, mas transita para um estado seguro (como "Retido como Pendente").
- **Isolamento de Falhas (Kafka):** Graças à arquitetura orientada a eventos (Saga Coreografada), se o serviço de Pagamentos cair, a vitrine e o fechamento de pedidos continuam 100% operacionais. As mensagens acumulam no Kafka de forma segura e são processadas automaticamente (Eventual Consistency) assim que o serviço voltar ao ar, garantindo que nenhum dado seja perdido.

## Observabilidade
A stack adota bases de dados de naturezas distintas para garantir monitoramento total da arquitetura distribuída:
- **Centralização de Logs (Logstash + Elasticsearch):** Logs complexos das aplicações são capturados pelo LogstashEncoder e armazenados num cluster Elasticsearch dedicado (`gamesstore-logs-*`).
- **Distributed Tracing (OpenTelemetry + Micrometer):** Um único `traceId` acompanha a requisição por toda a arquitetura (passando pelo Gateway, RestClient e Kafka). O `Otel-Collector` recebe os traces via protocolo OTLP e os exporta para o Elasticsearch (`gamesstore-traces`).
- **Métricas e Alertas (Prometheus):** Coleta de métricas de saúde, memória, CPU e métricas de negócio do Actuator.
- **Visualização Unificada (Grafana):** Painel central na porta `3000` que cruza dados do Prometheus (métricas) e do Elasticsearch (logs e traces em cascata).

<img width="1843" height="723" alt="image" src="https://github.com/user-attachments/assets/f78dec4f-7cb9-4cab-b7cd-cc319f8e10bb" />
<img width="1542" height="826" alt="image" src="https://github.com/user-attachments/assets/f6a77265-a924-4ece-a8c2-42f2ae770fbe" />

*(Demonstração de traceId e spanId interligados)*
<img width="1903" height="812" alt="image" src="https://github.com/user-attachments/assets/67d8eccf-e358-4329-9a23-95dca498b58c" />


## Como rodar

O projeto foi inteiramente conteinerizado para simplificar a implantação. Siga o passo a passo abaixo:

**Pré-requisitos:** Ter o Docker e o Docker Compose instalados na sua máquina.

1. **Preparação dos Binários (.jar)**
Compile os microsserviços via IDE ou utilizando `./mvnw clean package -DskipTests` na pasta de cada serviço. Os arquivos `.jar` gerados devem estar posicionados obrigatoriamente nas respectivas pastas `docker/` das aplicações.

2. **Passo 1: Subir os serviços de Elasticsearch primeiro**
Como as instâncias do Elasticsearch (`elasticsearch-logs` e `product-elasticsearch`) demoram mais para subir, inicie-os isoladamente para não causar timeouts:
```bash
docker-compose up -d elasticsearch-logs product-elasticsearch
```
*Aguarde cerca de 30 a 45 segundos.*

3. **Passo 2: Alimentar o Elasticsearch de Produtos (Catálogo)**
Execute o container de seed para popular o catálogo de jogos inicial no banco de buscas:
```bash
docker-compose run --rm product-elasticsearch-seeder
```

4. **Passo 3: Subir o resto do ecossistema junto**
Inicie os bancos PostgreSQL, Kafka, Eureka, API Gateway, microsserviços e ferramentas de monitoramento de uma vez:
```bash
docker-compose up -d --build
```

### Configurando os Dashboards no Grafana:
1. Acesse `http://localhost:3000` (Login: `admin` / Senha: `admin`).
2. No menu **Connections > Add new connection > Elasticsearch**:
   * **Logs:** Adicione a URL `http://elasticsearch-logs:9200`, Index name `gamesstore-logs-*`, Pattern **No pattern**, Time field `@timestamp`, Query mode `Logs`. Salve.
   * **Traces:** Adicione outra fonte Elasticsearch com a mesma URL, Index name `gamesstore-traces`, Pattern **No pattern**, Time field `@timestamp`. Salve.
3. No menu **Connections > Add new connection > Prometheus**:
   * **Métricas:** Adicione a URL `http://prometheus:9090`. Salve.
4. Vá em **Dashboards > Import** e faça o upload do arquivo `grafana-dashboard.json` localizado na raiz do projeto. Selecione as fontes recém-criadas e finalize.

## Endpoints

Como a arquitetura utiliza um **API Gateway**, o cliente final não precisa saber em que portas os microsserviços estão rodando internamente. Todas as requisições externas devem ser enviadas exclusivamente para a porta **`9999` (Gateway)**, que se encarrega de rotear para o destino correto.

### Catálogo de Produtos (`Product-Service`)
* **Listar todos os produtos:** `GET http://localhost:9999/products`
* **Buscar produto por ID:** `GET http://localhost:9999/products/{id}`
* **Cadastrar novo produto:** `POST http://localhost:9999/products`
  ```json
  {
    "title": "Elden Ring",
    "description": "Jogo de RPG de Ação, vencedor do GOTY.",
    "price": 250.00,
    "stockQuantity": 50,
    "platform": "PC",
    "releaseDate": "2022-02-25T00:00:00"
  }
  ```

### Pedidos (`Order-Service`)
* **Listar todos os pedidos:** `GET http://localhost:9999/orders`
* **Acompanhar status do pedido:** `GET http://localhost:9999/orders/{id}`
* **Realizar uma compra (Criar Pedido):** `POST http://localhost:9999/orders`
  ```json
  {
    "customerName": "João da Silva",
    "paymentMethod": "CREDIT_CARD",
    "items": [
      {
        "productId": "INSERIR-ID-DO-PRODUTO-AQUI",
        "quantity": 1
      }
    ]
  }
  ```

### Pagamentos (`Payment-Service`)
* **Listar transações de pagamento históricas:** `GET http://localhost:9999/payments`
