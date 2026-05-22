package os.infinitytech.os_front_infinitytech.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrdemModel {

    @SerializedName("clientId")
    private Long clientId;

    @SerializedName("serial")
    private String serial;

    @SerializedName("descricao")
    private String descricao;

    @SerializedName("status")
    private String status;

    @SerializedName("dataCadastro")
    private String dataCadastro;

    // UI only
    private String clientNome;

    // =========================
    // GETTERS / SETTERS
    // =========================

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getClientNome() {
        return clientNome;
    }

    public void setClientNome(String clientNome) {
        this.clientNome = clientNome;
    }

    // =========================
    // PROPERTIES (TABLEVIEW)
    // =========================

    public StringProperty clientIdProperty() {
        return new SimpleStringProperty(
                clientId != null ? clientId.toString() : ""
        );
    }

    public StringProperty serialProperty() {
        return new SimpleStringProperty(serial);
    }

    public StringProperty descricaoProperty() {
        return new SimpleStringProperty(descricao);
    }

    public StringProperty statusProperty() {
        return new SimpleStringProperty(status);
    }

    public StringProperty clientNomeProperty() {
        return new SimpleStringProperty(clientNome);
    }

    public StringProperty dataCadastroProperty() {
        return new SimpleStringProperty(
                dataCadastro != null ? dataCadastro : ""
        );
    }
}