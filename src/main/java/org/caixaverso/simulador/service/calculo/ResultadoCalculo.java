package org.caixaverso.simulador.service.calculo;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoCalculo(
        BigDecimal valorTotalFinal,
        BigDecimal valorTotalJuros,
        List<ParcelaCalculada> memoriaCalculo) {
}
