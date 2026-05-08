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
            // 1. Recupera o AuthService da sessão global
            AuthService auth = Session.getAuthService();

            // 2. Verifica se o serviço existe e se está logado
            if (auth == null || auth.getAuthorizationHeader() == null) {
                throw new RuntimeException("Sessão inválida. Por favor, faça login novamente.");
            }

            // 3. Obtém o cabeçalho (Basic Auth) que foi gerado no Login
            String authHeader = auth.getAuthorizationHeader();

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
            } else if (status == 404) {
                throw new RuntimeException("Usuário não encontrado");
            } else if (status == 401) {
                throw new RuntimeException("Não autorizado. Credenciais expiradas.");
            }

            throw new RuntimeException("Erro inesperado do servidor: " + status);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage());
        }
    }
}