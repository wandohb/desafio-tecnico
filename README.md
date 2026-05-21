# Simulador de Financiamentos

API REST que calcula juros compostos sobre uma operação de crédito, persiste a memória de cálculo mês a mês e permite consultar simulações anteriores.

**Stack:** Java 25 · Quarkus 3.35 · Hibernate ORM (Panache) · H2 embarcado · OpenAPI/Swagger · Jacoco com gate de 80%.

---

## Pré-requisitos

- **JDK 25** instalado e `JAVA_HOME` apontando pra ele. Temurin recomendado.
- O wrapper `./mvnw` (incluso no projeto) cuida do Maven — não precisa instalar separado.
- **Sem Docker.** Tudo roda nativo na máquina, conforme requisito do desafio.

Verificar o JDK:

```bash
java -version    # deve mostrar 25.x.x
```

---

## Rodar em dev mode

```bash
./mvnw quarkus:dev
```

Após ~10s de boot, a API fica em **`http://localhost:8080`**.

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/q/swagger-ui |
| OpenAPI (JSON/YAML) | http://localhost:8080/q/openapi |
| Quarkus Dev UI (só em dev) | http://localhost:8080/q/dev |

Atalhos do console interativo:

| Tecla | Ação |
|---|---|
| `w` | Abre o app no browser |
| `d` | Abre o Quarkus Dev UI |
| `r` | Re-executa os testes |
| `h` | Lista todos os atalhos |
| `q` | Encerra o servidor |

---

## Rodar testes e validar cobertura

```bash
./mvnw verify
```

Esse comando único:

1. Compila o código
2. Executa todos os testes unitários e de integração
3. Gera o relatório Jacoco em `target/site/jacoco/index.html`
4. **Valida o gate de cobertura mínima de 80%** (`LINE` e `BRANCH`). Se cair abaixo, o build falha com `BUILD FAILURE`.

Abrir o relatório de cobertura:

```bash
# Windows
start target/site/jacoco/index.html

# Mac
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html
```

> **Excluídos do gate por convenção:**
> - `**/dto/**` — records puros sem lógica
> - `**/persistence/**` — `SimulacaoRepository` é marker class do Panache (sem comportamento próprio)
>
> O `lombok.config` na raiz marca código gerado pelo Lombok com `@Generated`, que o Jacoco ignora automaticamente.

---

## Endpoints

### `POST /api/v1/simulacoes` — criar simulação

```bash
curl -X POST http://localhost:8080/api/v1/simulacoes \
  -H "Content-Type: application/json" \
  -d '{"valorInicial":1000.00,"taxaJurosMensal":1.5,"prazoMeses":12}'
```

**Sucesso (`201 Created`):**

```json
{
  "id": 1,
  "valorInicial": 1000.00,
  "taxaJurosMensal": 1.50,
  "prazoMeses": 12,
  "valorTotalFinal": 1195.62,
  "valorTotalJuros": 195.62,
  "criadoEm": "2026-05-21T10:00:00Z",
  "memoriaCalculo": [
    {"mes": 1,  "saldoInicial": 1000.00, "juros": 15.00, "saldoFinal": 1015.00},
    {"mes": 2,  "saldoInicial": 1015.00, "juros": 15.22, "saldoFinal": 1030.22},
    "... (12 entradas no total)"
  ]
}
```

**Validação (`400 Bad Request`)** para qualquer campo nulo, valor ≤ 0, taxa negativa ou prazo fora de `[1, 360]`:

```json
{
  "timestamp": "2026-05-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "valorInicial nao pode ser nulo",
  "path": "/api/v1/simulacoes"
}
```

### `GET /api/v1/simulacoes/{id}` — buscar simulação

```bash
curl http://localhost:8080/api/v1/simulacoes/1
```

**Sucesso (`200 OK`):** mesmo schema do POST response.

**Não encontrada (`404 Not Found`):**

```json
{
  "timestamp": "2026-05-21T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Simulacao com id 99999 nao encontrada",
  "path": "/api/v1/simulacoes/99999"
}
```

---

## Estrutura do projeto

```
src/main/java/org/caixaverso/simulador/
├── api/                       # Camada HTTP (JAX-RS)
│   ├── SimulacaoResource.java
│   ├── dto/                   # Records de Request, Response, ErrorResponse
│   └── exception/             # ExceptionMappers (400, 404)
├── service/                   # Orquestração
│   ├── SimulacaoService.java          (interface)
│   ├── SimulacaoServiceImpl.java
│   └── calculo/               # Lógica pura de juros compostos
│       ├── CalculoJurosService.java   (interface)
│       ├── CalculoJurosCompostosImpl.java
│       └── ResultadoCalculo / ParcelaCalculada (records)
├── persistence/               # PanacheRepository
│   └── SimulacaoRepository.java
└── domain/                    # Entidades JPA + exceptions de domínio
    ├── Simulacao.java
    ├── Parcela.java
    └── exception/
        ├── ParametroSimulacaoInvalidoException.java   (→ HTTP 400)
        └── SimulacaoNaoEncontradaException.java       (→ HTTP 404)
```

---

## Decisões arquiteturais

- **Camadas separadas com interfaces** (Resource → Service → Repository). Dependências injetadas via construtor pra facilitar mock em testes.
- **`BigDecimal` em todos os valores financeiros** com `MathContext.DECIMAL128` em divisões e `RoundingMode.HALF_EVEN` (banker's rounding) em arredondamentos. Padrão IEEE 754 financeiro, evita viés acumulado positivo do `HALF_UP`.
- **Cálculo isolado** (`CalculoJurosService`) sem dependências de framework. Pode ser testado sem subir o Quarkus.
- **Repository pattern** (não Active Record) — entity desacoplada do Panache.
- **Schema DECIMAL reflete o domínio** — `DECIMAL(15,2)` pra monetário, `DECIMAL(6,2)` pra taxa. Sem hack de normalização na apresentação: a representação no banco já está na escala do domínio.
- **Memória de cálculo persistida** (não recalculada no GET) — auditabilidade financeira: cliente recebe sempre o mesmo valor que viu na criação, mesmo se o algoritmo evoluir.
- **Exceptions custom de domínio** mapeadas para HTTP status precisos (400 e 404), evitando catch-all em `IllegalArgumentException` (que mascararia bugs internos como erros de cliente).
- **OpenAPI gerado automaticamente** via `quarkus-smallrye-openapi` — contrato sempre em sincronia com o código.

---

## Persistência local (H2 file mode)

Em dev mode, o H2 grava em `./data/simulador.mv.db`. Pra zerar o banco:

```bash
rm -rf data/
```

Testes usam **H2 in-memory** com schema recriado a cada execução (`drop-and-create`), via perfil `%test` em [application.properties](src/main/resources/application.properties). Não afetam o banco de dev.

---

## CI

Esteira GitHub Actions em [.github/workflows/ci.yml](.github/workflows/ci.yml). Em cada `push` ou `pull_request` pra `main`:

1. Checkout
2. Setup JDK 25 Temurin (com cache Maven)
3. `./mvnw -B -ntp verify` (compila + testa + valida cobertura ≥ 80%)
4. Publica o relatório Jacoco como artefato (retenção 14 dias)
