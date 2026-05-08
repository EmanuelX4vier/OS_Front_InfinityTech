package os.infinitytech.os_front_infinitytech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import os.infinitytech.os_front_infinitytech.config.AppConfig;
import os.infinitytech.os_front_infinitytech.config.Session;
import os.infinitytech.os_front_infinitytech.dto.ProduResponseDTO;
import os.infinitytech.os_front_infinitytech.types.Status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DialogProduController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtMarca;
    @FXML private TextField txtStatus;
    @FXML private TextField txtQuantidade;

    @FXML
    private void handleSalvar() {
        try {
            // Conversões iniciais
            Status statusEnum = Status.valueOf(txtStatus.getText().trim().toUpperCase());
            Long qtdLong = Long.parseLong(txtQuantidade.getText().trim());

            // 1. Criar e popular o objeto DTO
            ProduResponseDTO produto = new ProduResponseDTO();
            produto.setCodigo(txtCodigo.getText().trim());
            produto.setNome(txtNome.getText().trim());
            produto.setMarca(txtMarca.getText().trim());
            produto.setStatus(statusEnum);
            produto.setQuantidade(qtdLong);

            // 2. Converter para JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(produto);

            // 3. Recuperar o cabeçalho de autorização da Sessão
            if (Session.getAuthService() == null || !Session.isLogged()) {
                System.err.println("Usuário não autenticado!");
                return;
            }
            String authHeader = Session.getAuthService().getAuthorizationHeader();

            // 4. Montar e enviar a requisição
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.API_BASE_URL + "/products"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 201 || response.statusCode() == 200) {
                            System.out.println("Produto salvo com sucesso!");
                            // Fecha a janela apenas em caso de sucesso
                            Platform.runLater(this::fecharJanela);
                        } else {
                            System.err.println("Erro ao salvar no servidor: " + response.statusCode());
                            System.err.println("Corpo da resposta: " + response.body());
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("Falha na comunicação com o servidor: " + ex.getMessage());
                        return null;
                    });

        } catch (IllegalArgumentException e) {
            System.err.println("Erro: Status inválido ou quantidade com formato incorreto.");
        } catch (Exception e) {
            System.err.println("Erro inesperado:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        // Verifica se o componente existe antes de pegar a Scene para evitar NullPointerException
        if (txtNome != null && txtNome.getScene() != null) {
            Stage stage = (Stage) txtNome.getScene().getWindow();
            stage.close();
        }
    }
}