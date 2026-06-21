package os.infinitytech.os_front_infinitytech.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import os.infinitytech.os_front_infinitytech.auth.ApiClient;
import os.infinitytech.os_front_infinitytech.model.ProduModel;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StockService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    // ========================================================
    // LISTAR PRODUTOS DO ESTOQUE COM PAGINAÇÃO
    // ========================================================
    public List<ProduModel> buscarProdutos(int pagina) throws Exception {
        String json = apiClient.get("/products?page=" + pagina + "&size=20");

        System.out.println("ESTOQUE JSON (PÁGINA " + pagina + "):");
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
            Type listType = new TypeToken<ArrayList<ProduModel>>() {}.getType();

            List<ProduModel> resultado = gson.fromJson(content, listType);
            return resultado != null ? resultado : new ArrayList<>();

        } catch (Exception e) {
            System.err.println("Erro ao processar JSON do estoque: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<ProduModel> pesquisarProdutos(String termo, int pagina) throws Exception {
        String json = apiClient.get("/products/searchfor?termo=" + termo + "&page=" + pagina + "&size=20");

        System.out.println("ESTOQUE JSON (PÁGINA " + pagina + "):");
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
            Type listType = new TypeToken<ArrayList<ProduModel>>() {}.getType();

            List<ProduModel> resultado = gson.fromJson(content, listType);
            return resultado != null ? resultado : new ArrayList<>();

        } catch (Exception e) {
            System.err.println("Erro ao processar JSON do estoque: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String criarProduto(ProduModel produto) throws Exception {
        String json = gson.toJson(produto);
        return apiClient.post("/products", json);
    }

    public String atualizarProduto(String codigo, ProduModel produto) throws Exception {
        String json = gson.toJson(produto);
        return apiClient.patch("/products/" + codigo, json);
    }

    public void deletarProduto(String codigo) throws Exception {
        apiClient.delete("/products/" + codigo);
    }
}