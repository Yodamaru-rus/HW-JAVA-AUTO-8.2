package ru.netology.rest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.netology.data.SQLHelper.*;

@Slf4j
public class ApiTestV1 {
    int amount = 5000;

    @BeforeAll
    static void setUp() {
        deleteTableAuth();
        updateCards();
    }

    @Test
    void shouldReturnDemoAccounts() {
        given()
                .baseUri("http://localhost:9999")
                .contentType(ContentType.JSON)
                .body("{\"login\": \"vasya\", \"password\": \"qwerty123\"}")
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);


        String verificationCode = getValidationCode(getUserId("vasya").toString()).toString();
        log.info(verificationCode);

        String authToken = given()
                .baseUri("http://localhost:9999")
                .contentType(ContentType.JSON)
                .body("{\"login\": \"vasya\", \"code\": \"" + verificationCode + "\"}")  // проверьте, нужен ли здесь login
                .when()
                .post("/api/auth/verification")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("token");


        Response response = given()
                .baseUri("http://localhost:9999")
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)  // ← ВАЖНО: "Bearer " + токен
                .when()
                .get("/api/cards")
                .then()
                .log().all()  // ← смотрим результат
                .statusCode(200)
                .extract()
                .response();

        int balanceFirstBefore = response.path("[0].balance");
        log.info("balanceFirstBefore = " + balanceFirstBefore);
        int balanceSecondBefore = response.path("[1].balance");
        log.info("balanceSecondBefore = " + balanceSecondBefore);

        given()
                .baseUri("http://localhost:9999")
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)  // ← ВАЖНО: "Bearer " + токен
                .body("{\"from\": \"5559 0000 0000 0002\",\"to\": \"5559 0000 0000 0001\",  \"amount\":" + amount + "}")
                .when()
                .post("/api/transfer")
                .then()
                .log().all()  // ← смотрим результат
                .statusCode(200);

        Response responseAfterTransfer = given()
                .baseUri("http://localhost:9999")
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)  // ← ВАЖНО: "Bearer " + токен
                .when()
                .get("/api/cards")
                .then()
                .log().all()  // ← смотрим результат
                .statusCode(200)
                .extract()
                .response();

        int balanceFirstAfter = responseAfterTransfer.path("[0].balance");
        log.info("balanceFirstAfter = " + balanceFirstAfter);
        int balanceSecondAfter = responseAfterTransfer.path("[1].balance");
        log.info("balanceSecondAfter = " + balanceSecondAfter);
        assertThat(balanceFirstAfter, equalTo(balanceFirstBefore - amount));
        assertThat(balanceSecondAfter, equalTo(balanceSecondBefore + amount));
    }

    @AfterAll
    @SneakyThrows
    static void tearDown() {
        deleteTableAuth();
        log.info("Тест завершен, таблица очищена");
    }
}