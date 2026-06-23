package os.infinitytech.os_front_infinitytech.controller.dialog;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import os.infinitytech.os_front_infinitytech.model.ProduModel;
import os.infinitytech.os_front_infinitytech.model.ServiceModel;
import os.infinitytech.os_front_infinitytech.model.ClientModel;
import os.infinitytech.os_front_infinitytech.service.ServiceService;
import os.infinitytech.os_front_infinitytech.service.ClientService;
import os.infinitytech.os_front_infinitytech.types.Status;

import java.math.BigDecimal;

public class DialogServiceController {

    @FXML private TextField txtClientId;
    @FXML private TextField txtClientNome;
    @FXML private TextField txtSerial;
    @FXML private ComboBox<Status> cbStatus;
    @FXML private TextArea txtDescricao;
    @FXML private Label lblMensagem;

    private final ServiceService serviceService = new ServiceService();
    private final ClientService clientService = new ClientService();
    private ServiceModel serviceRecebido;

    @FXML
    public void initialize() {
        cbStatus.getItems().addAll();

        // 🔥 MELHORIA: Bloqueia a edição manual do nome para forçar a busca via ID correto
        txtClientNome.setEditable(false);

        // Adiciona ouvinte para disparar a busca quando o campo ID perde o foco
        txtClientId.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                buscarClientePorId();
            }
        });
    }

    public void initService(ServiceModel service){
        this.serviceRecebido = service;

        txtClientId.setText(String.valueOf(serviceRecebido.getClientId()));
        txtClientId.setDisable (true);

        txtSerial.setText(service.getSerial());
        txtDescricao.setText(service.getDescricao());
        cbStatus.setValue(Status.valueOf(service.getStatus()));
        txtClientNome.setText(service.getClientNome());

        // Pré-seleciona o Status correto vindo do objeto
        if (service.getStatus() != null) {
            try {
                Status statusEnum = Status.valueOf(service.getStatus().toUpperCase());
                cbStatus.setValue(statusEnum);
            } catch (IllegalArgumentException e) {
                System.err.println("Status inválido retornado pela API: " + service.getStatus());
            }
        }
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
        Status status = cbStatus.getValue();
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

                ServiceModel ordem = new ServiceModel();
                ordem.setClientId(clientId);
                ordem.setSerial(serial);
                ordem.setDescricao(txtDescricao.getText() != null ? txtDescricao.getText().trim() : "");
                ordem.setStatus(String.valueOf(status));
                ordem.setClientNome(nomeCliente);

                serviceService.criarOrdem(ordem);
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
    private void handleEditar(){
        try {
            if (this.serviceRecebido == null) {
                System.err.println("Nenhum produto base encontrado para edição.");
                return;
            }

            ServiceModel service = this.serviceRecebido;

            if(!txtDescricao.getText().trim().isEmpty()){
                service.setDescricao(service.getDescricao());
            }

            if(cbStatus.getValue() != null){
                service.setStatus(String.valueOf(cbStatus.getValue()));
            }

            if(!txtClientNome.getText().trim().isEmpty()){
                service.setClientNome(service.getClientNome());
            }


            new Thread(() -> {
                try {
                    serviceService.atualizarService(this.serviceRecebido.getSerial(), service);
                    Platform.runLater(this::fechar);
                } catch (Exception e) {
                    System.err.println("Erro ao salvar produto:");
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            System.err.println("Erro na validação dos dados:");
            e.printStackTrace();
        }
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