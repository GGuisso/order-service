# Order Service

O **Order Service** é um microserviço Spring Boot responsável pelo gerenciamento de pedidos. Ele recebe pedidos através de uma API REST, envia-os para processamento assíncrono via RabbitMQ e persiste os dados no MongoDB. Além disso, a aplicação utiliza Redis para cache, expõe endpoints de monitoramento com o Actuator e documenta a API com Swagger.

## Sumário

- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Executando a Aplicação](#executando-a-aplicação)
  - [Opção 1: Executando Localmente (sem Docker)](#opção-1-executando-localmente-sem-docker)
  - [Opção 2: Executando com Docker Compose](#opção-2-executando-com-docker-compose)
- [Endpoints da API](#endpoints-da-api)
  - [Swagger UI](#swagger-ui)
  - [Actuator](#actuator)
  - [Pedidos - CRUD](#pedidos---crud)
- [Exemplos de JSON](#exemplos-de-json)
- [Testando a Aplicação](#testando-a-aplicação)
- [Informações Adicionais](#informações-adicionais)
- [Autor](#autor)

## Tecnologias

- **Java 11+**
- **Spring Boot**
- **Spring Data MongoDB**
- **RabbitMQ**
- **Redis**
- **Swagger / OpenAPI**
- **Spring Actuator**
- **JUnit 5 e Mockito** (para testes)
- **Maven**
- **Docker e Docker Compose**

## Pré-requisitos

- JDK 11 ou superior instalado.
- Maven instalado.
- Docker e Docker Compose (caso opte por utilizar contêineres para RabbitMQ e MongoDB).
- Ambiente de desenvolvimento (IDE de sua preferência).

## Instalação

1. **Clone o repositório:**

   ```bash
   git clone https://github.com/seuusuario/order-service.git
   cd order-service
   ```

2. **Compile e construa o projeto com Maven:**

   ```bash
   mvn clean install
   ```

## Executando a Aplicação

### Opção 1: Executando Localmente (sem Docker)

Certifique-se de que o **MongoDB**, **RabbitMQ** e **Redis** estejam instalados e rodando em sua máquina conforme as configurações definidas em `application.properties`:

- **MongoDB:** localhost:27017  
- **RabbitMQ:** localhost:5672  
- **Redis:** localhost:6379  

Em seguida, execute a aplicação:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em [http://localhost:8080](http://localhost:8080).

### Opção 2: Executando com Docker Compose

Você pode utilizar o Docker Compose para rodar contêineres do MongoDB e RabbitMQ.

1. **Crie um arquivo `docker-compose.yml` na raiz do projeto com o seguinte conteúdo:**

   ```yaml
   version: '3.8'
   services:
     mongodb:
       image: mongo
       ports:
         - 27017:27017
       environment:
         - MONGO_INITDB_ROOT_USERNAME=admin
         - MONGO_INITDB_ROOT_PASSWORD=123

     rabbitmq:
       image: rabbitmq:3.9.29-management
       ports:
         - 5672:5672
         - 15672:15672
   ```

2. **Inicie os contêineres:**

   ```bash
   docker-compose up -d
   ```

3. **Execute a aplicação:**

   ```bash
   mvn spring-boot:run
   ```

## Endpoints da API

### Swagger UI

Acesse a documentação interativa da API em:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Actuator

Endpoints de monitoramento e métricas:  
[http://localhost:8080/actuator](http://localhost:8080/actuator)

### Pedidos - CRUD

#### 1. Receber Pedido (Criação)

- **Endpoint:** `POST /api/orders`
- **Descrição:** Recebe um pedido do Produto Externo A e o envia para processamento assíncrono via RabbitMQ.
- **Exemplo de Request:**

  ```json
  {
    "id": "order-123",
    "products": [
      {
        "id": "prod-1",
        "name": "Test Product",
        "price": 10.50
      },
      {
        "id": "prod-2",
        "name": "Another Product",
        "price": 20.75
      }
    ]
  }
  ```

- **Response:**  
  HTTP 202 Accepted  
  Corpo: `"Pedido recebido e em processamento"`

#### 2. Consultar Pedido pelo ID

- **Endpoint:** `GET /api/orders/{id}`
- **Descrição:** Consulta os detalhes de um pedido processado utilizando o ID do pedido.
- **Exemplo de Request:**  
  `GET /api/orders/order-123`
- **Exemplo de Response:**

  ```json
  {
    "id": "order-123",
    "totalAmount": 31.25,
    "status": "PROCESSED",
    "products": [
      {
        "id": "prod-1",
        "name": "Test Product",
        "price": 10.50
      },
      {
        "id": "prod-2",
        "name": "Another Product",
        "price": 20.75
      }
    ]
  }
  ```

#### 3. Listar Pedidos (Paginação)

- **Endpoint:** `GET /api/orders?page=0&size=10`
- **Descrição:** Retorna uma lista paginada de pedidos processados.
- **Exemplo de Response:**

  ```json
  {
    "content": [
      {
        "id": "order-123",
        "totalAmount": 31.25,
        "status": "PROCESSED",
        "products": [
          {
            "id": "prod-1",
            "name": "Test Product",
            "price": 10.50
          },
          {
            "id": "prod-2",
            "name": "Another Product",
            "price": 20.75
          }
        ]
      }
    ],
    "pageable": {
      "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
      },
      "pageNumber": 0,
      "pageSize": 10,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
  ```

## Exemplos de JSON

### Exemplo de Pedido para Criação

```json
{
  "id": "order-123",
  "products": [
    {
      "id": "prod-1",
      "name": "Test Product",
      "price": 10.50
    },
    {
      "id": "prod-2",
      "name": "Another Product",
      "price": 20.75
    }
  ]
}
```

### Exemplo de Consulta de Pedido

Resposta esperada para o `GET /api/orders/order-123`:

```json
{
  "id": "order-123",
  "totalAmount": 31.25,
  "status": "PROCESSED",
  "products": [
    {
      "id": "prod-1",
      "name": "Test Product",
      "price": 10.50
    },
    {
      "id": "prod-2",
      "name": "Another Product",
      "price": 20.75
    }
  ]
}
```

## Testando a Aplicação

A aplicação inclui testes unitários utilizando **JUnit 5** e **Mockito**. Para executar os testes, utilize o comando:

```bash
mvn test
```

Os relatórios dos testes serão gerados no diretório `target/surefire-reports`.

## Informações Adicionais

- **Idempotência:** O serviço verifica a duplicidade dos pedidos pelo ID, evitando processamentos redundantes.
- **Precisão Decimal:** O campo `totalAmount` é calculado e arredondado para, no máximo, duas casas decimais usando `BigDecimal`.
- **MapStruct:** É utilizado para mapear entre DTOs e entidades.
- **Logs:** A aplicação utiliza SLF4J com Logback para logging.
- **Cache:** Redis é configurado para cache de consultas a pedidos.

## Autor

Guilherme Guisso da Silva  
[guisso.silva@gmail.com](mailto:guisso.silva@gmail.com)