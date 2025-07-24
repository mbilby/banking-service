package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.http.AgenciaHTTP;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.utils.Utils;
import br.com.alura.exceptions.AgenciaInativaOuNaoEncontradaException;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    private final AgenciaRepository agenciaRepository;

    private final MeterRegistry meterRegistry;

    AgenciaService(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry) {
        this.agenciaRepository = agenciaRepository;
        this.meterRegistry = meterRegistry;
    }

    @WithTransaction
    public Uni<Void> cadastrar(Agencia agencia) {

        Uni<AgenciaHTTP> agenciaHTTP = situacaoCadastralHttpService
                .buscarPorCnpj(agencia.getCnpj());

        Utils utils = new Utils(agenciaRepository, meterRegistry);

        return agenciaHTTP
                .onItem()
                .ifNull()
                .failWith(new AgenciaInativaOuNaoEncontradaException())
                .onItem()
                .transformToUni(item -> utils.persistirSeAtivo(agencia, item));

    }
    @WithSession
    public Uni<List<Agencia>> buscaAgencias() {

        Uni<List<Agencia>> agencias = agenciaRepository.listAll();

        return agencias
                .onItem()
                .ifNull()
                .failWith(new IllegalStateException())
                .onItem()
                .invoke(list -> {
                    Log.info(String.format("Agencias retornadas: %s", list.size()));
                    meterRegistry.gauge("agencias_total", list.size());
                });

    }

    @WithSession
    public Uni<Agencia> buscaAgenciaPorId(long id) {

        return agenciaRepository.findById(id);
    }

    @WithTransaction
    public Uni<Void> deletarAgenciaPorId(long id) {
        Log.info(String.format("A agência com CNPJ: %d foi cadastrada!", id));
        return agenciaRepository
                .deleteById(id)
                .replaceWithVoid();
    }

    @WithTransaction
    public Uni<Void> AlterarAgencia(Agencia agencia) {
        Uni<Agencia> agenciaAtualUni = agenciaRepository.findById(agencia.getId());
        Utils utils = new Utils(agenciaRepository, meterRegistry);

        return agenciaAtualUni
                .onItem()
                .ifNull()
                .failWith(new IllegalStateException("Agencia com ID " + agencia.getId() + " não encontrada!"))
                .onItem()
                .transformToUni(founded -> {
                    return utils.updateSeAtivo(agencia);
                });
    }
}
