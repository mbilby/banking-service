package br.com.alura.domain.error;

import br.com.alura.domain.Endereco;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agencia_erro")
public class AgenciaErro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;

    @Column(name = "cnpj", nullable = false, length = 20)
    private String cnpj;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private EnderecoErro endereco;

    @Column(name = "motivo_erro", nullable = false, length = 1000)
    private String motivoErro;

    @Column(name = "data_erro", nullable = false)
    private LocalDateTime dataErro;

    public AgenciaErro() {

    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public EnderecoErro getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoErro endereco) {
        this.endereco = endereco;
    }

    public String getMotivoErro() {
        return motivoErro;
    }

    public void setMotivoErro(String motivoErro) {
        this.motivoErro = motivoErro;
    }

    public LocalDateTime getDataErro() {
        return dataErro;
    }

    public void setDataErro(LocalDateTime dataErro) {
        this.dataErro = dataErro;
    }
}
