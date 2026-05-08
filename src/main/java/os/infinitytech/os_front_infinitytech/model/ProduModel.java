package os.infinitytech.os_front_infinitytech.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProduModel {

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("nome")
    private String nome;

    @SerializedName("marca")
    private String marca;

    @SerializedName("status")
    private String status;

    @SerializedName("quantidade") // Nome exato que vem do JSON do back-end
    private String quantidade;

    public ProduModel() {}

    // Getters e Properties para o JavaFX
    public String getCodigo() { return codigo; }
    public StringProperty codigoProperty() { return new SimpleStringProperty(codigo); }

    public String getNome() { return nome; }
    public StringProperty nomeProperty() { return new SimpleStringProperty(nome); }

    public String getMarca() { return marca; }
    public StringProperty marcaProperty() { return new SimpleStringProperty(marca); }

    public String getStatus() { return status; }
    public StringProperty statusProperty() { return new SimpleStringProperty(status); }

    public String getQuantidade() { return quantidade; }
    public StringProperty quantidadeProperty() { return new SimpleStringProperty(quantidade); }
}