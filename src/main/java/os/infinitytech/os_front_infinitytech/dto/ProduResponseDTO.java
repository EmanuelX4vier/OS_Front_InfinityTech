package os.infinitytech.os_front_infinitytech.dto;

import os.infinitytech.os_front_infinitytech.types.Status;
import java.math.BigDecimal;

public class ProduResponseDTO {

    private String codigo;
    private String nome;
    private String marca;
    private Status status;
    private Long quantidade;
    private BigDecimal valorCompra;
    private BigDecimal valorVenda;

    // Getters e Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Long getQuantidade() { return quantidade; }
    public void setQuantidade(Long quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorCompra() { return valorCompra; }
    public void setValorCompra(BigDecimal valorCompra) { this.valorCompra = valorCompra; }

    public BigDecimal getValorVenda() { return valorVenda; }
    public void setValorVenda(BigDecimal valorVenda) { this.valorVenda = valorVenda; }
}