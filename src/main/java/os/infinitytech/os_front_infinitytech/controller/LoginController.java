package os.infinitytech.os_front_infinitytech.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.auth.AuthService;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label lblMensagem;

    private final AuthService authService = new AuthService();

    // 🔐 LOGIN
    @FXML
    public void onLogin() {

        lblMensagem.setText("Entrando...");
        lblMensagem.setStyle("-fx-text-fill: white;");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                authService.login(
                        emailField.getText(),
                        passwordField.getText()
                );

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            lblMensagem.setStyle("-fx-text-fill: green;");
            lblMensagem.setText("Login realizado com sucesso!");

            Platform.runLater(() -> {
                lblMensagem.setText("Login realizado com sucesso!");
                abrirTelaPrincipal();
            });

        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();

            lblMensagem.setStyle("-fx-text-fill: red;");
            lblMensagem.setText("Erro: " + ex.getMessage());
        });

        new Thread(task).start();
    }

    // 🚪 LOGOUT (opcional aqui)
    @FXML
    public void onLogout() {

        try {
            authService.logout();

            // 🔥 limpar UI
            emailField.clear();
            passwordField.clear();

            lblMensagem.setStyle("-fx-text-fill: black;");
            lblMensagem.setText("Sessão encerrada");

            // 🔥 voltar para login ou tela inicial
            Platform.runLater(() -> {
                lblMensagem.setText("Login realizado com sucesso!");
                abrirTelaPrincipal();
            });


        } catch (Exception e) {
            lblMensagem.setStyle("-fx-text-fill: red;");
            lblMensagem.setText("Erro ao fazer logout: " + e.getMessage());
        }
    }

    private void abrirTelaPrincipal() {
        try {
            // AJUSTE: O caminho deve apontar para home.fxml, não para o login novamente
            String path = "/os_front_infinitytech/fxml/home.fxml";
            URL fxmlLocation = getClass().getResource(path);

            if (fxmlLocation == null) {
                throw new IOException("Não foi possível encontrar o arquivo: " + path);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Pega o Stage atual a partir de qualquer elemento da tela (ex: campo de usuário)
            Stage stage = (Stage) emailField.getScene().getWindow();

            // Define a nova cena (Home)
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Infinity Tech - Home");
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela Home: " + e.getMessage());
            e.printStackTrace();
            lblMensagem.setText("Erro técnico: Verifique o console da IDE.");
        }
    }
}