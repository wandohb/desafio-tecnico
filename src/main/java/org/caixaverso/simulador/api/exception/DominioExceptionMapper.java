package org.caixaverso.simulador.api.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.caixaverso.simulador.api.dto.ErrorResponse;
import org.caixaverso.simulador.domain.exception.DominioException;
import org.caixaverso.simulador.domain.exception.ParametroSimulacaoInvalidoException;
import org.caixaverso.simulador.domain.exception.SimulacaoNaoEncontradaException;

import java.time.Instant;

@Provider
public class DominioExceptionMapper implements ExceptionMapper<DominioException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(DominioException exception) {
        Response.Status status = switch (exception) {
            case ParametroSimulacaoInvalidoException _ -> Response.Status.BAD_REQUEST;
            case SimulacaoNaoEncontradaException _ -> Response.Status.NOT_FOUND;
        };

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.getStatusCode(),
                status.getReasonPhrase(),
                exception.getMessage(),
                uriInfo.getPath());

        return Response.status(status).entity(body).build();
    }
}
