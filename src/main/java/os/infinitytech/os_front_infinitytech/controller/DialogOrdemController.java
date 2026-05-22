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

        // 🔥 MELHORIA: Bloqueia a edição manual do nome para forçar a busca via ID correto
        txtClientNome.setEditable(false);

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
            txtClientNome.setText("");
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
                    lblMensagem.setText("");
                } else {
                    txtClientNome.setText("");
                    lblMensagem.setText("⚠️ Cliente não encontrado.");
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                txtClientNome.setText("");
                lblMensagem.setText("❌ Erro ao buscar cliente no servidor.");
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleSalvar() {
        // 🔥 CORREÇÃO: Validação de Segurança dos campos antes de rodar a Thread
        String idText = txtClientId.getText().trim();
        String status = cbStatus.getValue();
        String serial = txtSerial.getText().trim();
        String nomeCliente = txtClientNome.getText().trim();

        if (idText.isEmpty() || nomeCliente.isEmpty()) {
            lblMensagem.setText("⚠️ Forneça um ID de cliente válido.");
            return;
        }
        if (status == null) {
            lblMensagem.setText("⚠️ Selecione um Status.");
            return;
        }
        if (serial.isEmpty()) {
            lblMensagem.setText("⚠️ Informe o Nº de Série/Equipamento.");
            return;
        }

        lblMensagem.setText("Salvando...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Aqui o parse é 100% seguro pois já validamos acima
                Long clientId = Long.parseLong(idText);

                OrdemModel ordem = new OrdemModel();
                ordem.setClientId(clientId);
                ordem.setSerial(serial);
                ordem.setDescricao(txtDescricao.getText() != null ? txtDescricao.getText().trim() : "");
                ordem.setStatus(status);
                ordem.setClientNome(nomeCliente);

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
                System.err.println("Erro ao salvar ordem de serviço:");
                task.getException().printStackTrace();
                lblMensagem.setText("❌ Erro: " + task.getException().getMessage());
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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