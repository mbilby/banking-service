package br.com.alura.service.http;

import br.com.alura.domain.http.AgenciaHTTP;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/situacao-cadastral")
@RegisterRestClient(configKey = "situacao-cadastral-api")
public interface SituacaoCadastralHttpService {

    /**
     * @param : CNPJ - identificador
     * @see: Uni<AgenciaHTTP> Representa uma promessa de um Objeto do tipo AgenciaHTTP*/
    @GET
    @Path("{cnpj}")
    Uni<AgenciaHTTP> buscarPorCnpj(String cnpj);

}
