package org.caixaverso.simulador.service.calculo;

import java.math.BigDecimal;

public interface CalculoJurosService {

    ResultadoCalculo calcular(BigDecimal valorInicial, BigDecimal taxaJurosMensal, int prazoMeses);
}
