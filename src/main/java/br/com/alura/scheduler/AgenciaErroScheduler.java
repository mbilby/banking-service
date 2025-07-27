package br.com.alura.scheduler;

import br.com.alura.service.AgenciaService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AgenciaErroScheduler {

    @Inject
    AgenciaService agenciaService;

    @WithTransaction
    @Scheduled(every = "2m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public Uni<Void> executarReprocessamento(ScheduledExecution execution) {

        Log.info("Iniciando reprocessamento de agências com erro...");

        return agenciaService.reprocessaAgenciasPendetes()
                .invoke(() -> Log.info("Reprocessamento concluído."))
                .onFailure()
                .invoke(failure -> Log.error("Falha no reprocessamento", failure))
                .replaceWithVoid();
    }
}
