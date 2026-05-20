package org.caixaverso.simulador.service;

import org.caixaverso.simulador.domain.Simulacao;

import java.math.BigDecimal;

public interface SimulacaoService {

    Simulacao simular(BigDecimal valorInicial, BigDecimal taxaJurosMensal, int prazoMeses);

    Simulacao buscar(Long id);
}
