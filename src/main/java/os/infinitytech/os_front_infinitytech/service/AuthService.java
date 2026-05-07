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

    private String authorizationHeader;

    public AuthService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
    }

    public LoginResult login(String username, String password) {

        try {
            String auth = username + ":" + password;

            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            String authorizationHeader = "Basic " + encodedAuth;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/users/auth/check"))
                    .header("Authorization", authorizationHeader)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            System.out.println("STATUS LOGIN: " + response.statusCode());
            System.out.println("BODY: " + response.body());

            if (status == 200) {
                this.authorizationHeader = authorizationHeader;
                return new LoginResult(true, "Login realizado com sucesso", status);
            } else if (status == 401) {
                return new LoginResult(false, "Usuário ou senha inválidos", status);
            } else if (status == 403) {
                return new LoginResult(false, "Acesso negado", status);
            } else {
                return new LoginResult(false, "Erro inesperado no servidor", status);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "Erro de conexão com o servidor", -1);
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
}