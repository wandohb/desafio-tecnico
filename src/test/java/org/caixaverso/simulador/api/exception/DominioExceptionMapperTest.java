package org.caixaverso.simulador.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.caixaverso.simulador.api.dto.ErrorResponse;
import org.caixaverso.simulador.domain.exception.ParametroSimulacaoInvalidoException;
import org.caixaverso.simulador.domain.exception.SimulacaoNaoEncontradaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DominioExceptionMapper")
class DominioExceptionMapperTest {

    @Mock
    private UriInfo uriInfo;

    @Test
    @DisplayName("ParametroSimulacaoInvalidoException -> 400 com ErrorResponse")
    void mapeiaParametroPara400() {
        when(uriInfo.getPath()).thenReturn("/api/v1/simulacoes");
        DominioExceptionMapper mapper = new DominioExceptionMapper();
        mapper.uriInfo = uriInfo;

        ParametroSimulacaoInvalidoException ex =
                new ParametroSimulacaoInvalidoException("valor invalido");

        Response response = mapper.toResponse(ex);

        assertEquals(400, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(400, body.status());
        assertEquals("Bad Request", body.error());
        assertEquals("valor invalido", body.message());
        assertEquals("/api/v1/simulacoes", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    @DisplayName("SimulacaoNaoEncontradaException -> 404 com ErrorResponse")
    void mapeiaNaoEncontradaPara404() {
        when(uriInfo.getPath()).thenReturn("/api/v1/simulacoes/99");
        DominioExceptionMapper mapper = new DominioExceptionMapper();
        mapper.uriInfo = uriInfo;

        SimulacaoNaoEncontradaException ex = new SimulacaoNaoEncontradaException(99L);

        Response response = mapper.toResponse(ex);

        assertEquals(404, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(404, body.status());
        assertEquals("Not Found", body.error());
        assertEquals("Simulacao com id 99 nao encontrada", body.message());
        assertEquals("/api/v1/simulacoes/99", body.path());
        assertNotNull(body.timestamp());
    }
}
