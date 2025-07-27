package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.error.AgenciaErro;
import br.com.alura.domain.error.EnderecoErro;
import br.com.alura.domain.http.AgenciaHTTP;
import br.com.alura.repository.AgenciaErroRepository;
import br.com.alura.utils.Utils;
import br.com.alura.exceptions.AgenciaInativaOuNaoEncontradaException;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AgenciaService {

    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    private final AgenciaRepository agenciaRepository;

    private final MeterRegistry meterRegistry;

    private final AgenciaErroRepository agenciaErroRepository;

    AgenciaService(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry, AgenciaErroRepository agenciaErroRepository) {
        this.agenciaRepository = agenciaRepository;
        this.meterRegistry = meterRegistry;
        this.agenciaErroRepository = agenciaErroRepository;
    }

    @WithTransaction
    @CircuitBreaker(requestVolumeThreshold = 5, delay = 2000, successThreshold = 2)
    @Fallback(fallbackMethod = "chamarFallback")
    public Uni<Void> cadastrar(Agencia agencia) {

        Uni<AgenciaHTTP> agenciaHTTP = situacaoCadastralHttpService
                .buscarPorCnpj(agencia.getCnpj());

        Utils utils = new Utils(agenciaRepository, meterRegistry, agenciaErroRepository);

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
        Utils utils = new Utils(agenciaRepository, meterRegistry, agenciaErroRepository);

        return agenciaAtualUni
                .onItem()
                .ifNull()
                .failWith(new IllegalStateException("Agencia com ID " + agencia.getId() + " não encontrada!"))
                .onItem()
                .transformToUni(founded -> utils.updateSeAtivo(agencia));
    }

    public Uni<Void> chamarFallback(Agencia agencia, Throwable throwable) {
        Log.errorf("🚨 Fallback acionado para o CNPJ: %s. Erro: %s",
                agencia.getCnpj(),
                throwable != null ? throwable.getMessage() : "Erro desconhecido");

        EnderecoErro enderecoErro = new EnderecoErro();
        enderecoErro.setRua(agencia.getEndereco().getRua());
        enderecoErro.setLogradouro(agencia.getEndereco().getLogradouro());
        enderecoErro.setComplemento(agencia.getEndereco().getComplemento());
        enderecoErro.setNumero(agencia.getEndereco().getNumero());

        AgenciaErro erro = new AgenciaErro();
        erro.setNome(agencia.getNome());
        erro.setCnpj(agencia.getCnpj());
        erro.setRazaoSocial(agencia.getRazaoSocial());
        erro.setEndereco(enderecoErro);
        erro.setMotivoErro(throwable != null ? throwable.getMessage() : "Erro desconhecido");
        erro.setDataErro(LocalDateTime.now());
        Utils utils = new Utils(agenciaRepository, meterRegistry, agenciaErroRepository);

        return utils.persistirSeErro(erro);
    }

    public Uni<Void> reprocessaAgenciasPendetes() {

        //Uni<List<AgenciaErro>> agenciasErroUni = agenciaErroRepository.listAll();

        Utils utils = new Utils(agenciaRepository, meterRegistry, agenciaErroRepository);

        //Uni<List<Agencia>> agenciasParaProcessamentoUni = utils.transformaAgenciaErroEmAgencia(agenciasErroUni);

        return agenciaErroRepository.listAll()
                .onItem().transformToMulti(listaErros -> {
                    if (listaErros == null || listaErros.isEmpty()) {
                        Log.info("Nenhuma agência com erro encontrada para reprocessamento.");
                        return Multi.createFrom().empty();
                    }
                    Log.infof("Encontradas %d agências com erro para reprocessar.", listaErros.size());
                    return Multi.createFrom().iterable(listaErros);
                })
                .onItem().transformToUniAndMerge(agenciaErro -> {
                    Agencia agencia = utils.transformaAgenciaErroEmAgencia(agenciaErro);

                    return situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj())
                            .onItem().ifNotNull()
                            .call(agenciaHTTP ->
                                    utils.persistirSeAtivo(agencia, agenciaHTTP)
                                            .call(() -> agenciaErroRepository.deleteById(agenciaErro.getId())
                                                    .invoke(() -> Log.infof("Removido erro do CNPJ: %s", agenciaErro.getCnpj()))
                                            )
                            )
                            .onFailure().invoke(e ->
                                    Log.errorf("Falha ao reprocessar CNPJ %s: %s", agenciaErro.getCnpj(), e.getMessage())
                            )
                            .onFailure().recoverWithNull();
                })
                .collect().asList()
                .invoke(lista -> Log.infof("Processamento finalizado para %d agências.", lista.size()))
                .replaceWithVoid();
    }
}
