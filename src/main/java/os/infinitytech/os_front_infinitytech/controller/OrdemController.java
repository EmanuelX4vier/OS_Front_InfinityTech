package os.infinitytech.os_front_infinitytech.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
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

    private final ObservableList<OrdemModel> listaOrdens = FXCollections.observableArrayList();
    private final OrdemService ordemService = new OrdemService();
    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    // =========================
    // CONFIGURAR COLUNAS
    // =========================
    private void configurarColunas() {
        colClientId.setCellValueFactory(cell -> cell.getValue().clientIdProperty());
        colClient.setCellValueFactory(cell -> cell.getValue().clientNomeProperty());
        colSerial.setCellValueFactory(cell -> cell.getValue().serialProperty());
        colDescricao.setCellValueFactory(cell -> cell.getValue().descricaoProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colDataCadastro.setCellValueFactory(cell -> cell.getValue().dataCadastroProperty());

        // =========================
        // DESCRIÇÃO MULTILINHA E BRANCA
        // =========================
        colDescricao.setCellFactory(column -> new TableCell<>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(
                        column.widthProperty().subtract(15)
                );
                text.setFill(javafx.scene.paint.Color.WHITE);

                setGraphic(text);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    text.setText(null);
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });

        tblOrdens.setFixedCellSize(-1);
    }

    // =========================
    // LISTAR ORDENS
    // =========================
    @FXML
    private void carregarDados() {
        Task<List<OrdemModel>> task = new Task<>() {
            @Override
            protected List<OrdemModel> call() throws Exception {
                return ordemService.buscarOrdens(clientService);
            }
        };

        task.setOnSucceeded(event -> {
            List<OrdemModel> ordens = task.getValue();
            Platform.runLater(() -> {
                listaOrdens.clear();
                listaOrdens.addAll(ordens);
                tblOrdens.setItems(listaOrdens);
                tblOrdens.refresh(); // Força o JavaFX a renderizar os nomes que foram preenchidos
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Erro ao carregar ordens:");
            task.getException().printStackTrace();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // =========================
    // ADICIONAR
    // =========================
    @FXML
    private void handleAdicionar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/os_front_infinitytech/fxml/ordemDialog.fxml"
                    )
            );

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Infinity Tech - Nova Ordem");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            carregarDados();

        } catch (IOException e) {
            System.err.println("Erro ao abrir ordem:");
            e.printStackTrace();
        }
    }

    // =========================
    // ATUALIZAR
    // =========================
    @FXML
    private void handleAtualizar() {
        carregarDados();
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
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Infinity Tech - Home");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao voltar:");
            e.printStackTrace();
        }
    }
}