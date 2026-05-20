package org.caixaverso.simulador.api.dto;

import org.caixaverso.simulador.domain.Simulacao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SimulacaoResponse(
        Long id,
        BigDecimal valorInicial,
        BigDecimal taxaJurosMensal,
        int prazoMeses,
        BigDecimal valorTotalFinal,
        BigDecimal valorTotalJuros,
        Instant criadoEm,
        List<ParcelaResponse> memoriaCalculo) {

    public static SimulacaoResponse fromEntity(Simulacao simulacao) {
        List<ParcelaResponse> parcelas = simulacao.getParcelas().stream()
                .map(ParcelaResponse::fromEntity)
                .toList();

        return new SimulacaoResponse(
                simulacao.getId(),
                simulacao.getValorInicial(),
                simulacao.getTaxaJurosMensal(),
                simulacao.getPrazoMeses(),
                simulacao.getValorTotalFinal(),
                simulacao.getValorTotalJuros(),
                simulacao.getCriadoEm(),
                parcelas);
    }
}
