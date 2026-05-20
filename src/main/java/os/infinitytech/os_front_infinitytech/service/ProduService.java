package os.infinitytech.os_front_infinitytech.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import os.infinitytech.os_front_infinitytech.auth.ApiClient;
import os.infinitytech.os_front_infinitytech.model.ProduModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProduService {

    private final ApiClient apiClient = new ApiClient();
    private final Gson gson = new Gson();

    // 🔥 LISTAR PRODUTOS
    public List<ProduModel> buscarProdutos() throws Exception {

        String json = apiClient.post("/produtos/listar", "{}");

        com.google.gson.JsonObject jsonObject =
                com.google.gson.JsonParser.parseString(json).getAsJsonObject();

        if (jsonObject.has("content")) {

            com.google.gson.JsonArray contentArray =
                    jsonObject.getAsJsonArray("content");

            Type listType = new TypeToken<ArrayList<ProduModel>>() {}.getType();

            return gson.fromJson(contentArray, listType);
        }

        return new ArrayList<>();
    }

    // 🔥 CRIAR PRODUTO (exemplo base)
    public String criarProduto(ProduModel produto) throws Exception {
        String json = gson.toJson(produto);
        return apiClient.post("/produtos", json);
    }

    // 🔥 ATUALIZAR PRODUTO
    public String atualizarProduto(ProduModel produto) throws Exception {
        String json = gson.toJson(produto);
        return apiClient.post("/produtos/atualizar", json);
    }

    // 🔥 DELETAR PRODUTO
    public String deletarProduto(Long id) throws Exception {
        return apiClient.post("/produtos/deletar", "{\"id\":" + id + "}");
    }
}