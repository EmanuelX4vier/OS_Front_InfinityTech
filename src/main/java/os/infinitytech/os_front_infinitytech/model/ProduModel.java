package os.infinitytech.os_front_infinitytech.model;

import com.google.gson.annotations.SerializedName;

public class ProduModel {
    @SerializedName("codigo") private String codigo;
    @SerializedName("nome") private String nome;
    @SerializedName("marca") private String marca;
    @SerializedName("status") private String status;

    public ProduModel() {}

    // Getters simples (Essenciais para o PropertyValueFactory)
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getMarca() { return marca; }
    public String getStatus() { return status; }

    // Setters
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNome(String nome) { this.nome = nome; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setStatus(String status) { this.status = status; }
}