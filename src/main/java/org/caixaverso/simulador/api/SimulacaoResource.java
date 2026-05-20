package org.caixaverso.simulador.api;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.caixaverso.simulador.api.dto.SimulacaoRequest;
import org.caixaverso.simulador.api.dto.SimulacaoResponse;
import org.caixaverso.simulador.domain.Simulacao;
import org.caixaverso.simulador.service.SimulacaoService;

@Path("/api/v1/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    private final SimulacaoService service;

    @Inject
    public SimulacaoResource(SimulacaoService service) {
        this.service = service;
    }

    @POST
    public Response criar(@Valid SimulacaoRequest request) {
        Simulacao simulacao = service.simular(
                request.valorInicial(),
                request.taxaJurosMensal(),
                request.prazoMeses());

        return Response.status(Response.Status.CREATED)
                .entity(SimulacaoResponse.fromEntity(simulacao))
                .build();
    }

    @GET
    @Path("/{id}")
    public SimulacaoResponse buscar(@PathParam("id") Long id) {
        return SimulacaoResponse.fromEntity(service.buscar(id));
    }
}
