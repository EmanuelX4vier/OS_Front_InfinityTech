package os.infinitytech.os_front_infinitytech.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import os.infinitytech.os_front_infinitytech.auth.ApiClient;
import os.infinitytech.os_front_infinitytech.model.ProduModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProduService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    // =========================
    // LISTAR PRODUTOS
    // =========================
    public List<ProduModel> buscarProdutos() throws Exception {

        String json = apiClient.get("/products");

        System.out.println("JSON RECEBIDO:");
        System.out.println(json);

        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }

        JsonElement element = JsonParser.parseString(json);

        if (!element.isJsonObject()) {
            System.out.println("Resposta não é JSON Object");
            return new ArrayList<>();
        }

        JsonObject jsonObject = element.getAsJsonObject();

        if (!jsonObject.has("content")) {
            System.out.println("Campo 'content' não encontrado");
            return new ArrayList<>();
        }

        JsonArray contentArray = jsonObject.getAsJsonArray("content");

        Type listType = new TypeToken<ArrayList<ProduModel>>() {}.getType();

        return gson.fromJson(contentArray, listType);
    }

    // =========================
    // CRIAR PRODUTO
    // =========================
    public String criarProduto(ProduModel produto) throws Exception {

        String json = gson.toJson(produto);

        return apiClient.post("/products", json);
    }

    // =========================
    // ATUALIZAR PRODUTO
    // =========================
    public String atualizarProduto(String codigo, ProduModel produto) throws Exception {

        String json = gson.toJson(produto);

        return apiClient.post("/products/" + codigo, json);
    }

    // =========================
    // DELETAR PRODUTO
    // =========================
    public String deletarProduto(String codigo) throws Exception {

        return apiClient.post("/products/" + codigo, "");
    }
}