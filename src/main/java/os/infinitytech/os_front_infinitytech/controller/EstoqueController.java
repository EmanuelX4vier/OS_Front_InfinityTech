package os.infinitytech.os_front_infinitytech.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.config.Session;
import os.infinitytech.os_front_infinitytech.model.ProduModel;
import os.infinitytech.os_front_infinitytech.service.AuthService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EstoqueController {

    @FXML private TableView<ProduModel> tblEstoque;
    @FXML private TableColumn<ProduModel, String> colCodigo;
    @FXML private TableColumn<ProduModel, String> colNome;
    @FXML private TableColumn<ProduModel, String> colMarca;
    @FXML private TableColumn<ProduModel, String> colStatus;

    @FXML
    public void initialize() {
        // Vincula as colunas usando reflexão direta sobre os campos do Modelo
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        carregarDados();
    }

    private void carregarDados() {
        AuthService service = Session.getAuthService();

        if (service == null) {
            System.err.println("Erro: Sessão não encontrada.");
            return;
        }

        Task<List<ProduModel>> task = new Task<>() {
            @Override
            protected List<ProduModel> call() throws Exception {
                String json = service.buscarProdutos();

                com.google.gson.JsonObject jsonObject = com.google.gson.JsonParser.parseString(json).getAsJsonObject();

                if (jsonObject.has("content")) {
                    com.google.gson.JsonArray contentArray = jsonObject.getAsJsonArray("content");
                    Type listType = new TypeToken<ArrayList<ProduModel>>(){}.getType();
                    return new Gson().fromJson(contentArray, listType);
                }
                return new ArrayList<>();
            }
        };

        task.setOnSucceeded(e -> {
            List<ProduModel> produtos = task.getValue();
            Platform.runLater(() -> {
                tblEstoque.setItems(FXCollections.observableArrayList(produtos));

                // O TRUQUE FINAL: Força a tabela a se redesenhar completamente
                tblEstoque.refresh();
            });
        });

        task.setOnFailed(e -> {
            System.err.println("Falha ao carregar estoque:");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            String path = "/os_front_infinitytech/fxml/home.fxml";
            URL fxmlLocation = getClass().getResource(path);
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Infinity Tech - Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}