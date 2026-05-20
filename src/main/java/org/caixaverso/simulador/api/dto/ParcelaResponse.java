package org.caixaverso.simulador.api.dto;

import org.caixaverso.simulador.domain.Parcela;

import java.math.BigDecimal;

public record ParcelaResponse(
        int mes,
        BigDecimal saldoInicial,
        BigDecimal juros,
        BigDecimal saldoFinal) {

    public static ParcelaResponse fromEntity(Parcela parcela) {
        return new ParcelaResponse(
                parcela.getMes(),
                parcela.getSaldoInicial(),
                parcela.getJuros(),
                parcela.getSaldoFinal());
    }
}
