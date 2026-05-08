package os.infinitytech.os_front_infinitytech.service;

import os.infinitytech.os_front_infinitytech.config.LoginResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthService {

    private final String baseUrl;
    private final HttpClient client;
    private String authorizationHeader; // Armazena o token da sessão

    public AuthService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
    }

    public LoginResult login(String username, String password) {
        try {
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            String header = "Basic " + encodedAuth;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/users/auth/check"))
                    .header("Authorization", header)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            if (status == 200) {
                this.authorizationHeader = header; // Guarda o cabeçalho para uso futuro
                return new LoginResult(true, "Login realizado com sucesso", status);
            } else if (status == 401) {
                return new LoginResult(false, "Usuário ou senha inválidos", status);
            } else {
                return new LoginResult(false, "Erro no servidor: " + status, status);
            }

        } catch (Exception e) {
            return new LoginResult(false, "Erro de conexão: " + e.getMessage(), -1);
        }
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public boolean isLogged() {
        return authorizationHeader != null;
    }

    public void logout() {
        this.authorizationHeader = null;
    }

    public String buscarProdutos() throws Exception {
        if (authorizationHeader == null) throw new IllegalStateException("Não autenticado");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/products?size=100")) // size=100 para trazer mais itens que o padrão 20
                .header("Authorization", authorizationHeader)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) return response.body();
        throw new Exception("Erro ao buscar dados: " + response.statusCode());
    }
}