package com.geekbrains.cloud.jan;

import com.geekbrains.cloud.jan.model.DatabaseAuthService;
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

public class SignUpController {

    @FXML
    private Button returnBtn;

    @FXML
    private Button authButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField loginField;

    @FXML
    void initialize() {
        authButton.setOnAction(event -> {
            signUpNewUser();
            System.out.println("Новый пользователь успешно добавлен");
        });
        returnBtn.setOnAction(event -> {
            returnBtn.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("signIn.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        });
    }

    private void signUpNewUser() {
        DatabaseAuthService db = new DatabaseAuthService();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String userName = loginField.getText();
        String password = passwordField.getText();
        User user = new User(firstName, lastName, userName, password);
        db.signUpUser(user);
    }
}
