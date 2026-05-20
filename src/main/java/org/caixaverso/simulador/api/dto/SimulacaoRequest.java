package org.caixaverso.simulador.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimulacaoRequest(

        @NotNull(message = "valorInicial nao pode ser nulo")
        @DecimalMin(value = "0.01", message = "valorInicial deve ser maior que zero")
        BigDecimal valorInicial,

        @NotNull(message = "taxaJurosMensal nao pode ser nulo")
        @DecimalMin(value = "0.0", message = "taxaJurosMensal nao pode ser negativa")
        BigDecimal taxaJurosMensal,

        @Min(value = 1, message = "prazoMeses deve ser maior que zero")
        @Max(value = 360, message = "prazoMeses nao pode ser maior que 360")
        int prazoMeses) {
}
