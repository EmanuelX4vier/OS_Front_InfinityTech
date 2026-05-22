package os.infinitytech.os_front_infinitytech.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import os.infinitytech.os_front_infinitytech.model.ClientModel;
import os.infinitytech.os_front_infinitytech.service.ClientService;

import java.io.IOException;
import java.util.List;

public class ClientController {

    @FXML private TableView<ClientModel> tblClients;

    @FXML private TableColumn<ClientModel, String> colId;
    @FXML private TableColumn<ClientModel, String> colNome;
    @FXML private TableColumn<ClientModel, String> colTelefone;
    @FXML private TableColumn<ClientModel, String> colEndereco;
    @FXML private TableColumn<ClientModel, String> colDataCadastro;

    private final ObservableList<ClientModel> listaClients =
            FXCollections.observableArrayList();

    private final ClientService clientService =
            new ClientService();

    @FXML
    public void initialize() {

        configurarColunas();

        carregarDados();
    }

    private void configurarColunas() {

        colId.setCellValueFactory(cell ->
                cell.getValue().idProperty());

        colNome.setCellValueFactory(cell ->
                cell.getValue().nomeProperty());

        colTelefone.setCellValueFactory(cell ->
                cell.getValue().telefoneProperty());

        colEndereco.setCellValueFactory(cell ->
                cell.getValue().enderecoProperty());

        colDataCadastro.setCellValueFactory(cell ->
                cell.getValue().dataCadastroProperty());
    }

    // =========================
    // LISTAR CLIENTS
    // =========================
    @FXML
    private void carregarDados() {

        Task<List<ClientModel>> task = new Task<>() {

            @Override
            protected List<ClientModel> call() throws Exception {

                return clientService.buscarClients();
            }
        };

        task.setOnSucceeded(event -> {

            List<ClientModel> clients = task.getValue();

            Platform.runLater(() -> {

                listaClients.clear();

                listaClients.addAll(clients);

                tblClients.setItems(listaClients);

                tblClients.refresh();
            });
        });

        task.setOnFailed(event -> {

            System.err.println("Erro ao carregar clients:");

            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);

        thread.setDaemon(true);

        thread.start();
    }

    // =========================
    // ATUALIZAR
    // =========================
    @FXML
    private void handleAtualizar() {

        carregarDados();
    }

    // =========================
    // ADICIONAR CLIENT
    // =========================
    @FXML
    private void handleAdicionar() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/os_front_infinitytech/fxml/dialogClient.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Infinity Tech - Novo Client");

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));

            stage.setResizable(false);

            stage.showAndWait();

            carregarDados();

        } catch (IOException e) {

            System.err.println("Erro ao abrir cadastro client:");

            e.printStackTrace();
        }
    }

    // =========================
    // VOLTAR
    // =========================
    @FXML
    private void handleVoltar(javafx.event.ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/os_front_infinitytech/fxml/home.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage)
                    ((javafx.scene.Node) event.getSource())
                            .getScene()
                            .getWindow();

            stage.setScene(new Scene(root));

            stage.setTitle("Infinity Tech - Home");

            stage.show();

        } catch (IOException e) {

            System.err.println("Erro ao voltar:");

            e.printStackTrace();
        }
    }
}