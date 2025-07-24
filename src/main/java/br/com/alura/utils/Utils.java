package br.com.alura.utils;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.http.AgenciaHTTP;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.exceptions.AgenciaInativaOuNaoEncontradaException;
import br.com.alura.repository.AgenciaRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

public class Utils {

    private final AgenciaRepository agenciaRepository;

    private final MeterRegistry meterRegistry;

    public Utils(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry) {
        this.agenciaRepository = agenciaRepository;
        this.meterRegistry = meterRegistry;
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
}
