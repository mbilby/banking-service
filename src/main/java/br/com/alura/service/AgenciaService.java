package br.com.alura.service;

import br.com.alura.domain.Agencia;
import br.com.alura.domain.http.AgenciaHTTP;
import br.com.alura.domain.http.SituacaoCadastral;
import br.com.alura.exceptions.AgenciaInativaOuNaoEncontradaException;
import br.com.alura.repository.AgenciaRepository;
import br.com.alura.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
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

    public void cadastrar(Agencia agencia) {

        AgenciaHTTP agenciaHTTP = situacaoCadastralHttpService
                .buscarPorCnpj(agencia.getCnpj());

        if (agenciaHTTP != null && agenciaHTTP.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            Log.info(String.format("A agência com CNPJ: %s foi cadastrada!", agencia.getCnpj()));
            meterRegistry.counter("agencia_adicionada_count").increment();
            agenciaRepository.persist(agencia);
        } else {
            Log.info(String.format("A agência com CNPJ: %s não foi cadastrada!", agencia.getCnpj()));
            meterRegistry.counter("agencia_nao_adicionada_count").increment();
            throw new AgenciaInativaOuNaoEncontradaException();
        }
    }

    public List<Agencia> buscaAgencias() {
        List<Agencia> agencias = agenciaRepository.listAll();
        Log.info(String.format("Agencias retornadas: %s", agencias.size()));
        meterRegistry.gauge("agencias_total", agencias.size());
        return agencias;
    }

    public Agencia buscaAgenciaPorId(long id) {
        return agenciaRepository.findById(id);
    }

    public void deletarAgenciaPorId(long id) {
        Log.info(String.format("A agência com CNPJ: %d foi cadastrada!", id));
        agenciaRepository.deleteById(id);
    }

    public void AlterarAgencia(Agencia agencia) {
        Agencia agenciaAtual = agenciaRepository.findById(agencia.getId());

        if (agenciaAtual != null) {
            agenciaAtual.setNome(agencia.getNome());
            agenciaAtual.setRazaoSocial(agencia.getRazaoSocial());
            agenciaAtual.setCnpj(agencia.getCnpj());
        }
        else {
            throw new IllegalStateException("Agencia com ID " + agencia.getId() + " não encontrada!");
        }
    }
}
