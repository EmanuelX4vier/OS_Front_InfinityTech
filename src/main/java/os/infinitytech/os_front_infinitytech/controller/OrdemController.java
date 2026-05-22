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

import os.infinitytech.os_front_infinitytech.model.OrdemModel;
import os.infinitytech.os_front_infinitytech.service.OrdemService;
import os.infinitytech.os_front_infinitytech.service.ClientService;

import java.io.IOException;
import java.util.List;

public class OrdemController {

    @FXML private TableView<OrdemModel> tblOrdens;
    @FXML private TableColumn<OrdemModel, String> colClientId;
    @FXML private TableColumn<OrdemModel, String> colClient;
    @FXML private TableColumn<OrdemModel, String> colSerial;
    @FXML private TableColumn<OrdemModel, String> colDescricao;
    @FXML private TableColumn<OrdemModel, String> colStatus;
    @FXML private TableColumn<OrdemModel, String> colDataCadastro;

    @FXML private Button btnOrdemAnterior;
    @FXML private Button btnOrdemProxima;
    @FXML private Label lblOrdemPagina;

    private final ObservableList<OrdemModel> listaOrdens = FXCollections.observableArrayList();
    private final OrdemService ordemService = new OrdemService();
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
    }

    @FXML
    private void carregarDados() {
        // Bloqueia interações rápidas enquanto carrega a API
        btnOrdemAnterior.setDisable(true);
        btnOrdemProxima.setDisable(true);

        Task<List<OrdemModel>> task = new Task<>() {
            @Override
            protected List<OrdemModel> call() throws Exception {
                // Passa o clientService e a página correta conforme especificado no seu OrdemService
                return ordemService.buscarOrdens(clientService, paginaAtual);
            }
        };

        task.setOnSucceeded(event -> {
            List<OrdemModel> ordens = task.getValue();

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
            // 🔥 CORREÇÃO: Garante o caminho e nome exato do seu arquivo FXML (ajuste se for ordemDialog.fxml)
            String fxmlPath = "/os_front_infinitytech/fxml/ordemDialog.fxml";

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os_front_infinitytech/fxml/home.fxml"));
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