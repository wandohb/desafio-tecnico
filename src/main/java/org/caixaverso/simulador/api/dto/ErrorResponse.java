package org.caixaverso.simulador.api.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Payload de erro retornado quando a requisicao falha")
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path) {
}
