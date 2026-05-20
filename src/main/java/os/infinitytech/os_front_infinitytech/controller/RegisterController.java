package os.infinitytech.os_front_infinitytech.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import os.infinitytech.os_front_infinitytech.auth.AuthService;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void onRegister() {
        try {
            authService.register(
                    nameField.getText(),
                    emailField.getText(),
                    passwordField.getText()
            );

            messageLabel.setText("Usuário registrado com sucesso!");

        } catch (Exception e) {
            messageLabel.setText("Erro: " + e.getMessage());
        }
    }
}
