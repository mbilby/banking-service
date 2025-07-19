package br.com.alura.controller;

import br.com.alura.domain.Agencia;
import br.com.alura.service.AgenciaService;
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
    @Transactional
    public RestResponse<Void> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
        this.agenciaService.cadastrar(agencia);
        return RestResponse.created(uriInfo.getAbsolutePath());
    }

    @GET
    @Path("{id}")
    public RestResponse<Agencia> buscarAgenciaPorId(long id) {
        Agencia agencia = this.agenciaService.buscaAgenciaPorId(id);
        return RestResponse.ok(agencia);
    }

    @GET
    public RestResponse<List<Agencia>> buscarAgencias() {
        List<Agencia> agencias = agenciaService.buscaAgencias();
        return RestResponse.ok(agencias);
    }

    @PUT
    @Transactional
    public RestResponse<Void> alterarAgencia(Agencia agencia) {
        this.agenciaService.AlterarAgencia(agencia);
        return RestResponse.ok();
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public RestResponse<Void> deletarAgenciaPorId(long id) {
        this.agenciaService.deletarAgenciaPorId(id);
        return RestResponse.ok();
    }
}
