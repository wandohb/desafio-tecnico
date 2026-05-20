package org.caixaverso.simulador.api.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.caixaverso.simulador.api.dto.ErrorResponse;
import org.caixaverso.simulador.domain.exception.ParametroSimulacaoInvalidoException;

import java.time.Instant;

@Provider
public class ParametroSimulacaoInvalidoExceptionMapper
        implements ExceptionMapper<ParametroSimulacaoInvalidoException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ParametroSimulacaoInvalidoException exception) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                Response.Status.BAD_REQUEST.getStatusCode(),
                Response.Status.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                uriInfo.getPath());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(body)
                .build();
    }
}
