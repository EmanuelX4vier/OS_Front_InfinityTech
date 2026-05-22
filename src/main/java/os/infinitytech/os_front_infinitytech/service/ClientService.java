package os.infinitytech.os_front_infinitytech.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import os.infinitytech.os_front_infinitytech.auth.ApiClient;
import os.infinitytech.os_front_infinitytech.model.ClientModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClientService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    // =========================
    // LISTAR CLIENTES
    // =========================
    public List<ClientModel> buscarClients() throws Exception {

        String json = apiClient.get("/clients");

        System.out.println("CLIENTES JSON:");
        System.out.println(json);

        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        JsonArray content = obj.getAsJsonArray("content");

        Type listType = new TypeToken<ArrayList<ClientModel>>() {}.getType();

        return gson.fromJson(content, listType);
    }

    // =========================
    // CRIAR CLIENTE
    // =========================
    public String criarClient(ClientModel cliente) throws Exception {

        String json = gson.toJson(cliente);

        return apiClient.post("/clients", json);
    }

    // =========================
    // BUSCAR CLIENTE POR ID
    // =========================
    public ClientModel buscarPorId(Long id) throws Exception {
        String json = apiClient.get("/clients/" + id);

        if (json == null || json.isBlank()) {
            return null;
        }

        return gson.fromJson(json, ClientModel.class);
    }
}