package br.com.alura.domain.error;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco_erro")
public class EnderecoErro {

    public EnderecoErro() {

    }

    public EnderecoErro(Integer id, String rua, String logradouro, String complemento, Integer numero) {
        this.id = id;
        this.rua = rua;
        this.logradouro = logradouro;
        this.complemento = complemento;
        this.numero = numero;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String rua;

    private String logradouro;

    private String complemento;

    private Integer numero;


    public Integer getId() {
        return id;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }
}
