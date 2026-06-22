package os.infinitytech.os_front_infinitytech.controller.initial;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class HomeController {

    @FXML
    private void handleEstoque(ActionEvent event) {
        // O caminho deve ser relativo ao local onde o FXMLLoader procura os recursos
        carregarTela("/os_front_infinitytech/fxml/stock.fxml", "Infinity Tech - Gerenciamento de Estoque", event);
    }

    @FXML
    private void handleClientes(ActionEvent event){
        carregarTela("/os_front_infinitytech/fxml/client.fxml", "Infinity Tech - Gerenciamento de Clientes", event);
    }

    @FXML
    private void handleServicos(ActionEvent event) {
        carregarTela("/os_front_infinitytech/fxml/service.fxml", "Infinity Tech - Gerenciamento de Ordens", event);
    }

    private void carregarTela(String fxmlPath, String titulo, ActionEvent event) {
        try {
            URL fxmlLocation = getClass().getResource(fxmlPath);

            if (fxmlLocation == null) {
                throw new IOException("Arquivo FXML não encontrado em: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            // Obtém o Stage atual a partir do botão que disparou o evento
            Button btnSource = (Button) event.getSource();
            Stage stage = (Stage) btnSource.getScene().getWindow();

            // Troca apenas o conteúdo da cena atual
            stage.getScene().setRoot(root);

            // Atualiza o título da janela
            stage.setTitle(titulo);

        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta(
                    "Erro de Navegação",
                    "Não foi possível carregar a tela: " + e.getMessage()
            );
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}