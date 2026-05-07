package os.infinitytech.os_front_infinitytech.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.config.LoginResult;
import os.infinitytech.os_front_infinitytech.service.AuthService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensagem;

    private final AuthService authService = new AuthService("http://100.71.91.25:8080");

    @FXML
    public void handleLogin() {

        String username = txtUsername.getText();
        String password = txtPassword.getText();

        lblMensagem.setText("Verificando login...");

        Task<LoginResult> task = new Task<>() {
            @Override
            protected LoginResult call() {
                return authService.login(username, password);
            }
        };

        task.setOnSucceeded(event -> {
            LoginResult result = task.getValue();

            if (result.isSuccess()) {
                lblMensagem.setText("Login realizado com sucesso!");
                abrirTelaPrincipal();
            } else {
                lblMensagem.setText(result.getMessage());
            }
        });

        task.setOnFailed(event -> {
            lblMensagem.setText("Erro inesperado ao tentar login.");
        });

        new Thread(task).start();
    }

    private void abrirTelaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os/infinitytech/os_front_infinitytech/view/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sistema OS - InfinityTech");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblMensagem.setText("Erro ao abrir a tela principal.");
        }
    }
}
