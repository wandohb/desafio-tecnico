package org.caixaverso.simulador.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@DisplayName("SimulacaoResource")
class SimulacaoResourceTest {

    private static final String BASE = "/api/v1/simulacoes";

    @Nested
    @DisplayName("POST " + BASE)
    class Criar {

        @Test
        @DisplayName("retorna 201 com a simulacao criada e memoria de calculo completa")
        void happyPath() {
            String body = """
                    {
                      "valorInicial": 1000.00,
                      "taxaJurosMensal": 1.5,
                      "prazoMeses": 12
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post(BASE)
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("valorInicial", equalTo(1000.00f))
                    .body("taxaJurosMensal", equalTo(1.5f))
                    .body("prazoMeses", equalTo(12))
                    .body("valorTotalFinal", equalTo(1195.62f))
                    .body("valorTotalJuros", equalTo(195.62f))
                    .body("memoriaCalculo", hasSize(12))
                    .body("memoriaCalculo[0].mes", equalTo(1))
                    .body("memoriaCalculo[0].juros", equalTo(15.00f))
                    .body("memoriaCalculo[0].saldoFinal", equalTo(1015.00f))
                    .body("memoriaCalculo[11].mes", equalTo(12))
                    .body("memoriaCalculo[11].juros", equalTo(17.67f))
                    .body("memoriaCalculo[11].saldoFinal", equalTo(1195.62f));
        }

        @Test
        @DisplayName("retorna 400 quando valorInicial eh nulo")
        void rejeitaValorInicialNulo() {
            String body = """
                    {
                      "taxaJurosMensal": 1.5,
                      "prazoMeses": 12
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post(BASE)
                    .then()
                    .statusCode(400)
                    .body("status", equalTo(400))
                    .body("error", equalTo("Bad Request"))
                    .body("message", containsString("valorInicial"))
                    .body("path", containsString("simulacoes"))
                    .body("timestamp", notNullValue());
        }

        @Test
        @DisplayName("retorna 400 quando taxaJurosMensal eh negativa")
        void rejeitaTaxaNegativa() {
            String body = """
                    {
                      "valorInicial": 1000.00,
                      "taxaJurosMensal": -1.5,
                      "prazoMeses": 12
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post(BASE)
                    .then()
                    .statusCode(400)
                    .body("message", containsString("taxaJurosMensal"));
        }

        @Test
        @DisplayName("retorna 400 quando prazoMeses excede o limite de 360")
        void rejeitaPrazoForaDoRange() {
            String body = """
                    {
                      "valorInicial": 1000.00,
                      "taxaJurosMensal": 1.5,
                      "prazoMeses": 999
                    }
                    """;

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post(BASE)
                    .then()
                    .statusCode(400)
                    .body("message", containsString("360"));
        }
    }

    @Nested
    @DisplayName("GET " + BASE + "/{id}")
    class Buscar {

        @Test
        @DisplayName("retorna 200 com a simulacao quando o id existe")
        void retornaSimulacaoExistente() {
            String body = """
                    {
                      "valorInicial": 500.00,
                      "taxaJurosMensal": 2,
                      "prazoMeses": 6
                    }
                    """;

            Long id = given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .when()
                    .post(BASE)
                    .then()
                    .statusCode(201)
                    .extract()
                    .jsonPath()
                    .getLong("id");

            given()
                    .when()
                    .get(BASE + "/" + id)
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(id.intValue()))
                    .body("valorInicial", equalTo(500.00f))
                    .body("prazoMeses", equalTo(6))
                    .body("memoriaCalculo", hasSize(6));
        }

        @Test
        @DisplayName("retorna 404 quando o id nao existe")
        void retorna404QuandoNaoEncontrada() {
            given()
                    .when()
                    .get(BASE + "/99999")
                    .then()
                    .statusCode(404)
                    .body("status", equalTo(404))
                    .body("error", equalTo("Not Found"))
                    .body("message", containsString("99999"))
                    .body("path", containsString("99999"))
                    .body("timestamp", notNullValue());
        }
    }
}
