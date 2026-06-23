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

<img width="1544" height="785" alt="image" src="https://github.com/user-attachments/assets/315224c5-3af1-48d5-b206-ca20af997b55" />
<img width="1542" height="826" alt="image" src="https://github.com/user-attachments/assets/f6a77265-a924-4ece-a8c2-42f2ae770fbe" />

## Microservices e Infraestrutura

| Serviço / Componente | Responsabilidade | Porta Pública | Banco de Dados / Infra |
| --- | --- | --- | --- |
| **Eureka Server** | Registro e descoberta de serviços (Service Discovery). | `8761` | N/A |
| **API Gateway** | Roteamento centralizado, balanceamento de carga e ponto único de acesso. | `9999` | N/A |
| **Product-Service** | Gerenciamento do catálogo de jogos e estoque. | Dinâmica | Elasticsearch (Catálogo) |
| **Payment-Service** | Processamento de pagamentos de forma assíncrona via Kafka. | Dinâmica | PostgreSQL |
| **Order-Service** | Orquestração do checkout. Chama serviços via RestClient e emite eventos no Kafka. | Dinâmica | PostgreSQL |
| **Kafka-UI** | Interface visual para monitoramento dos tópicos e mensagens no Kafka. | `9091` | N/A |
| **Grafana** | Dashboard unificado de visualização de métricas. | `3000` | Consome Prometheus e Elasticsearch (Logs) |

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
## 📊 Como configurar a Observabilidade e Dashboards

A nossa stack de monitoramento utiliza o Grafana como centralizador de visualização. Siga os passos abaixo para plugar os bancos de dados (Elasticsearch e Prometheus) na interface.

### 1. Acessando o Grafana
- **URL:** [http://localhost:3000](http://localhost:3000)
- **Login / Senha (padrão):** `admin` / `admin`

### 2. Configurando o Elasticsearch (Logs e Traces)
O Elasticsearch armazena tanto os nossos logs (via Logstash) quanto a árvore de traces (via Otel-Collector). Precisamos criar duas conexões separadas.

No Grafana, vá no menu lateral: **Connections** > **Add new connection** > Busque por **Elasticsearch** e adicione duas fontes de dados:

#### 🔹 Fonte de Dados 1: Logs do Sistema
- **Name:** `Elasticsearch Logs`
- **URL:** `http://elasticsearch-logs:9200`
- **Index name:** `gamesstore-logs-*`
- **Pattern:** `No pattern` *(⚠️ Crucial para evitar erros de leitura)*
- **Time field name:** `@timestamp`
- **Version:** `8.x+`
- **Default query mode:** `Logs`
> Clique em **Save & test**. Um aviso verde confirmará o sucesso.

#### 🔹 Fonte de Dados 2: Traces (Otel Collector)
Volte em Add new connection e crie a segunda fonte:
- **Name:** `Elasticsearch Traces`
- **URL:** `http://elasticsearch-logs:9200`
- **Index name:** `gamesstore-traces`
- **Pattern:** `No pattern`
- **Time field name:** `@timestamp`
- **Version:** `8.x+`
> Clique em **Save & test**. *(Obs: Os traces e spans ids só aparecerão no índice após você fazer a primeira requisição na aplicação).*

### 3. Configurando o Prometheus (Métricas)
Para acompanhar a saúde da aplicação (memória, CPU, requisições por segundo):
1. Vá novamente em **Connections** > **Add new connection** > Busque por **Prometheus**.
2. **URL:** `http://prometheus:9090`
3. Clique em **Save & test**.

### 4. Importando o Dashboard Central
Com as fontes de dados plugadas, você já pode importar a nossa visualização pronta:
1. No menu lateral, vá em **Dashboards** > **New** > **Import**.
2. Clique em **Upload JSON file** e selecione o arquivo `grafana-dashboard.json` localizado na raiz deste projeto.
3. O Grafana pedirá para você vincular as variáveis de fonte de dados. Selecione as conexões que você acabou de criar nos passos anteriores.
4. Clique em **Import**.

Pronto! Agora você tem uma visão completa cruzando Logs e Traces (Distributed Tracing) na mesma tela!





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

## Observabilidade
Demonstração de traceId e spanId
<img width="1919" height="853" alt="image" src="https://github.com/user-attachments/assets/ff45c1a6-093c-46ce-b801-e2bea5089f52" />
<img width="1919" height="749" alt="image" src="https://github.com/user-attachments/assets/721507c5-20e7-44ff-9724-2e5d05413f70" />
<img width="1918" height="766" alt="image" src="https://github.com/user-attachments/assets/f08f8cb8-c435-400f-bb15-112618c6a910" />





