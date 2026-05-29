package os.infinitytech.os_front_infinitytech.controller.dialog;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.model.ProduModel;
import os.infinitytech.os_front_infinitytech.service.StockService;
import os.infinitytech.os_front_infinitytech.types.Status;
import java.math.BigDecimal;

public class DialogStockController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtMarca;
    @FXML private ComboBox<Status> cbStatus;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtValorCompra;
    @FXML private TextField txtValorVenda;

    private final StockService stockService = new StockService();
    private ProduModel produtoRecebido;

    //Inicializa as opções de Status.
    @FXML
    public void initialize() {
        cbStatus.getItems().addAll(Status.DISPONIVEL, Status.INDISPONIVEL);
    }

    //Produ recebido pelo StockController.
    public void initProdu(ProduModel produ){
        this.produtoRecebido = produ;

        txtCodigo.setText(produ.getCodigo());
        txtCodigo.setDisable(true);

        txtNome.setText(produ.getNome());
        txtMarca.setText(produ.getMarca());
        txtQuantidade.setText(String.valueOf(produ.getQuantidade()));
        txtValorCompra.setText(String.valueOf(produ.getValorCompra()));
        txtValorVenda.setText(String.valueOf(produ.getValorVenda()));

        // Pré-seleciona o Status correto vindo do objeto
        if (produ.getStatus() != null) {
            try {
                Status statusEnum = Status.valueOf(produ.getStatus().toUpperCase());
                cbStatus.setValue(statusEnum);
            } catch (IllegalArgumentException e) {
                System.err.println("Status inválido retornado pela API: " + produ.getStatus());
            }
        }
    }

    @FXML
    private void handleSalvar() {
        try {
            if (cbStatus.getValue() == null) {
                System.err.println("Por favor, selecione um status!");
                return;
            }

            Status statusEnum = cbStatus.getValue();
            Long qtd = Long.parseLong(txtQuantidade.getText().trim());
            BigDecimal vCompra = new BigDecimal(txtValorCompra.getText().trim().replace(",", "."));
            BigDecimal vVenda = new BigDecimal(txtValorVenda.getText().trim().replace(",", "."));

            ProduModel produto = new ProduModel();
            produto.setCodigo(txtCodigo.getText().trim());
            produto.setNome(txtNome.getText().trim());
            produto.setMarca(txtMarca.getText().trim());
            produto.setStatus(String.valueOf(statusEnum));
            produto.setQuantidade(qtd);
            produto.setValorCompra(vCompra);
            produto.setValorVenda(vVenda);

            new Thread(() -> {
                try {
                    stockService.criarProduto(produto);
                    Platform.runLater(this::fecharJanela);
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
    private void handleEditar(){
        try {
            if (this.produtoRecebido == null) {
                System.err.println("Nenhum produto base encontrado para edição.");
                return;
            }

            ProduModel produto = this.produtoRecebido;

            if(!txtNome.getText().trim().isEmpty()){
                produto.setNome(txtNome.getText().trim());
            }

            if(!txtMarca.getText().trim().isEmpty()){
                produto.setMarca(txtMarca.getText().trim());
            }

            if(cbStatus.getValue() != null){
                produto.setStatus(String.valueOf(cbStatus.getValue()));
            }

            if(!txtQuantidade.getText().trim().isEmpty()){
                Long qtd = Long.parseLong(txtQuantidade.getText().trim());
                produto.setQuantidade(qtd);
            }

            if(!txtValorCompra.getText().trim().isEmpty()){
                BigDecimal vCompra = new BigDecimal(txtValorCompra.getText().trim().replace(",", "."));
                produto.setValorCompra(vCompra);
            }

            if(!txtValorVenda.getText().trim().isEmpty()){
                BigDecimal vVenda = new BigDecimal(txtValorVenda.getText().trim().replace(",", "."));
                produto.setValorVenda(vVenda);
            }

            new Thread(() -> {
                try {
                    stockService.atualizarProduto(this.produtoRecebido.getCodigo(), produto);
                    Platform.runLater(this::fecharJanela);
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
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
}