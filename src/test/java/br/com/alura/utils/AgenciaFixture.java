package br.com.alura.utils;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.Endereco;
import br.com.alura.domain.http.AgenciaHTTP;

public class AgenciaFixture {

    public static AgenciaHTTP criaAgenciaHTTP(String status) {
        return new AgenciaHTTP("Agencia Teste", "Razao social da Agencia Teste", "123", status);
    }

    public static Agencia criarAgencia() {
        Endereco endereco = new Endereco(1, "Rua de teste", "Logradouro de teste", "Complemento de teste", 1);
        Long id = 1L;
        return new Agencia(id, "Agencia Teste", "Razao social da Agencia Teste", "123", endereco);
    }
}
