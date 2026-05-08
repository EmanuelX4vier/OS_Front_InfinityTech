package os.infinitytech.os_front_infinitytech.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.config.AppConfig;
import os.infinitytech.os_front_infinitytech.config.LoginResult;
import os.infinitytech.os_front_infinitytech.service.AuthService;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensagem;

    private final AuthService authService = new AuthService(AppConfig.API_BASE_URL);

    @FXML
    public void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblMensagem.setText("Preencha todos os campos.");
            return;
        }

        lblMensagem.setText("Verificando login...");

        // Task para não travar a UI durante a autenticação Basic Auth
        Task<LoginResult> task = new Task<>() {
            @Override
            protected LoginResult call() {
                return authService.login(username, password);
            }
        };

        task.setOnSucceeded(event -> {
            LoginResult result = task.getValue();
            if (result.isSuccess()) {
                // SALVA A SESSÃO: Guarda o serviço autenticado para uso posterior
                os.infinitytech.os_front_infinitytech.config.Session.setAuthService(this.authService);

                Platform.runLater(() -> {
                    lblMensagem.setText("Login realizado com sucesso!");
                    abrirTelaPrincipal();
                });
            } else {
                lblMensagem.setText(result.getMessage());
            }
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> lblMensagem.setText("Erro de conexão com o servidor."));
        });

        new Thread(task).start();
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
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            // Define a nova cena (Home)
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Infinity Tech - Home");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela Home: " + e.getMessage());
            e.printStackTrace();
            lblMensagem.setText("Erro técnico: Verifique o console da IDE.");
        }
    }
}