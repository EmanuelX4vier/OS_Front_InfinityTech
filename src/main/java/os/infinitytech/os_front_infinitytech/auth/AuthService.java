package os.infinitytech.os_front_infinitytech.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final ApiClient apiClient = new ApiClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // 🔐 LOGIN
    public void login(String email, String password) throws Exception {

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("senha", password);

        String json = mapper.writeValueAsString(body);

        String response = apiClient.postWithoutAuth("/auth/login", json);

        System.out.println("LOGIN RESPONSE: " + response);

        Map<String, Object> obj = mapper.readValue(response, Map.class);

        Object tokenObj = obj.get("accessToken");

        if (tokenObj == null) {
            throw new RuntimeException("Token não recebido do backend!");
        }

        String accessToken = tokenObj.toString();

        AuthStorage.getInstance().saveAccessToken(accessToken);

        System.out.println("TOKEN SALVO: " + accessToken);
    }
    // 🆕 REGISTER
    public void register(String name, String email, String password) throws Exception {

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);

        String json = mapper.writeValueAsString(body);

        apiClient.postWithoutAuth("/auth/register", json);
    }

    // 🔥 REFRESH (cookie automático)
    public void refreshToken() throws Exception {

        String response = apiClient.postWithoutAuth("/auth/refresh", "{}");

        Map<String, Object> obj = mapper.readValue(response, Map.class);

        String newAccessToken = (String) obj.get("accessToken");

        AuthStorage.getInstance().saveAccessToken(newAccessToken);
    }

    // 🚪 LOGOUT (AQUI O QUE FALTAVA)
    public void logout() throws Exception {

        try {
            apiClient.post("/auth/logout", "{}");
        } finally {
            // sempre limpa local mesmo se backend falhar
            AuthStorage.getInstance().clear();
        }
    }
}