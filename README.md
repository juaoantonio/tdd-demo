# tdd-demo

Projeto demonstrativo de **TDD em Java** usando Spring Boot 3, JUnit 5, Mockito e RestAssured.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Framework | Spring Boot 3 + Java 21 |
| Persistência | Spring Data JPA + PostgreSQL (produção) / H2 (testes) |
| HTTP | Spring Web + Bean Validation |
| Testes unitários | JUnit 5 + Mockito |
| Testes de integração | @DataJpaTest + @SpringBootTest |
| Testes E2E | RestAssured |

## Estrutura de pacotes

```
br.dev.joaobarbosa.tdddemo
├── domain/          # Entidades e interfaces de repositório (núcleo do negócio)
├── application/     # Casos de uso (OrderService) e exceções
├── infra/
│   └── persistence/ # Adaptadores JPA (JpaOrderRepositoryAdapter)
└── web/             # Controller REST, DTOs e GlobalExceptionHandler
```

## Endpoints

| Método | Caminho | Descrição |
|---|---|---|
| POST | `/orders` | Cria um novo pedido |
| GET | `/orders/{id}` | Busca pedido por ID |

## Pirâmide de testes

```
        /\
       /E2E\         ← OrderE2ETest (RestAssured, @SpringBootTest)
      /------\
     /Integra-\
    / ção      \     ← OrderServiceIntegrationTest, OrderJpaRepositoryTest
   /------------\
  /  Unitários   \   ← OrderServiceTest (Mockito, sem Spring)
 /________________\
```

## Executar

```bash
# Subir banco de dados local (PostgreSQL via Docker)
docker compose up -d

# Rodar todos os testes (usam H2 em memória)
./gradlew clean test

# Subir a aplicação
./gradlew bootRun
```

## Fluxo TDD demonstrado nos commits

1. `chore: init spring project + deps` — scaffold inicial
2. `test: add unit tests for OrderService (red)` — testes escrito antes da implementação **(RED)**
3. `feat: implement OrderService (green)` — mínimo necessário para os testes passarem **(GREEN)**
4. `refactor: clean service validations` — extrair métodos de validação **(REFACTOR)**
5. `feat: add JPA entity + repository` — persistência real com JPA
6. `test: add integration test with H2` — validar wiring do Spring com H2
7. `feat: add controller + DTOs` — expor via API REST
8. `test: add e2e tests with rest-assured` — testes de ponta a ponta
9. `chore: polish packages + error handling` — polimento para apresentação
