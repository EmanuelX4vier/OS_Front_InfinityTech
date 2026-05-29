package os.infinitytech.os_front_infinitytech.controller.dialog;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import os.infinitytech.os_front_infinitytech.model.ClientModel;
import os.infinitytech.os_front_infinitytech.service.ClientService;

public class DialogClientController {

    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEndereco;

    @FXML private Label lblMensagem;

    private final ClientService clientService = new ClientService();

    // =========================
    // SALVAR CLIENT
    // =========================
    @FXML
    private void handleSalvar() {

        lblMensagem.setText("Salvando...");
        lblMensagem.setStyle("-fx-text-fill: white;");

        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                ClientModel client = new ClientModel();

                client.setNome(txtNome.getText());
                client.setTelefone(txtTelefone.getText());
                client.setEndereco(txtEndereco.getText());

                clientService.criarClient(client);

                return null;
            }
        };

        task.setOnSucceeded(event -> {

            Platform.runLater(() -> {

                lblMensagem.setStyle("-fx-text-fill: #00cc66;");
                lblMensagem.setText("Client cadastrado com sucesso!");

                fecharJanela();
            });
        });

        task.setOnFailed(event -> {

            Throwable ex = task.getException();

            Platform.runLater(() -> {

                lblMensagem.setStyle("-fx-text-fill: red;");
                lblMensagem.setText("Erro: " + ex.getMessage());
            });

            ex.printStackTrace();
        });

        Thread thread = new Thread(task);

        thread.setDaemon(true);

        thread.start();
    }

    // =========================
    // CANCELAR
    // =========================
    @FXML
    private void handleCancelar() {

        fecharJanela();
    }

    // =========================
    // FECHAR JANELA
    // =========================
    private void fecharJanela() {

        Stage stage = (Stage)
                txtNome.getScene().getWindow();

        stage.close();
    }
}