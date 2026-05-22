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

    // ========================================================
    // LISTAR CLIENTES COM PAGINAÇÃO
    // ========================================================
    public List<ClientModel> buscarClients(int pagina) throws Exception {
        String json = apiClient.get("/clients?page=" + pagina + "&size=20");

        System.out.println("CLIENTES JSON (PÁGINA " + pagina + "):");
        System.out.println(json);

        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }

        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            if (!obj.has("content") || obj.get("content").isJsonNull()) {
                return new ArrayList<>();
            }

            JsonArray content = obj.getAsJsonArray("content");
            Type listType = new TypeToken<ArrayList<ClientModel>>() {}.getType();

            List<ClientModel> resultado = gson.fromJson(content, listType);
            return resultado != null ? resultado : new ArrayList<>();

        } catch (Exception e) {
            System.err.println("Erro ao desserializar a página de clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public ClientModel buscarPorId(Long id) throws Exception {
        String json = apiClient.get("/clients/" + id);

        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            JsonObject clientJson = JsonParser.parseString(json).getAsJsonObject();
            return gson.fromJson(clientJson, ClientModel.class);
        } catch (Exception e) {
            System.err.println("Erro ao buscar cliente por ID: " + e.getMessage());
            return null;
        }
    }

    public String criarClient(ClientModel cliente) throws Exception {
        String json = gson.toJson(cliente);
        return apiClient.post("/clients", json);
    }

    public String atualizarClient(Long id, ClientModel cliente) throws Exception {
        String json = gson.toJson(cliente);
        return apiClient.post("/clients/" + id, json);
    }
}