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

import os.infinitytech.os_front_infinitytech.controller.dialog.DialogClientController;
import os.infinitytech.os_front_infinitytech.controller.dialog.DialogStockController;
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

    @FXML private Button btnClientAnterior;
    @FXML private Button btnClientProxima;
    @FXML private Label lblClientPagina;

    private final ObservableList<ClientModel> listaClientes = FXCollections.observableArrayList();
    private final ClientService clientService = new ClientService();

    private int paginaAtual = 0;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();

        tblClients.setOnMouseClicked(event -> {
           if(event.getClickCount() == 2 && tblClients.getSelectionModel().getSelectedItem() != null){
               ClientModel clientSelecionado = tblClients.getSelectionModel().getSelectedItem();
               handleEditar(clientSelecionado);
           }
        });
    }

    private void configurarColunas() {
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colNome.setCellValueFactory(cell -> cell.getValue().nomeProperty());
        colTelefone.setCellValueFactory(cell -> cell.getValue().telefoneProperty());
        colEndereco.setCellValueFactory(cell -> cell.getValue().enderecoProperty());
        colDataCadastro.setCellValueFactory(cell -> cell.getValue().dataCadastroProperty());
    }

    @FXML
    private void carregarDados() {
        // Desativa temporariamente os botões para evitar cliques duplos rápidos
        btnClientAnterior.setDisable(true);
        btnClientProxima.setDisable(true);

        Task<List<ClientModel>> task = new Task<>() {
            @Override
            protected List<ClientModel> call() throws Exception {
                // Passa a página atual corretamente para o Service
                return clientService.buscarClients(paginaAtual);
            }
        };

        task.setOnSucceeded(event -> {
            List<ClientModel> clientes = task.getValue();

            Platform.runLater(() -> {
                listaClientes.clear();
                if (clientes != null && !clientes.isEmpty()) {
                    listaClientes.addAll(clientes);
                    tblClients.setItems(listaClientes);

                    // Se o backend retornou 20 registros completos, pode haver uma próxima página
                    btnClientProxima.setDisable(clientes.size() < 20);
                } else {
                    tblClients.setItems(FXCollections.emptyObservableList());
                    btnClientProxima.setDisable(true);
                }

                // Atualiza o texto visual (Exibição amigável: index 0 vira Página 1)
                lblClientPagina.setText("Página: " + (paginaAtual + 1));

                // Controle do botão Anterior
                btnClientAnterior.setDisable(paginaAtual == 0);
                tblClients.refresh();
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Erro ao carregar clientes da página " + paginaAtual);
            task.getException().printStackTrace();

            Platform.runLater(() -> {
                btnClientAnterior.setDisable(paginaAtual == 0);
                btnClientProxima.setDisable(false);
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleClientAnterior() {
        if (paginaAtual > 0) {
            paginaAtual--;
            carregarDados();
        }
    }

    @FXML
    private void handleClientProxima() {
        paginaAtual++;
        carregarDados();
    }

    @FXML
    private void handleAtualizar(ActionEvent event) {
        carregarDados();
    }

    @FXML
    private void handleAdicionar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os_front_infinitytech/fxml/dialog/dialogClient.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Infinity Tech - Novo Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            // Recarrega os dados após fechar o modal
            carregarDados();
        } catch (IOException e) {
            System.err.println("Erro ao abrir cadastro cliente:");
            e.printStackTrace();
        }
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

    private void handleEditar(ClientModel client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os_front_infinitytech/fxml/dialog/edits/dialogEditClient.fxml"));
            Parent root = loader.load();
            DialogClientController dialogController = loader.getController();
            dialogController.initClient(client);
            Stage stage = new Stage();
            stage.setTitle("Infinity Tech - Editar client");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            carregarDados();

        } catch (IOException e) {
            System.err.println("Erro ao abrir tela");
            e.printStackTrace();
        }
    }
}