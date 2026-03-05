package br.dev.joaobarbosa.tdddemo.web;

import br.dev.joaobarbosa.tdddemo.infra.stock.InMemoryStockAdapter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderLifecycleE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryStockAdapter stockAdapter;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/orders";
        stockAdapter.setStock("SKU-001", 100);
    }

    private int createOrder(String customerName, String sku, int qty, double total) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "customerName": "%s",
                          "customerEmail": "%s@email.com",
                          "productSku": "%s",
                          "quantity": %d,
                          "total": %s
                        }
                        """.formatted(customerName, customerName.toLowerCase(), sku, qty, total))
                .when().post()
                .then().statusCode(201)
                .extract().path("id");
    }

    @Test
    @DisplayName("POST /orders/{id}/pay → 200 com status PAID")
    void shouldPayOrderAndReturnPaidStatus() {
        int id = createOrder("Alice", "SKU-001", 2, 99.90);

        given()
        .when()
            .post("/{id}/pay", id)
        .then()
            .statusCode(200)
            .body("status", equalTo("PAID"))
            .body("id", equalTo(id));
    }

    @Test
    @DisplayName("POST /orders/{id}/cancel → 200 com status CANCELLED (pedido CREATED)")
    void shouldCancelCreatedOrderAndReturn200() {
        int id = createOrder("Bob", "SKU-001", 1, 49.90);

        given()
        .when()
            .post("/{id}/cancel", id)
        .then()
            .statusCode(200)
            .body("status", equalTo("CANCELLED"));
    }

    @Test
    @DisplayName("criar → pagar → cancelar com estorno → status CANCELLED")
    void shouldPayAndThenCancelWithRefund() {
        int id = createOrder("Carol", "SKU-001", 3, 299.70);

        given().when().post("/{id}/pay", id).then().statusCode(200);

        given()
        .when()
            .post("/{id}/cancel", id)
        .then()
            .statusCode(200)
            .body("status", equalTo("CANCELLED"));
    }

    @Test
    @DisplayName("POST /orders/{id}/pay num pedido já pago → 409 CONFLICT")
    void shouldReturn409WhenPayingAlreadyPaidOrder() {
        int id = createOrder("Dave", "SKU-001", 1, 75.00);
        given().when().post("/{id}/pay", id).then().statusCode(200);

        given()
        .when()
            .post("/{id}/pay", id)
        .then()
            .statusCode(409);
    }

    @Test
    @DisplayName("POST /orders/{id}/cancel num pedido já cancelado → 409 CONFLICT")
    void shouldReturn409WhenCancellingAlreadyCancelledOrder() {
        int id = createOrder("Eve", "SKU-001", 1, 50.00);
        given().when().post("/{id}/cancel", id).then().statusCode(200);

        given()
        .when()
            .post("/{id}/cancel", id)
        .then()
            .statusCode(409);
    }

    @Test
    @DisplayName("POST /orders/{id}/pay com estoque insuficiente → 422 UNPROCESSABLE_ENTITY")
    void shouldReturn422WhenStockIsInsufficient() {
        stockAdapter.setStock("SKU-001", 0);

        int id = createOrder("Frank", "SKU-001", 5, 250.00);

        given()
        .when()
            .post("/{id}/pay", id)
        .then()
            .statusCode(422);
    }

    @Test
    @DisplayName("POST /orders/{id}/pay para id inexistente → 404")
    void shouldReturn404WhenPayingNonExistentOrder() {
        given()
        .when()
            .post("/{id}/pay", 999999)
        .then()
            .statusCode(404);
    }
}
