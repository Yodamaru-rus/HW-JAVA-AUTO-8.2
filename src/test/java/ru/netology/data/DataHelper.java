package ru.netology.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataHelper {

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static Transfer getTransfer() {
        return new Transfer("5559 0000 0000 0002", "5559 0000 0000 0001");
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class Transfer {
        String to;
        String from;
    }

}