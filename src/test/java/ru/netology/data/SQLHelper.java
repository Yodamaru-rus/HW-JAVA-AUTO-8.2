package ru.netology.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class SQLHelper {
    private static final QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @SneakyThrows
    public static void updateCards() {
        var dataSQL = "UPDATE cards SET balance_in_kopecks = 1000000 WHERE number IN ('5559 0000 0000 0001', '5559 0000 0000 0002')";
        try (var conn = getConnection()) {
            runner.update(conn, dataSQL);
        }
    }

    @SneakyThrows
    public static Object getUserId(String login) {
        var dataSQL = "Select id FROM users WHERE login = ?;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new ScalarHandler<>(), login);
        }
    }

    @SneakyThrows
    public static Object getValidationCode(String userID) {
        var dataSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created  DESC LIMIT 1;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new ScalarHandler<>(), userID);
        }
    }

    @SneakyThrows
    public static void deleteTableAuth() {
        var dataSQL = "DELETE FROM auth_codes;";
        try (var conn = getConnection()) {
            runner.update(conn, dataSQL);
        }
    }
}
