# Games Store Microservices (Assessment)

Este projeto é um sistema distribuído de e-commerce voltado para a venda de jogos eletrônicos, desenvolvido para garantir alta disponibilidade e resiliência. O problema central que o sistema resolve é a **falta de consistência e perda de vendas em cenários de falha de rede** durante o processo de *checkout*.

Através de uma arquitetura de microsserviços guiada pelos princípios do Domain-Driven Design (DDD), o sistema isola as responsabilidades de catálogo (produtos), pagamentos e fechamento de pedidos. A principal solução técnica implementada é a proteção do fluxo de compras: se o gateway de pagamento apresentar lentidão ou sair do ar, o sistema não retorna um erro para o cliente nem perde o carrinho, mas atua com um mecanismo de *Fallback* que retém o pedido no banco de dados com o status de "PENDENTE" para conciliação futura, deduzindo corretamente o estoque no processo.

# Integrantes

* Eduardo Francisco

## Arquitetura

* **Microservices Baseados em Domínio:** Serviços independentes para Pedidos, Produtos e Pagamentos, cada um com as suas próprias regras de negócio e persistência de dados.
* **Service Discovery (Netflix Eureka):** Atua como a "lista telefônica" do sistema. Os microsserviços não conhecem os IPs uns dos outros; eles registram-se no Eureka e encontram-se pelos nomes (ex: `http://PRODUCT-SERVICE`).
* **API Gateway:** O único ponto de entrada para os clientes externos (Front-end/Mobile). Ele recebe as requisições na porta 8080 e faz o roteamento dinâmico para os microsserviços apropriados consultando o Eureka.
* **Resiliência Nativa:** Utilização de `RestClient` moderno com configuração estrita de *Timeouts* e controle de fluxo via exceções (Padrão Circuit Breaker/Fallback simplificado) para evitar falhas em cascata.
* **Bancos de Dados Descentralizados:** Cada microsserviço é dono da sua própria base de dados (H2 em memória e PostgreSQL em containers Docker) para evitar acoplamento de dados.

## Microservices

| Serviço | Responsabilidade | Porta | Banco de Dados |
| --- | --- | --- | --- |
| **Eureka Server** | Registro e descoberta de serviços (Service Discovery). | 8761 | N/A |
| **API Gateway** | Roteamento centralizado, balanceamento de carga e ponto único de acesso. | 8080 | N/A |
| **Product-Service** | Gerenciamento do catálogo de jogos e controle de saldo de estoque. | 8082 | Elastic Search |
| **Payment-Service** | Simulação de gateway de operadora de cartão. Implementa latência artificial (Caos) para testes de resiliência. | 8083 | PostgreSQL |
| **Order-Service** | Orquestração do checkout. Aplica o DDD, consulta preços, deduz estoque, chama pagamento e aplica máquina de estados. | Aleatória (0) / 8081 | PostgreSQL |

## Como executar

**1. Subir a infraestrutura de Bancos de Dados (Docker)**
Navegue até as pastas `Product-Service` e `Payment-Service` (onde estão os arquivos `docker-compose.yml`) e execute os containers para subir as instâncias do PostgreSQL:

```bash
docker-compose up -d

```

**2. Subir o ecossistema Spring Boot**
Para o correto funcionamento do *Service Discovery*, os serviços devem ser iniciados na seguinte ordem através da sua IDE (IntelliJ/Eclipse) ou via Maven (`./mvnw spring-boot:run`):

1. `EurekaApplication` (Aguarde inicializar completamente)
2. `GatewayApplication`
3. `ProductApplication`
4. `PaymentApplication`
5. `OrderApplication`

## Discovery Server

* **URL de acesso (Dashboard):** `http://localhost:8761`
* **Serviços que devem constar como registrados (Instances currently registered with Eureka):**
* `GATEWAY-SERVICE`
* `ORDER-SERVICE`
* `PRODUCT-SERVICE`
* `PAYMENT-SERVICE`



## API Gateway

O Gateway está rodando em `http://localhost:8080`. As rotas configuradas permitem que as chamadas sejam feitas diretamente para o Gateway, que se encarregará de repassar para os serviços corretos nos bastidores:

* **Rota de Pedidos:** `http://localhost:8080/orders/`
* **Rota de Produtos:** `http://localhost:8080/products/`
* **Rota de Pagamentos:** `http://localhost:8080/payments/`

## Exemplos de requisições

Aqui estão os *payloads* para você testar no Insomnia, Postman ou salvar em arquivos `.http`.

### 1. Criar um Produto (Stock) - `POST http://localhost:8080/products`

*(Necessário para ter itens no estoque antes de fazer o pedido)*

```json
{
  "name": "Elden Ring",
  "price": 250.00,
  "stockQuantity": 50,
  "platform": "PC"
}

```

### 2. Criar um Pedido (Checkout Resiliente) - `POST http://localhost:8080/orders`

*(Substitua os UUIDs do `productId` pelos IDs reais devolvidos na criação dos produtos no passo 1)*

```json
{
  "customerName": "João da Silva",
  "paymentMethod": "CREDIT_CARD",
  "items": [
    {
      "productId": "INSERIR-UUID-DO-PRODUTO-AQUI",
      "quantity": 1
    },
    {
      "productId": "INSERIR-UUID-DE-OUTRO-PRODUTO-AQUI",
      "quantity": 2
    }
  ]
}

```
