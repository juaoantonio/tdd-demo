# TASK-001 — Gestão de Pedidos

**Time:** Produto / Negócio  
**Assignee:** Dev Backend  
**Sprint:** Sprint 12  
**Prioridade:** Alta  
**Data de criação:** 06/03/2026  
**Status:** To Do  

---

## Contexto

O sistema precisa expor uma API REST para gerenciar pedidos de clientes. O fluxo inicial cobre a **criação** de um pedido e a **consulta** de um pedido existente por identificador. Essas operações são a base do módulo de pedidos e devem estar disponíveis para consumo pelos times de front-end e integrações externas.

---

## Objetivo

Implementar o endpoint de pedidos (`/orders`) com as operações de **criação** e **consulta**, aplicando as validações de negócio definidas abaixo e persistindo os dados em banco de dados relacional.

---

## Requisitos Funcionais

### RF-01 — Criação de Pedido

- O sistema deve permitir a criação de um novo pedido informando **nome do cliente** (`customerName`) e **valor total** (`total`).
- Ao ser criado, o pedido deve ter o status inicial **`CREATED`**.
- O pedido criado deve ser **persistido** no banco de dados e retornar um **identificador único** (`id`) gerado automaticamente.

**Endpoint:** `POST /orders`  
**Resposta de sucesso:** HTTP `201 Created` com o corpo contendo `id`, `customerName`, `total` e `status`.

---

### RF-02 — Consulta de Pedido por ID

- O sistema deve permitir a consulta de um pedido pelo seu **identificador** (`id`).
- Se o pedido existir, deve ser retornado com todos os seus dados: `id`, `customerName`, `total` e `status`.
- Se o pedido **não existir**, o sistema deve retornar um erro de **não encontrado**.

**Endpoint:** `GET /orders/{id}`  
**Resposta de sucesso:** HTTP `200 OK` com o corpo do pedido.  
**Resposta de erro:** HTTP `404 Not Found` quando o ID não existir.

---

## Regras de Negócio

### RN-01 — Valor total obrigatório e positivo

- O campo `total` é **obrigatório**.
- O valor do `total` deve ser **maior que zero**.
- Valores iguais a `0` (zero) ou negativos **não são permitidos** e devem resultar em erro de validação.
- **Resposta esperada em caso de violação:** HTTP `400 Bad Request`.

### RN-02 — Nome do cliente obrigatório e não vazio

- O campo `customerName` é **obrigatório**.
- O nome do cliente **não pode ser nulo**, **vazio** (`""`) ou composto **apenas por espaços em branco** (`"   "`).
- **Resposta esperada em caso de violação:** HTTP `400 Bad Request`.

### RN-03 — Identificador único gerado pelo sistema

- O `id` do pedido deve ser gerado automaticamente pelo sistema no momento da persistência.
- O sistema **não deve aceitar** que o `id` seja informado pelo cliente na criação do pedido.

### RN-04 — Status inicial fixo

- Todo pedido recém-criado deve obrigatoriamente ter o status **`CREATED`**.
- O cliente **não define** o status na criação; ele é atribuído automaticamente pelo sistema.

---

## Modelo de Dados

### Pedido (`Order`)

| Campo          | Tipo           | Obrigatório | Descricao                                       |
|----------------|----------------|-------------|------------------------------------------------|
| `id`           | Long           | Gerado      | Identificador único, gerado pelo banco de dados |
| `customerName` | String         | Sim         | Nome do cliente que realizou o pedido           |
| `total`        | BigDecimal     | Sim         | Valor total do pedido (deve ser > 0)            |
| `status`       | Enum (String)  | Automatico  | Status do pedido — inicialmente `CREATED`       |

---

## Contrato da API

### `POST /orders`

**Request Body:**
```json
{
  "customerName": "Alice",
  "total": 99.90
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "customerName": "Alice",
  "total": 99.90,
  "status": "CREATED"
}
```

**Response `400 Bad Request`** (validacao falhou):
```json
{
  "error": "Validation failed",
  "message": "..."
}
```

---

### `GET /orders/{id}`

**Response `200 OK`:**
```json
{
  "id": 1,
  "customerName": "Alice",
  "total": 99.90,
  "status": "CREATED"
}
```

**Response `404 Not Found`** (pedido nao encontrado):
```json
{
  "error": "Not Found",
  "message": "Order 999999 not found"
}
```

---

## Cenarios de Erro Esperados

| Cenario                                   | HTTP Status | Mensagem esperada                   |
|-------------------------------------------|-------------|-------------------------------------|
| `total` igual a zero                      | `400`       | "Total must be greater than zero"   |
| `total` negativo                          | `400`       | "Total must be greater than zero"   |
| `customerName` vazio (`""`)               | `400`       | "Customer name must not be empty"   |
| `customerName` apenas espacos em branco   | `400`       | "Customer name must not be empty"   |
| `customerName` nulo                       | `400`       | "Customer name must not be empty"   |
| `GET /orders/{id}` com ID inexistente     | `404`       | "Order {id} not found"              |

---

## Criterios Tecnicos (Acordados com o Time de Engenharia)

- A persistência deve ser feita em banco de dados relacional via JPA.
- Cada operação de escrita deve ser garantida por transação.
- Os dados devem sobreviver ao ciclo de vida da aplicação (sem armazenamento em memória).
- O rollback automático deve ser garantido em caso de falha durante a persistência.
- A tabela de pedidos deve iniciar vazia a cada ciclo de testes isolado.

---

## Criterios de Aceite

- [ ] `POST /orders` com dados válidos retorna `201` com `id` gerado, `customerName`, `total` e `status = CREATED`.
- [ ] `GET /orders/{id}` com ID existente retorna `200` com os dados corretos do pedido.
- [ ] `GET /orders/{id}` com ID inexistente retorna `404`.
- [ ] `POST /orders` com `total` igual a zero retorna `400`.
- [ ] `POST /orders` com `total` negativo retorna `400`.
- [ ] `POST /orders` com `customerName` vazio retorna `400`.
- [ ] `POST /orders` com `customerName` contendo apenas espaços retorna `400`.
- [ ] `POST /orders` com `customerName` nulo retorna `400`.
- [ ] O pedido é corretamente persistido no banco de dados e pode ser recuperado após a criação.
- [ ] O `id` é gerado automaticamente pelo banco, sem intervenção do cliente da API.
- [ ] O status inicial é sempre `CREATED`, independentemente do input.

---

## Observacoes

- Esta tarefa cobre apenas o ciclo inicial do pedido (criação e consulta). Funcionalidades como **atualização de status**, **cancelamento** e **listagem** serão tratadas em tarefas futuras.
- O time de QA deve validar os cenários de erro com base na tabela "Cenários de Erro Esperados" acima.
- As mensagens de erro exatas (`"Total must be greater than zero"` e `"Customer name must not be empty"`) devem estar presentes na resposta para facilitar o diagnóstico pelo consumidor da API.

