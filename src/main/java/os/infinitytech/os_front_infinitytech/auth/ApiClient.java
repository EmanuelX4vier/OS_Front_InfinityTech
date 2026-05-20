package os.infinitytech.os_front_infinitytech.auth;

import java.net.*;
import java.net.http.*;

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

    public String post(String endpoint, String jsonBody) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        String token = AuthStorage.getInstance().getAccessToken();

        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = client.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 401) {
            throw new RuntimeException("UNAUTHORIZED");
        }

        return response.body();
    }

    public String postWithoutAuth(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}