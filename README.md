docker-compose up -d (Sobe apenas o banco)

docker-compose run --rm elasticsearch-seeder (Roda o seeder e o --rm garante que ele é apagado da sua máquina no segundo em que terminar de inserir os jogos).



Exposição de métricas:
pedidos criados
Chamadas para outros microservices
pagamentos aprovados





# Games Store Microservices (Assessment)

Este projeto é um sistema distribuído de e-commerce voltado para a venda de jogos eletrônicos, desenvolvido para garantir alta disponibilidade, observabilidade e resiliência.

Através de uma arquitetura de microsserviços guiada pelos princípios do Domain-Driven Design (DDD), o sistema isola as responsabilidades de catálogo (produtos), pagamentos e fechamento de pedidos. A principal evolução do sistema é a adoção de uma **Arquitetura Orientada a Eventos (EDA)** com o padrão Saga Coreografada. Se houver falhas ou lentidão no processamento, o fluxo de compras é protegido de forma assíncrona, não bloqueando o cliente e garantindo a eventual consistência dos dados de pagamento e estoque.

# Integrantes

* Eduardo Francisco

## Arquitetura

* **Microservices Baseados em Domínio:** Serviços independentes para Pedidos, Produtos e Pagamentos, cada um com as suas próprias regras de negócio e persistência de dados.
* **Mensageria Assíncrona (Kafka):** O sistema utiliza o Kafka para a comunicação assíncrona entre o `Order-Service` e o `Payment-Service`, orquestrando a transação sem acoplamento temporal.
* **Comunicação Síncrona Resiliente (RestClient):** Utilização do `RestClient` moderno do Spring com configuração estrita de *Timeouts* e controle de fluxo via exceções (mecanismo de Fallback) para consultas imediatas entre os serviços, evitando falhas em cascata.
* **Service Discovery (Netflix Eureka):** Atua como a "lista telefônica" do sistema. Os microsserviços registram-se no Eureka e encontram-se dinamicamente (ex: `http://PRODUCT-SERVICE`).
* **API Gateway:** O ponto único de entrada para clientes externos. Ele recebe as requisições na porta `9999` e faz o roteamento dinâmico para os microsserviços apropriados.
* **Observabilidade Completa:** Monitoramento de métricas customizadas de negócio e saúde com **Prometheus e Grafana**, além de centralização de logs estruturados utilizando **Logstash e Elasticsearch**.
* **Bancos de Dados Descentralizados:** O catálogo utiliza o **Elasticsearch** original para buscas rápidas (Full-Text Search) de produtos, enquanto as transações e pedidos ficam em bancos **PostgreSQL** relacionais isolados.

## Observabilidade

O ecossistema adota duas bases de dados de naturezas distintas para garantir a observabilidade completa:
* **Métricas e Alertas (Actuator + Prometheus):** O **Prometheus** atua como um *Time-Series Database* (TSDB). Ele armazena exclusivamente séries temporais numéricas (uso de CPU, consumo de memória, tempo de requisição) do *Actuator*. Estes dados alimentam os dashboards do **Grafana** para acompanhamento da saúde e performance (ex: responder "o sistema está sobrecarregado?"), além de coletar as métricas personalizadas de negócio desenvolvidas no projeto, como a quantidade de pedidos criados, tempo médio de processamento e o volume de pagamentos aprovados ou cancelados.
* **Centralização de Logs (Logstash + Elasticsearch):** Um **segundo cluster Elasticsearch** dedicado à infraestrutura atua como um motor de busca de documentos. Ele armazena e indexa os logs complexos ejetados pelas aplicações e trafegados via Logstash. Serve para investigar incidentes em profundidade (ex: responder "qual foi a linha de código que falhou e gerou este erro?").
* **Visualização Unificada (Grafana):** Atua como a camada de visualização central da arquitetura. Ele é responsável por montar dashboards personalizados consumindo dados tanto do Prometheus (para gráficos de performance e métricas de negócio) quanto do Elasticsearch dedicado à infraestrutura (para análise de logs), unificando toda a saúde do ecossistema em um único painel operacional.

## Microservices e Infraestrutura

| Serviço / Componente | Responsabilidade | Porta Pública | Banco de Dados / Infra |
| --- | --- | --- | --- |
| **Eureka Server** | Registro e descoberta de serviços (Service Discovery). | `8761` | N/A |
| **API Gateway** | Roteamento centralizado, balanceamento de carga e ponto único de acesso. | `9999` | N/A |
| **Product-Service** | Gerenciamento do catálogo de jogos e estoque. | Dinâmica | Elasticsearch (Catálogo) |
| **Payment-Service** | Processamento de pagamentos de forma assíncrona via Kafka. | Dinâmica | PostgreSQL |
| **Order-Service** | Orquestração do checkout. Chama serviços via RestClient e emite eventos no Kafka. | Dinâmica | PostgreSQL |
| **Kafka-UI** | Interface visual para monitoramento dos tópicos e mensagens no Kafka. | `9091` | N/A |
| **Grafana** | Dashboards unificados de visualização de métricas e logs. | `3000` | Consome Prometheus e Elasticsearch (Logs) |

Aqui está a atualização do seu tópico **"Como executar"** estruturada exatamente com a estratégia de orquestração por etapas que pediu, garantindo que o build do Docker encontre os binários e que os bancos pesados fiquem prontos antes do restante do ecossistema:


## Como executar

O projeto foi inteiramente conteinerizado para simplificar a implantação. Siga o passo a passo abaixo para garantir que o ecossistema suba de forma segura e sem erros de timeout:

**Pré-requisitos:** Ter o Docker e o Docker Compose instalados na máquina.

### 1. Preparação dos Binários (.jar)
Antes de iniciar os containers, certifique-se de compilar os microsserviços (via IDE ou utilizando o comando `./mvnw clean package -DskipTests` em cada pasta de serviço). **Os arquivos `.jar` gerados devem estar obrigatoriamente posicionados dentro das suas devidas pastas de contexto** (no diretório `docker/` de cada microsserviço, onde os arquivos `Dockerfile` correspondentes estão configurados para buscá-los para construir as imagens).

### 2. Fluxo Sequencial de Instalação

**Passo 1: Subir os serviços de Elasticsearch primeiro**
Como as duas instâncias do Elasticsearch (`elasticsearch-logs` e `product-elasticsearch`) demoram mais tempo para subir e inicializar completamente os seus motores de busca, inicie apenas os dois de forma isolada:
```bash
docker-compose up -d elasticsearch-logs product-elasticsearch

```

*Aguarde cerca de 30 a 45 segundos para que fiquem totalmente prontos para aceitar conexões.*

**Passo 2: Alimentar o Elasticsearch de Produtos (Catálogo)**
Com o banco de dados de produtos de pé, execute o container de seed para popular o catálogo de jogos eletrônicos inicial na base:

```bash
docker-compose run --rm product-elasticsearch-seeder

```

**Passo 3: Subir o resto do ecossistema junto**
Agora que o catálogo de produtos está populado e a base de logs está ativa, você pode iniciar o restante das coisas juntas (bancos PostgreSQL, Kafka, Eureka, API Gateway, microsserviços e ferramentas de monitoramento) de uma só vez:

```bash
docker-compose up -d --build

```
## Endpoints

Como a arquitetura utiliza um **API Gateway**, o utilizador final (ou aplicação Front-end) não precisa de saber em que portas os microsserviços estão a rodar internamente. **Todas as requisições devem ser enviadas para a porta `9999` (Gateway)**, que se encarrega de rotear para o serviço correto.

Abaixo estão os principais endpoints disponíveis para interagir com o ecossistema:

### Catálogo de Produtos (`Product-Service`)
Responsável por gerir a vitrine da loja e as quantidades em stock no Elasticsearch.

* **Listar todos os produtos:**
    * **Método:** `GET`
    * **URL:** `http://localhost:9999/products`

* **Buscar um produto específico por ID:**
    * **Método:** `GET`
    * **URL:** `http://localhost:9999/products/{id}`

* **Cadastrar um novo produto (Alimentar o Stock):**
    * **Método:** `POST`
    * **URL:** `http://localhost:9999/products`
    * **Payload (JSON):**
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
Responsável por orquestrar o carrinho, validar o stock e iniciar o pagamento via Kafka.

* **Listar todos os pedidos gerados:**
    * **Método:** `GET`
    * **URL:** `http://localhost:9999/orders`

* **Acompanhar o status de um pedido:**
    * *Útil para verificar se o pagamento assíncrono via Kafka aprovou ou recusou a compra.*
    * **Método:** `GET`
    * **URL:** `http://localhost:9999/orders/{id}`

* **Realizar uma compra (Criar Pedido):**
    * *Nota: Substitua o `productId` com o ID real gerado pelo Elasticsearch ao criar um produto no passo anterior.*
    * **Método:** `POST`
    * **URL:** `http://localhost:9999/orders`
    * **Payload (JSON):**
      ```json
      {
        "customerName": "João da Silva",
        "paymentMethod": "CREDIT_CARD",
        "items": [
          {
            "productId": "INSERIR-ID-DO-PRODUTO-AQUI",
            "quantity": 1
          },
          {
            "productId": "INSERIR-OUTRO-ID-AQUI",
            "quantity": 2
          }
        ]
      }
      ```

### Pagamentos (`Payment-Service`)
Embora os pagamentos sejam processados de forma 100% assíncrona (escutando o Kafka) quando um pedido é criado, pode consultar o histórico financeiro:

* **Listar todas as transações de pagamento:**
    * **Método:** `GET`
    * **URL:** `http://localhost:9999/payments`


