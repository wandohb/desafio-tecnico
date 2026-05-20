package org.caixaverso.simulador.service.calculo;

import java.math.BigDecimal;

public record ParcelaCalculada(
        int mes,
        BigDecimal saldoInicial,
        BigDecimal juros,
        BigDecimal saldoFinal) {
}
