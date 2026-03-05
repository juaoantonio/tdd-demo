package br.dev.joaobarbosa.tdddemo.web;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/orders";
    }

    @Test
    @DisplayName("POST /orders → 201 com id retornado")
    void shouldCreateOrderAndReturn201() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "customerName": "Alice",
                      "customerEmail": "alice@email.com",
                      "productSku": "SKU-001",
                      "quantity": 2,
                      "total": 99.90
                    }
                    """)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("customerName", equalTo("Alice"))
            .body("customerEmail", equalTo("alice@email.com"))
            .body("productSku", equalTo("SKU-001"))
            .body("quantity", equalTo(2))
            .body("total", equalTo(99.90f))
            .body("status", equalTo("CREATED"));
    }

    @Test
    @DisplayName("GET /orders/{id} → 200 com dados corretos")
    void shouldGetOrderAndReturn200() {
        int id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "customerName": "Bob",
                          "customerEmail": "bob@email.com",
                          "productSku": "SKU-002",
                          "quantity": 1,
                          "total": 250.00
                        }
                        """)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
        .when()
            .get("/{id}", id)
        .then()
            .statusCode(200)
            .body("id", equalTo(id))
            .body("customerName", equalTo("Bob"))
            .body("status", equalTo("CREATED"));
    }

    @Test
    @DisplayName("POST /orders com total <= 0 → 400")
    void shouldReturn400WhenTotalIsInvalid() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "customerName": "Eve",
                      "customerEmail": "eve@email.com",
                      "productSku": "SKU-001",
                      "quantity": 1,
                      "total": 0
                    }
                    """)
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("GET /orders/{id} para id inexistente → 404")
    void shouldReturn404WhenOrderNotFound() {
        given()
        .when()
            .get("/{id}", 999999)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("POST /orders com nome vazio → 400")
    void shouldReturn400WhenCustomerNameIsBlank() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                      "customerName": "",
                      "customerEmail": "eve@email.com",
                      "productSku": "SKU-001",
                      "quantity": 1,
                      "total": 50.00
                    }
                    """)
        .when()
            .post()
        .then()
            .statusCode(400);
    }
}
