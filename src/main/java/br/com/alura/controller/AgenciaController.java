package br.com.alura.controller;

import br.com.alura.domain.Agencia;
import br.com.alura.service.AgenciaService;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/agencias")
public class AgenciaController {

    private final AgenciaService agenciaService;

    AgenciaController (AgenciaService agenciaService) {
        this.agenciaService = agenciaService;
    }

    @POST
    @NonBlocking
    @Transactional
    public Uni<RestResponse<Void>> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
        return this.agenciaService.cadastrar(agencia)
                .replaceWith(
                        RestResponse
                                .created(uriInfo.getAbsolutePath())
                );
    }

    @GET
    @Path("{id}")
    public Uni<RestResponse<Agencia>> buscarAgenciaPorId(long id) {
        return this.agenciaService
                .buscaAgenciaPorId(id)
                .onItem()
                .transform(RestResponse::ok);
    }

    @GET
    public Uni<RestResponse<List<Agencia>>> buscarAgencias() {
        return agenciaService
                .buscaAgencias()
                .onItem()
                .transform(RestResponse::ok);
    }

    @PUT
    @NonBlocking
    @Transactional
    public Uni<RestResponse<Void>> alterarAgencia(Agencia agencia) {
        return agenciaService.
                AlterarAgencia(agencia)
                .replaceWith(RestResponse::ok);
    }

    @DELETE
    @NonBlocking
    @Transactional
    @Path("{id}")
    public Uni<RestResponse<Void>> deletarAgenciaPorId(long id) {
        return agenciaService
                .deletarAgenciaPorId(id)
                .replaceWith(RestResponse::ok);
    }
}
