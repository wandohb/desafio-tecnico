package org.caixaverso.simulador.service.calculo;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class CalculoJurosCompostosImpl implements CalculoJurosService {

    private static final BigDecimal CEM = new BigDecimal("100");
    private static final int ESCALA_MONETARIA = 2;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;

    @Override
    public ResultadoCalculo calcular(BigDecimal valorInicial, BigDecimal taxaJurosMensal, int prazoMeses) {
        validar(valorInicial, taxaJurosMensal, prazoMeses);

        BigDecimal taxaDecimal = taxaJurosMensal.divide(CEM, MathContext.DECIMAL128);
        BigDecimal valorInicialArredondado = arredondar(valorInicial);

        List<ParcelaCalculada> memoria = new ArrayList<>(prazoMeses);
        BigDecimal saldoAtual = valorInicialArredondado;

        for (int mes = 1; mes <= prazoMeses; mes++) {
            BigDecimal saldoInicio = saldoAtual;
            BigDecimal juros = arredondar(saldoInicio.multiply(taxaDecimal));
            BigDecimal saldoFim = arredondar(saldoInicio.add(juros));

            memoria.add(new ParcelaCalculada(mes, saldoInicio, juros, saldoFim));
            saldoAtual = saldoFim;
        }

        BigDecimal valorTotalFinal = saldoAtual;
        BigDecimal valorTotalJuros = arredondar(valorTotalFinal.subtract(valorInicialArredondado));

        return new ResultadoCalculo(valorTotalFinal, valorTotalJuros, List.copyOf(memoria));
    }

    private void validar(BigDecimal valorInicial, BigDecimal taxaJurosMensal, int prazoMeses) {
        Objects.requireNonNull(valorInicial, "valorInicial nao pode ser nulo");
        Objects.requireNonNull(taxaJurosMensal, "taxaJurosMensal nao pode ser nulo");
        if (valorInicial.signum() <= 0) {
            throw new IllegalArgumentException("valorInicial deve ser maior que zero");
        }
        if (taxaJurosMensal.signum() < 0) {
            throw new IllegalArgumentException("taxaJurosMensal nao pode ser negativa");
        }
        if (prazoMeses <= 0) {
            throw new IllegalArgumentException("prazoMeses deve ser maior que zero");
        }
    }

    private BigDecimal arredondar(BigDecimal valor) {
        return valor.setScale(ESCALA_MONETARIA, ARREDONDAMENTO);
    }
}
