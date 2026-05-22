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

import os.infinitytech.os_front_infinitytech.model.ProduModel;
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

    // Componentes de Paginação injetados do FXML
    @FXML private Button btnPaginaAnterior;
    @FXML private Button btnProximaPagina;
    @FXML private Label lblPaginaAtual;

    private final ObservableList<ProduModel> listaProdutos = FXCollections.observableArrayList();
    private final ProduService produService = new ProduService();

    // Controladores de estado da paginação (Backend Spring costuma ser indexado em 0)
    private int paginaAtual = 0;

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

    @FXML
    private void carregarDados() {
        // Bloqueia interações rápidas enquanto carrega do backend
        btnPaginaAnterior.setDisable(true);
        btnProximaPagina.setDisable(true);

        Task<List<ProduModel>> task = new Task<>() {
            @Override
            protected List<ProduModel> call() throws Exception {
                // Passando a página atual de forma correta para o Service
                return produService.buscarProdutos(paginaAtual);
            }
        };

        task.setOnSucceeded(event -> {
            List<ProduModel> produtos = task.getValue();

            Platform.runLater(() -> {
                listaProdutos.clear();
                if (produtos != null && !produtos.isEmpty()) {
                    listaProdutos.addAll(produtos);
                    tblEstoque.setItems(listaProdutos);

                    // Se retornou 20 itens, possivelmente há uma próxima página ativa
                    btnProximaPagina.setDisable(produtos.size() < 20);
                } else {
                    tblEstoque.setItems(FXCollections.emptyObservableList());
                    btnProximaPagina.setDisable(true);
                }

                // Atualiza o texto visual (Ex: Interface exibe "Página 1" para index 0)
                lblPaginaAtual.setText("Página: " + (paginaAtual + 1));

                // Valida se o botão anterior deve ficar ativo
                btnPaginaAnterior.setDisable(paginaAtual == 0);
                tblEstoque.refresh();
            });
        });

        task.setOnFailed(event -> {
            System.err.println("Erro ao carregar estoque na página " + paginaAtual);
            task.getException().printStackTrace();

            Platform.runLater(() -> {
                btnPaginaAnterior.setDisable(paginaAtual == 0);
                btnProximaPagina.setDisable(false);
            });
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handlePaginaAnterior(ActionEvent event) {
        if (paginaAtual > 0) {
            paginaAtual--;
            carregarDados();
        }
    }

    @FXML
    private void handleProximaPagina(ActionEvent event) {
        paginaAtual++;
        carregarDados();
    }

    @FXML
    private void handleAdicionar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os_front_infinitytech/fxml/dialogProdu.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Infinity Tech - Novo Produto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            // Recarrega a página atual para listar o novo item se necessário
            carregarDados();

        } catch (IOException e) {
            System.err.println("Erro ao abrir tela de cadastro:");
            e.printStackTrace();
        }
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