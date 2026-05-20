package org.caixaverso.simulador.api.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.caixaverso.simulador.api.dto.ErrorResponse;

import java.time.Instant;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String mensagemConsolidada = exception.getConstraintViolations().stream()
                .map(this::formatarViolacao)
                .collect(Collectors.joining("; "));

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                Response.Status.BAD_REQUEST.getStatusCode(),
                Response.Status.BAD_REQUEST.getReasonPhrase(),
                mensagemConsolidada,
                uriInfo.getPath());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(body)
                .build();
    }

    private String formatarViolacao(ConstraintViolation<?> violacao) {
        return violacao.getMessage();
    }
}
