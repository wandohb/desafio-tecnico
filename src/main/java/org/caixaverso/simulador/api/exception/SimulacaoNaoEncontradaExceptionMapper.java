package org.caixaverso.simulador.api.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.caixaverso.simulador.api.dto.ErrorResponse;
import org.caixaverso.simulador.domain.exception.SimulacaoNaoEncontradaException;

import java.time.Instant;

@Provider
public class SimulacaoNaoEncontradaExceptionMapper
        implements ExceptionMapper<SimulacaoNaoEncontradaException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(SimulacaoNaoEncontradaException exception) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                Response.Status.NOT_FOUND.getStatusCode(),
                Response.Status.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                uriInfo.getPath());

        return Response.status(Response.Status.NOT_FOUND)
                .entity(body)
                .build();
    }
}
