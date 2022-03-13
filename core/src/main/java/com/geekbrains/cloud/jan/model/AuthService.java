package com.geekbrains.cloud.jan.model;

import java.sql.SQLException;


public interface AuthService {

    void start() throws SQLException;

    void stop();

}
