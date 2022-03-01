package com.geekbrains.cloud.jan.model;

import java.sql.*;

public class DatabaseAuthService extends MySQLConfigs implements AuthService{
    private static Connection connection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {

        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");

        connection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        return connection;
    }

    public void signUpUser(User user) {
        String insert = "INSERT INTO " + Constants.USER_TABLE + "(" + Constants.USERS_FIRSTNAME + "," +
                Constants.USERS_LASTNAME + "," + Constants.USERS_USERNAME + "," + Constants.USERS_PASSWORD + ")" +
                "VALUES(?,?,?,?)";
        try {
            PreparedStatement ps = getDbConnection().prepareStatement(insert);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getPassword());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getUser(User user)  {
        ResultSet resultSet = null;
        String select = "SELECT * FROM " + Constants.USER_TABLE + " WHERE " + Constants.USERS_USERNAME +
                "=? AND " + Constants.USERS_PASSWORD + "=?";
        try {
            PreparedStatement ps = getDbConnection().prepareStatement(select);
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getPassword());

            resultSet = ps.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    @Override
    public void start() throws SQLException {
        System.out.println("Auth service is running");
    }

    @Override
    public void stop() {
        System.out.println("Auth service is shutting down");
    }
}
