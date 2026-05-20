package org.caixaverso.simulador.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Simulacao")
class SimulacaoTest {

    @Test
    @DisplayName("construtor publico preserva valores e inicia sem parcelas")
    void construtorPreservaValores() {
        Simulacao simulacao = novaSimulacao();

        assertEquals(new BigDecimal("1000.00"), simulacao.getValorInicial());
        assertEquals(new BigDecimal("1.5"), simulacao.getTaxaJurosMensal());
        assertEquals(12, simulacao.getPrazoMeses());
        assertEquals(new BigDecimal("1195.62"), simulacao.getValorTotalFinal());
        assertEquals(new BigDecimal("195.62"), simulacao.getValorTotalJuros());
        assertTrue(simulacao.getParcelas().isEmpty());
    }

    @Test
    @DisplayName("adicionarParcela adiciona na lista e associa o pai")
    void adicionarParcelaMantemBidirecional() {
        Simulacao simulacao = novaSimulacao();
        Parcela parcela = new Parcela(
                1,
                new BigDecimal("1000.00"),
                new BigDecimal("15.00"),
                new BigDecimal("1015.00"));

        simulacao.adicionarParcela(parcela);

        assertEquals(1, simulacao.getParcelas().size());
        assertSame(parcela, simulacao.getParcelas().get(0));
        assertSame(simulacao, parcela.getSimulacao(),
                "Parcela deve apontar de volta pra Simulacao apos adicionar");
    }

    @Test
    @DisplayName("adicionarParcela suporta multiplas parcelas em ordem")
    void adicionarMultiplasParcelas() {
        Simulacao simulacao = novaSimulacao();
        Parcela primeira = new Parcela(1, new BigDecimal("1000.00"),
                new BigDecimal("15.00"), new BigDecimal("1015.00"));
        Parcela segunda = new Parcela(2, new BigDecimal("1015.00"),
                new BigDecimal("15.22"), new BigDecimal("1030.22"));

        simulacao.adicionarParcela(primeira);
        simulacao.adicionarParcela(segunda);

        assertEquals(2, simulacao.getParcelas().size());
        assertEquals(1, simulacao.getParcelas().get(0).getMes());
        assertEquals(2, simulacao.getParcelas().get(1).getMes());
    }

    @Test
    @DisplayName("getParcelas retorna lista imutavel")
    void getParcelasRetornaImutavel() {
        Simulacao simulacao = novaSimulacao();
        Parcela extra = new Parcela(99, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThrows(
                UnsupportedOperationException.class,
                () -> simulacao.getParcelas().add(extra));
    }

    private Simulacao novaSimulacao() {
        Simulacao s = new Simulacao(
                new BigDecimal("1000.00"),
                new BigDecimal("1.5"),
                12,
                new BigDecimal("1195.62"),
                new BigDecimal("195.62"));
        assertNotNull(s);
        return s;
    }
}
