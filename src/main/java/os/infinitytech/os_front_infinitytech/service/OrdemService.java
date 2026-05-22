package os.infinitytech.os_front_infinitytech.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import os.infinitytech.os_front_infinitytech.auth.ApiClient;
import os.infinitytech.os_front_infinitytech.model.OrdemModel;
import os.infinitytech.os_front_infinitytech.model.ClientModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrdemService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    // =========================
    // LISTAR ORDENS
    // =========================
    public List<OrdemModel> buscarOrdens(ClientService clientService) throws Exception {

        String json = apiClient.get("/equips");

        System.out.println("ORDENS JSON:");
        System.out.println(json);

        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        JsonArray content = obj.getAsJsonArray("content");

        Type listType = new TypeToken<List<OrdemModel>>() {}.getType();
        List<OrdemModel> list = gson.fromJson(content, listType);

        // Preenche o nome de cada cliente buscando pelo ID correspondente na API
        if (list != null && clientService != null) {
            for (OrdemModel ordem : list) {
                if (ordem.getClientId() != null) {
                    try {
                        ClientModel cliente = clientService.buscarPorId(ordem.getClientId());
                        if (cliente != null && cliente.getNome() != null) {
                            ordem.setClientNome(cliente.getNome());
                        } else {
                            ordem.setClientNome("Não encontrado");
                        }
                    } catch (Exception e) {
                        System.err.println("Erro ao vincular nome do cliente à ordem: " + e.getMessage());
                        ordem.setClientNome("Erro ao carregar");
                    }
                }
            }
        }

        return list;
    }

    // =========================
    // CRIAR ORDEM
    // =========================
    public String criarOrdem(OrdemModel ordem) throws Exception {

        String json = gson.toJson(ordem);

        System.out.println("JSON ENVIADO:");
        System.out.println(json);

        return apiClient.post("/equips", json);
    }

    // =========================
    // BUSCAR POR SERIAL
    // =========================
    public OrdemModel buscarPorSerial(String serial) throws Exception {

        String json = apiClient.get("/equips/" + serial);

        return gson.fromJson(json, OrdemModel.class);
    }
}