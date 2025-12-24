package ru.netology.data;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import static ru.netology.data.SQLHelper.getUserId;
import static ru.netology.data.SQLHelper.getValidationCode;

@Slf4j
public class APIHelper {

    public static RequestSpecification given() {
        return RestAssured.given()
                .baseUri("http://localhost:9999")
                .contentType(ContentType.JSON);
    }

    private static RequestSpecification givenWithAuth(String token) {
        return given().header("Authorization", "Bearer " + token);
    }

    private static String buildLoginJson(String login, String password) {
        return String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password);
    }

    private static String buildVerificationJson(String login, String code) {
        return String.format("{\"login\": \"%s\", \"code\": \"%s\"}", login, code);
    }

    private static String buildTransferJson(String from, String to, int amount) {
        return String.format("{\"from\": \"%s\",\"to\": \"%s\",\"amount\":%d}", from, to, amount);
    }

    public static void login(String login, String password) {
        given()
                .body(buildLoginJson(login, password))
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static String getVerificationCode(String login) {
        String code = getValidationCode(getUserId(login).toString()).toString();
        log.info(code);
        return code;
    }

    public static String authToken(String login) {

        return given()
                .body(buildVerificationJson(login, getVerificationCode(login)))
                .when()
                .post("/api/auth/verification")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static Response response(String authToken) {
        return givenWithAuth(authToken)
                .when()
                .get("/api/cards")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
    }


    public static void transfer(String authToken, String from, String to, int amount) {
        givenWithAuth(authToken)
                .body(buildTransferJson(from, to, amount))
                .when()
                .post("/api/transfer")
                .then()
                .log().all()
                .statusCode(200);
    }
}
