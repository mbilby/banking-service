package br.com.alura.utils;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.Endereco;
import br.com.alura.domain.http.AgenciaHTTP;
import io.smallrye.mutiny.Uni;

public class AgenciaFixture {

    public static Uni<AgenciaHTTP> criaAgenciaHTTP(String status) {
        return Uni.createFrom().item(new AgenciaHTTP( "Agencia Teste", "Razao social da Agencia Teste", "123", status));
    }

    public static Agencia criarAgencia() {
        Endereco endereco = new Endereco(1, "Rua de teste", "Logradouro de teste", "Complemento de teste", 1);
        return new Agencia(1L, "Agencia Teste", "Razao social da Agencia Teste", "123", endereco);
    }
}
