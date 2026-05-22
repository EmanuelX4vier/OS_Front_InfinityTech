package os.infinitytech.os_front_infinitytech.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import os.infinitytech.os_front_infinitytech.model.OrdemModel;
import os.infinitytech.os_front_infinitytech.model.ClientModel;
import os.infinitytech.os_front_infinitytech.service.OrdemService;
import os.infinitytech.os_front_infinitytech.service.ClientService;

public class DialogOrdemController {

    @FXML private TextField txtClientId;
    @FXML private TextField txtClientNome;
    @FXML private TextField txtSerial;
    @FXML private ComboBox<String> cbStatus;
    @FXML private TextArea txtDescricao;
    @FXML private Label lblMensagem;

    private final OrdemService service = new OrdemService();
    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {

        cbStatus.getItems().addAll(
                "ANDAMENTO",
                "CONCLUIDO",
                "AGUARDANDO",
                "AUTORIZADO",
                "DISPONIVEL",
                "INDISPONIVEL"
        );

        txtClientNome.setEditable(true);

        // Adiciona ouvinte para disparar a busca quando o campo ID perde o foco
        txtClientId.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                buscarClientePorId();
            }
        });
    }

    private void buscarClientePorId() {
        String idText = txtClientId.getText().trim();
        if (idText.isEmpty()) {
            return;
        }

        Task<ClientModel> task = new Task<>() {
            @Override
            protected ClientModel call() throws Exception {
                try {
                    Long id = Long.parseLong(idText);
                    return clientService.buscarPorId(id);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        };

        task.setOnSucceeded(e -> {
            ClientModel cliente = task.getValue();
            Platform.runLater(() -> {
                if (cliente != null && cliente.getNome() != null) {
                    txtClientNome.setText(cliente.getNome());
                } else {
                    txtClientNome.setText("Cliente não encontrado");
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                txtClientNome.setText("Erro ao buscar cliente");
            });
        });

        new Thread(task).start();
    }

    @FXML
    private void handleSalvar() {

        lblMensagem.setText("Salvando...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                OrdemModel ordem = new OrdemModel();

                ordem.setClientId(
                        Long.parseLong(txtClientId.getText())
                );

                ordem.setSerial(txtSerial.getText());
                ordem.setDescricao(txtDescricao.getText());
                ordem.setStatus(cbStatus.getValue());

                // Salva o nome preenchido no componente visual
                ordem.setClientNome(txtClientNome.getText());

                service.criarOrdem(ordem);

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                lblMensagem.setText("Ordem criada com sucesso!");
                fechar();
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                lblMensagem.setText("Erro: " + task.getException().getMessage());
            });
        });

        new Thread(task).start();
    }

    @FXML
    private void handleCancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtSerial.getScene().getWindow();
        stage.close();
    }
}