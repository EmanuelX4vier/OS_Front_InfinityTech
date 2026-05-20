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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.model.ProduModel;
import os.infinitytech.os_front_infinitytech.service.ProduService;
import os.infinitytech.os_front_infinitytech.service.ProduService;

import java.io.IOException;
import java.util.List;

public class EstoqueController {

    @FXML private TableView<ProduModel> tblEstoque;
    @FXML private TableColumn<ProduModel, String> colCodigo;
    @FXML private TableColumn<ProduModel, String> colNome;
    @FXML private TableColumn<ProduModel, String> colMarca;
    @FXML private TableColumn<ProduModel, String> colStatus;
    @FXML private TableColumn<ProduModel, String> colQuantidade;
    @FXML private TableColumn<ProduModel, String> colValorCompra;
    @FXML private TableColumn<ProduModel, String> colValorVenda;

    private final ObservableList<ProduModel> listaProdutos = FXCollections.observableArrayList();

    private final ProduService produtoService = new ProduService();

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {
        colCodigo.setCellValueFactory(cell -> cell.getValue().codigoProperty());
        colNome.setCellValueFactory(cell -> cell.getValue().nomeProperty());
        colMarca.setCellValueFactory(cell -> cell.getValue().marcaProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colQuantidade.setCellValueFactory(cell -> cell.getValue().quantidadeProperty());
        colValorCompra.setCellValueFactory(cell -> cell.getValue().valorCompraProperty());
        colValorVenda.setCellValueFactory(cell -> cell.getValue().valorVendaProperty());
    }

    // 🔥 LISTAR PRODUTOS
    @FXML
    private void carregarDados() {

        Task<List<ProduModel>> task = new Task<>() {
            @Override
            protected List<ProduModel> call() throws Exception {
                return produtoService.buscarProdutos();
            }
        };

        task.setOnSucceeded(e -> {
            List<ProduModel> produtos = task.getValue();

            Platform.runLater(() -> {
                listaProdutos.setAll(produtos);
                tblEstoque.setItems(listaProdutos);
                tblEstoque.refresh();
            });
        });

        task.setOnFailed(e -> {
            System.err.println("Erro ao carregar estoque:");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    // 🔥 ABRIR CADASTRO
    @FXML
    private void handleAdicionar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/os_front_infinitytech/fxml/dialogProdu.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Infinity Tech - Novo Produto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            carregarDados();

        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de cadastro: " + e.getMessage());
        }
    }

    // 🔥 REFRESH
    @FXML
    private void handleAtualizar(ActionEvent event) {
        carregarDados();
    }

    // 🔥 VOLTAR
    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/os_front_infinitytech/fxml/home.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Infinity Tech - Home");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao voltar: " + e.getMessage());
        }
    }
}