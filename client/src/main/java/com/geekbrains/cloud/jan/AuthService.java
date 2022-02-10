package com.geekbrains.cloud.jan;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthService {

    void start() throws SQLException;

    void stop();

    Optional<String> getNickByLoginAndPass(String login, String pass);
}
