package os.infinitytech.os_front_infinitytech.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClientModel {

    @SerializedName("id")
    private Long id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("telefone")
    private String telefone;

    @SerializedName("endereco")
    private String endereco;

    @SerializedName("dataCadastro")
    private String dataCadastro;

    // =========================
    // PROPERTIES (TABLEVIEW)
    // =========================

    public StringProperty idProperty() {
        return new SimpleStringProperty(id != null ? id.toString() : "");
    }

    public StringProperty nomeProperty() {
        return new SimpleStringProperty(nome != null ? nome : "");
    }

    public StringProperty telefoneProperty() {
        return new SimpleStringProperty(telefone != null ? telefone : "");
    }

    public StringProperty enderecoProperty() {
        return new SimpleStringProperty(endereco != null ? endereco : "");
    }

    public StringProperty dataCadastroProperty() {
        return new SimpleStringProperty(dataCadastro != null ? dataCadastro : "");
    }

    // =========================
    // GETTERS / SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}