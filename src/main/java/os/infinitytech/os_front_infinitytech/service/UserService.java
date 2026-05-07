package os.infinitytech.os_front_infinitytech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import os.infinitytech.os_front_infinitytech.dto.UserResponseDTO;
import os.infinitytech.os_front_infinitytech.config.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserService {

    private final String baseUrl;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public UserService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public UserResponseDTO findById(Long id) {

        try {
            String authHeader = Session.getAuthService().getAuthorizationHeader();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/users/" + id))
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            if (status == 200) {
                return mapper.readValue(response.body(), UserResponseDTO.class);
            }

            if (status == 404) {
                throw new RuntimeException("Usuário não encontrado");
            }

            if (status == 401) {
                throw new RuntimeException("Não autorizado. Faça login novamente.");
            }

            throw new RuntimeException("Erro inesperado: " + status);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage());
        }
    }
}