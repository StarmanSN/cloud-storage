package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.DatabaseAuthService;
import com.geekbrains.cloud.jan.model.WrongAnimation;
import com.geekbrains.cloud.jan.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInController {

    @FXML
    private Button loginSignUpButton;

    @FXML
    private Button authButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    void initialize() {
        loginSignUpButton.setOnAction(event -> {
           openNewWindow("signUp.fxml");
        });

        authButton.setOnAction(event -> {
            String loginText = loginField.getText().trim();
            String passwordText = passwordField.getText().trim();
            if (!loginText.equals("") && !passwordText.equals("")) {
                loginUser(loginText, passwordText);
            } else {
                System.out.println("Введено пустое поле логина/пароля");
            }
        });
    }

    private void loginUser(String loginText, String passwordText) {
        DatabaseAuthService db = new DatabaseAuthService();
        User user = new User();
        user.setUserName(loginText);
        user.setPassword(passwordText);
        ResultSet rs = db.getUser(user);
        int counter = 0;
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            counter++;
        }
        if (counter >= 1) {
            System.out.println("Пользователь успешно авторизован");
            openNewWindow("cloudStorage.fxml");
        } else {
            System.out.println("Введен неверный логин/пароль");
            WrongAnimation userLoginWrongAnimation = new WrongAnimation(loginField);
            WrongAnimation userPassWrongAnimation = new WrongAnimation(passwordField);
            userLoginWrongAnimation.playAnimation();
            userPassWrongAnimation.playAnimation();
        }
    }

    public void openNewWindow(String window) {
        loginSignUpButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
}
