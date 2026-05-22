package os.infinitytech.os_front_infinitytech.auth;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient client;

    public ApiClient() {

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    // =========================
    // POST (com auth)
    // =========================

    public String post(String endpoint, String jsonBody) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        addAuthHeader(builder);

        HttpRequest request = builder.build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        log("POST", response);

        validateAuth(response);

        return response.body();
    }

    // =========================
    // POST sem auth
    // =========================

    public String postWithoutAuth(String endpoint, String jsonBody) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        log("POST (NO AUTH)", response);

        return response.body();
    }

    // =========================
    // GET (com auth)
    // =========================

    public String get(String endpoint) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

        addAuthHeader(builder);

        HttpRequest request = builder.GET().build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        log("GET", response);

        validateAuth(response);

        return response.body();
    }

    // =========================
    // DELETE
    // =========================

    public String delete(String endpoint) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

        addAuthHeader(builder);

        HttpRequest request = builder.DELETE().build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        log("DELETE", response);

        validateAuth(response);

        return response.body();
    }

    // =========================
    // AUTH HEADER
    // =========================

    private void addAuthHeader(HttpRequest.Builder builder) {

        String token = AuthStorage.getInstance().getAccessToken();

        if (token != null && !token.isBlank()) {

            builder.header("Authorization", "Bearer " + token);
        }

        System.out.println("TOKEN ENVIADO: " + token);
    }

    // =========================
    // LOG
    // =========================

    private void log(String method, HttpResponse<String> response) {

        System.out.println(method + " STATUS: " + response.statusCode());
        System.out.println(method + " BODY: " + response.body());
    }

    // =========================
    // AUTH CHECK
    // =========================

    private void validateAuth(HttpResponse<String> response) {

        if (response.statusCode() == 401 ||
                response.statusCode() == 403) {

            throw new RuntimeException("UNAUTHORIZED");
        }
    }
}