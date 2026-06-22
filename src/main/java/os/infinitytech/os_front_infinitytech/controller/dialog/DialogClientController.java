package os.infinitytech.os_front_infinitytech.controller.dialog;

import com.almasb.fxgl.net.Client;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import os.infinitytech.os_front_infinitytech.model.ClientModel;
import os.infinitytech.os_front_infinitytech.service.ClientService;

public class DialogClientController {

    @FXML private TextField txtId;
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEndereco;

    @FXML private Label lblMensagem;

    private final ClientService clientService = new ClientService();
    private ClientModel clientRecebido;

    public void initClient(ClientModel client){
        this.clientRecebido = client;

        txtId.setText(client.getId().toString());
        txtId.setDisable(true);

        txtNome.setText(client.getNome());
        txtTelefone.setText(client.getTelefone());
        txtEndereco.setText(clientRecebido.getEndereco());
    }

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

    @FXML
    private void handleApagar(){

        fecharJanela();
    }

    @FXML
    private void handleEditar(){
        try {
            if (this.clientRecebido == null) {
                System.err.println("Nenhum cliente base encontrado para edição");
                return;
            }

            ClientModel client = this.clientRecebido;

            if (!txtNome.getText().trim().isEmpty()) {
                client.setNome(txtNome.getText().trim());
            }

            if (!txtTelefone.getText().trim().isEmpty()) {
                client.setTelefone(txtTelefone.getText().trim());
            }

            if (!txtEndereco.getText().trim().isEmpty()) {
                client.setEndereco(txtEndereco.getText().trim());
            }

            new Thread(() -> {
                try {
                    clientService.atualizarClient(this.clientRecebido.getId(), client);
                    Platform.runLater(this::fecharJanela);
                } catch (Exception e) {
                    System.err.println("Erro ao salvar cliente:");
                    e.printStackTrace();
                }
            }).start();

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
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