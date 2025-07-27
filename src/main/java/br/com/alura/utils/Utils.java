package br.com.alura.utils;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.Endereco;
import br.com.alura.domain.error.AgenciaErro;
import br.com.alura.domain.http.AgenciaHTTP;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.exceptions.AgenciaInativaOuNaoEncontradaException;
import br.com.alura.repository.AgenciaErroRepository;
import br.com.alura.repository.AgenciaRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

public class Utils {

    private final AgenciaRepository agenciaRepository;

    private final MeterRegistry meterRegistry;

    private final AgenciaErroRepository agenciaErroRepository;

    public Utils(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry, AgenciaErroRepository agenciaErroRepository) {
        this.agenciaRepository = agenciaRepository;
        this.meterRegistry = meterRegistry;
        this.agenciaErroRepository = agenciaErroRepository;
    }

    public Uni<Void> persistirSeAtivo(Agencia agencia, AgenciaHTTP agenciaHTTP) {

        if (agenciaHTTP != null && agenciaHTTP.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            Log.info(String.format("A agência com CNPJ: %s foi cadastrada!", agencia.getCnpj()));
            meterRegistry.counter("agencia_adicionada_count").increment();
            return agenciaRepository.persist(agencia).replaceWithVoid();
        } else {
            Log.info(String.format("A agência com CNPJ: %s não foi cadastrada!", agencia.getCnpj()));
            meterRegistry.counter("agencia_nao_adicionada_count").increment();
            return Uni.createFrom().failure(new AgenciaInativaOuNaoEncontradaException());
        }
    }

    public Uni<Void> updateSeAtivo(Agencia agencia) {

        Log.info(String.format("A agência com CNPJ: %s foi Atualizada!", agencia.getCnpj()));
        meterRegistry.counter("agencia_atualizada_count").increment();
        return agenciaRepository.update(
                        "nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4",
                        agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId())
                .replaceWithVoid();

    }

    public Uni<Void> persistirSeErro(AgenciaErro agenciaErro) {

        Log.info(String.format("Agência de CNPJ: %s com erro!", agenciaErro.getCnpj()));
        meterRegistry.counter("falha_cadastro_agencia").increment();
        return agenciaErroRepository.persist(agenciaErro)
                .replaceWithVoid();

    }


    public Agencia transformaAgenciaErroEmAgencia(AgenciaErro agenciaErro) {
        Agencia agencia = new Agencia();

        Endereco endereco = new Endereco();
        endereco.setRua(agenciaErro.getEndereco().getRua());
        endereco.setComplemento(agenciaErro.getEndereco().getComplemento());
        endereco.setLogradouro(agenciaErro.getEndereco().getComplemento());
        endereco.setNumero(agenciaErro.getEndereco().getNumero());

        agencia.setNome(agenciaErro.getNome());
        agencia.setCnpj(agenciaErro.getCnpj());
        agencia.setRazaoSocial(agenciaErro.getRazaoSocial());
        agencia.setEndereco(endereco);

        return agencia;
    }
}


