package org.caixaverso.simulador.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Parcela")
class ParcelaTest {

    @Test
    @DisplayName("construtor publico preserva valores e nasce sem Simulacao associada")
    void construtorPreservaValores() {
        Parcela parcela = new Parcela(
                3,
                new BigDecimal("1030.22"),
                new BigDecimal("15.45"),
                new BigDecimal("1045.67"));

        assertEquals(3, parcela.getMes());
        assertEquals(new BigDecimal("1030.22"), parcela.getSaldoInicial());
        assertEquals(new BigDecimal("15.45"), parcela.getJuros());
        assertEquals(new BigDecimal("1045.67"), parcela.getSaldoFinal());
        assertNull(parcela.getSimulacao(),
                "Parcela recem-criada nao deve ter Simulacao ate ser adicionada");
    }
}
