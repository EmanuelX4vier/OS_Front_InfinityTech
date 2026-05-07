package os.infinitytech.os_front_infinitytech.dto;

import os.infinitytech.os_front_infinitytech.types.Functions;

public class UserResponseDTO {

    private Long id;
    private String nome;
    private Functions funcao;
    private String dataCadastro;

    public UserResponseDTO() {}

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

    public Functions getFuncao() {
        return funcao;
    }

    public void setFuncao(Functions funcao) {
        this.funcao = funcao;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}