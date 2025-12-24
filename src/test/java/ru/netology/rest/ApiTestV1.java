package ru.netology.rest;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ru.netology.data.APIHelper.*;
import static ru.netology.data.DataHelper.getAuthInfo;
import static ru.netology.data.DataHelper.getTransfer;
import static ru.netology.data.SQLHelper.*;

@Slf4j
public class ApiTestV1 {
    int amount = 10001;

    @BeforeAll
    static void setUp() {
        deleteTableAuth();
        updateCards();
    }

    @Test
    void shouldReturnDemoAccounts() {
        login(getAuthInfo().getLogin(), getAuthInfo().getPassword());

        String authToken = authToken(getAuthInfo().getLogin());

        Response response = response(authToken);

        int balanceFirstBefore = response.path("[0].balance");
        log.info("balanceFirstBefore = " + balanceFirstBefore);
        int balanceSecondBefore = response.path("[1].balance");
        log.info("balanceSecondBefore = " + balanceSecondBefore);
        amount = new Random().nextInt(Math.abs(balanceFirstBefore));

        transfer(authToken, getTransfer().getTo(), getTransfer().getFrom(), amount);

        Response responseAfterTransfer = response(authToken);

        int balanceFirstAfter = responseAfterTransfer.path("[0].balance");
        log.info("balanceFirstAfter = " + balanceFirstAfter);
        int balanceSecondAfter = responseAfterTransfer.path("[1].balance");
        log.info("balanceSecondAfter = " + balanceSecondAfter);

        assertThat(balanceFirstAfter, equalTo(balanceFirstBefore - amount));
        assertThat(balanceSecondAfter, equalTo(balanceSecondBefore + amount));
    }

    @AfterAll
    static void tearDown() {
        deleteTableAuth();
        log.info("Тест завершен, таблица очищена");
    }
}