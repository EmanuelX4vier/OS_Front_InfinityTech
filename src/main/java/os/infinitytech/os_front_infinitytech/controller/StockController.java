package os.infinitytech.os_front_infinitytech.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import os.infinitytech.os_front_infinitytech.controller.dialog.DialogStockController;
import os.infinitytech.os_front_infinitytech.model.ProduModel;
import os.infinitytech.os_front_infinitytech.service.StockService;
import os.infinitytech.os_front_infinitytech.util.StatusColor;

import java.io.IOException;
import java.util.List;

public class StockController {

    @FXML private TableView<ProduModel> tblEstoque;
    @FXML private TableColumn<ProduModel, String> colCodigo;
    @FXML private TableColumn<ProduModel, String> colNome;
    @FXML private TableColumn<ProduModel, String> colMarca;
    @FXML private TableColumn<ProduModel, String> colStatus;
    @FXML private TableColumn<ProduModel, String> colQuantidade;
    @FXML private TableColumn<ProduModel, String> colValorCompra;
    @FXML private TableColumn<ProduModel, String> colValorVenda;

    @FXML private TextField txtPesquisa;

    // Componentes de Paginação injetados do FXML
    @FXML private Button btnPaginaAnterior;
    @FXML private Button btnProximaPagina;
    @FXML private Label lblPaginaAtual;

    private final ObservableList<ProduModel> listaProdutos = FXCollections.observableArrayList();
    private final StockService stockService = new StockService();

    // Controladores de estado da paginação (Backend Spring costuma ser indexado em 0)
    private int paginaAtual = 0;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();

        //"Escuta" duplo clique para abrir tela de editar item.
        tblEstoque.setOnMouseClicked(event ->{
            //Pega a contagem dos cliques | Verifica se o item da tabela está vazia.
            if(event.getClickCount() == 2 && tblEstoque.getSelectionModel().getSelectedItem() != null) {
                ProduModel produtoSelecionado = tblEstoque.getSelectionModel().getSelectedItem();
                handleEditar(produtoSelecionado);
            }
        });

        // Cria um cronomentro para dar tempo de o usuario escrever.
        PauseTransition debounce = new PauseTransition(Duration.millis(400));

        // Define o que acontece quando o usuário para de digitar.
        debounce.setOnFinished(event -> {
            paginaAtual = 0; // Reseta para a primeira página em uma nova busca.
            carregarDados(); // Chama o carregador que agora engloba a busca.
        });

        // Escuta cada tecla digitada no campo de pesquisa para disparar o timer
        txtPesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            debounce.playFromStart();
        });
    }

    private void configurarColunas() {

        //Exibição das informações nas células.
        colCodigo.setCellValueFactory(cell -> cell.getValue().codigoProperty());
        colNome.setCellValueFactory(cell -> cell.getValue().nomeProperty());
        colMarca.setCellValueFactory(cell -> cell.getValue().marcaProperty());
        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());
        colStatus.setCellFactory(column -> new StatusColor<ProduModel>());
        colQuantidade.setCellValueFactory(cell -> cell.getValue().quantidadeProperty());
        colValorCompra.setCellValueFactory(cell -> cell.getValue().valorCompraProperty());
        colValorVenda.setCellValueFactory(cell -> cell.getValue().valorVendaProperty());

    }

    @FXML
    private void carregarDados() {
        // Bloqueia interações rápidas enquanto carrega do backend
        btnPaginaAnterior.setDisable(true);
        btnProximaPagina.setDisable(true);

        String termoTratado = txtPesquisa.getText() != null ? txtPesquisa.getText().trim() : "";

        Task<List<ProduModel>> task = new Task<>() {
            @Override
            protected List<ProduModel> call() throws Exception {
                return stockService.pesquisarProdutos(termoTratado, paginaAtual);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os_front_infinitytech/fxml/dialog/dialogStock.fxml"));
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

    private void handleEditar(ProduModel produ){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/os_front_infinitytech/fxml/dialog/edits/dialogEditStock.fxml"));
            Parent root = loader.load();

            //Pega o controller da tela que vai receber produ.
            DialogStockController dialogController = loader.getController();
            //Joga o produ nesse metodo dentro do controller pego.
            dialogController.initProdu(produ);

            Stage stage = new Stage();
            stage.setTitle("Infinity Tech - Editar produto");
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