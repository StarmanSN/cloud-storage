package com.geekbrains.cloud.jan;

import java.sql.*;
import java.util.Optional;

public class DbAuthService implements AuthService {

    private static Connection connection;
    private static Statement statement;

    @Override
    public void start() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:javaServer.db");
        statement = connection.createStatement();
        System.out.println("Сервер с базой данных запущен");
    }

    @Override
    public void stop() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Сервер с базой данных остановлен");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Optional<String> getNickByLoginAndPass(String login, String pass) {
        try {
            try (ResultSet rs = statement.executeQuery("SELECT * from Users")) {
                while (rs.next()) {
                    if (rs.getString(1).equals(login) && rs.getString(2).equals(pass)) {
                        return Optional.ofNullable(rs.getString(3));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
