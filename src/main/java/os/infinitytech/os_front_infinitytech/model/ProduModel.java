package os.infinitytech.os_front_infinitytech.model;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;
import java.math.BigDecimal;

public class ProduModel {

    @SerializedName("codigo")
    private String codigo;

    @SerializedName("nome")
    private String nome;

    @SerializedName("marca")
    private String marca;

    @SerializedName("status")
    private String status;

    @SerializedName("quantidade")
    private Long quantidade;

    @SerializedName("valorDeCompra")
    private BigDecimal valorCompra;

    @SerializedName("valorDeVenda")
    private BigDecimal valorVenda;

    // --- Métodos Property para a TableView ---

    public StringProperty codigoProperty() { return new SimpleStringProperty(codigo); }
    public StringProperty nomeProperty() { return new SimpleStringProperty(nome); }
    public StringProperty marcaProperty() { return new SimpleStringProperty(marca); }
    public StringProperty statusProperty() { return new SimpleStringProperty(status); }
    public StringProperty quantidadeProperty() {
        return new SimpleStringProperty(quantidade != null ? quantidade.toString() : "0");
    }
    public StringProperty valorCompraProperty() {
        return new SimpleStringProperty(valorCompra != null ? String.format("R$ %.2f", valorCompra) : "R$ 0,00");
    }
    public StringProperty valorVendaProperty() {
        return new SimpleStringProperty(valorVenda != null ? String.format("R$ %.2f", valorVenda) : "R$ 0,00");
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }
}