package br.com.alura.repository;

import br.com.alura.domain.error.AgenciaErro;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgenciaErroRepository implements PanacheRepository<AgenciaErro> {
}
