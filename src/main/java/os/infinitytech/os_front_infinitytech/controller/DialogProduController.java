package os.infinitytech.os_front_infinitytech.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.model.ProduModel;
import os.infinitytech.os_front_infinitytech.service.ProduService;
import os.infinitytech.os_front_infinitytech.types.Status;

import java.math.BigDecimal;

public class DialogProduController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtMarca;
    @FXML private TextField txtStatus;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtValorCompra;
    @FXML private TextField txtValorVenda;

    private final ProduService produService = new ProduService();

    @FXML
    private void handleSalvar() {

        try {
            // 🔥 CONVERSÕES
            Status statusEnum = Status.valueOf(txtStatus.getText().trim().toUpperCase());

            Long qtd = Long.parseLong(txtQuantidade.getText().trim());

            BigDecimal vCompra = new BigDecimal(
                    txtValorCompra.getText().trim().replace(",", ".")
            );

            BigDecimal vVenda = new BigDecimal(
                    txtValorVenda.getText().trim().replace(",", ".")
            );

            // 🔥 CRIA OBJETO
            ProduModel produto = new ProduModel();
            produto.setCodigo(txtCodigo.getText().trim());
            produto.setNome(txtNome.getText().trim());
            produto.setMarca(txtMarca.getText().trim());
            produto.setStatus(String.valueOf(statusEnum));
            produto.setQuantidade(qtd);
            produto.setValorCompra(vCompra);
            produto.setValorVenda(vVenda);

            // 🔥 CHAMA SERVICE (SEM HTTP AQUI)
            new Thread(() -> {
                try {
                    produService.criarProduto(produto);

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