package os.infinitytech.os_front_infinitytech.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import os.infinitytech.os_front_infinitytech.model.ServiceModel;
import os.infinitytech.os_front_infinitytech.service.ServiceService;
import os.infinitytech.os_front_infinitytech.service.ClientService;
import os.infinitytech.os_front_infinitytech.util.StatusColor;

import java.io.IOException;
import java.util.List;

public class ServiceController {

    @FXML private TableView<ServiceModel> tblOrdens;
    @FXML private TableColumn<ServiceModel, String> colClientId;
    @FXML private TableColumn<ServiceModel, String> colClient;
    @FXML private TableColumn<ServiceModel, String> colSerial;
    @FXML private TableColumn<ServiceModel, String> colDescricao;
    @FXML private TableColumn<ServiceModel, String> colStatus;
    @FXML private TableColumn<ServiceModel, String> colDataCadastro;

    @FXML private Button btnOrdemAnterior;
    @FXML private Button btnOrdemProxima;
    @FXML private Label lblOrdemPagina;

    private final ObservableList<ServiceModel> listaOrdens = FXCollections.observableArrayList();
    private final ServiceService ordemService = new ServiceService();
    private final ClientService clientService = new ClientService(); // Necessário para o buscarOrdens do seu Service

    private int paginaAtual = 0;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colClientId.setCellValueFactory(cell -> cell.getValue().clientIdProperty());
        colClient.setCellValueFactory(cell -> cell.getValue().clientNomeProperty());
        colSerial.setCellValueFactory(cell -> cell.getValue().serialProperty());
        colDescricao.setCellValueFactory(cell -> cell.getValue().descricaoProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colDataCadastro.setCellValueFactory(cell -> cell.getValue().dataCadastroProperty());
        colStatus.setCellFactory(column -> new StatusColor<ServiceModel>());
    }

    @FXML
    private void carregarDados() {
        // Bloqueia interações rápidas enquanto carrega a API
        btnOrdemAnterior.setDisable(true);
        btnOrdemProxima.setDisable(true);

        Task<List<ServiceModel>> task = new Task<>() {
            @Override
            protected List<ServiceModel> call() throws Exception {
                // Passa o clientService e a página correta conforme especificado no seu OrdemService
                return ordemService.buscarOrdens(clientService, paginaAtual);
            }
        };

        task.setOnSucceeded(event -> {
            List<ServiceModel> ordens = task.getValue();

            Platform.runLater(() -> {
                listaOrdens.clear();
                if (ordens != null && !ordens.isEmpty()) {
                    listaOrdens.addAll(ordens);
                    tblOrdens.setItems(listaOrdens);

                    // Desativa o avançar caso venha menos que a quantidade cheia de uma página (20)
                    btnOrdemProxima.setDisable(ordens.size() < 20);
                } else {
                    tblOrdens.setItems(FXCollections.emptyObservableList());
                    btnOrdemProxima.setDisable(true);
                }

                // Ajusta os estados visuais textuais e de retorno
                lblOrdemPagina.setText("Página: " + (paginaAtual + 1));
                btnOrdemAnterior.setDisable(paginaAtual == 0);
                tblOrdens.refresh();
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Erro ao carregar ordens da página " + paginaAtual);
            task.getException().printStackTrace();

            Platform.runLater(() -> {
                btnOrdemAnterior.setDisable(paginaAtual == 0);
                btnOrdemProxima.setDisable(false);
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleAdicionar() {
        try {
            // 🔥 CORREÇÃO: Garante o caminho e nome exato do seu arquivo FXML (ajuste se for dialogService.fxml)
            String fxmlPath = "/os_front_infinitytech/fxml/dialog/dialogService.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Infinity Tech - Nova Ordem de Serviço");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // Exibe o dialog e trava a tela de trás até que ele seja fechado
            stage.showAndWait();

            // Recarrega a tabela atualizada após fechar o cadastro
            carregarDados();

        } catch (IOException e) {
            System.err.println("Erro ao abrir cadastro de ordem:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrdemAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            carregarDados();
        }
    }

    @FXML
    private void handleOrdemProxima() {
        paginaAtual++;
        carregarDados();
    }

    @FXML
    private void handleAtualizar(ActionEvent event) {
        carregarDados();
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os_front_infinitytech/fxml/initial/home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Infinity Tech - Home");

            // 🔥 CORREÇÃO: Garante que ao voltar para a Home a tela continue cheia
            stage.setMaximized(false);
            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao voltar:");
            e.printStackTrace();
        }
    }
}