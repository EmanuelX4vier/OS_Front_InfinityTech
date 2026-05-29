package os.infinitytech.os_front_infinitytech.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import os.infinitytech.os_front_infinitytech.auth.ApiClient;
import os.infinitytech.os_front_infinitytech.model.ServiceModel;
import os.infinitytech.os_front_infinitytech.model.ClientModel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ServiceService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    // LISTAR ORDENS COM PAGINAÇÃO

    public List<ServiceModel> buscarOrdens(ClientService clientService, int pagina) throws Exception {
        String json = apiClient.get("/equips?page=" + pagina + "&size=20");

        System.out.println("ORDENS JSON (PÁGINA " + pagina + "):");
        System.out.println(json);

        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }

        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        if (!obj.has("content") || obj.get("content").isJsonNull()) {
            return new ArrayList<>();
        }

        JsonArray content = obj.getAsJsonArray("content");

        Type listType = new TypeToken<List<ServiceModel>>() {}.getType();
        List<ServiceModel> list = gson.fromJson(content, listType);

        if (list != null && clientService != null) {
            for (ServiceModel ordem : list) {
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

    public String criarOrdem(ServiceModel ordem) throws Exception {
        String json = gson.toJson(ordem);
        System.out.println("JSON ENVIADO:");
        System.out.println(json);
        return apiClient.post("/equips", json);
    }

    public ServiceModel buscarPorSerial(String serial) throws Exception {
        String json = apiClient.get("/equips/" + serial);
        return gson.fromJson(json, ServiceModel.class);
    }
}