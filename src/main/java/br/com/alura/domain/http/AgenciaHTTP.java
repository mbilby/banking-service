package br.com.alura.domain.http;

public class AgenciaHTTP {

    private final String nome;
    private final String razaoSocial;
    private final String cnpj;
    private final SituacaoCadastral situacaoCadastral;


    public AgenciaHTTP(String nome, String razaoSocial, String cnpj, String situacaoCadastral) {
        this.nome = nome;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.situacaoCadastral = SituacaoCadastral.valueOf(situacaoCadastral);
    }

    public String getNome() {
        return nome;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public SituacaoCadastral getSituacaoCadastral() {
        return situacaoCadastral;
    }
}
