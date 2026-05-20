package org.caixaverso.simulador.domain.exception;

public class SimulacaoNaoEncontradaException extends RuntimeException {

    public SimulacaoNaoEncontradaException(Long id) {
        super("Simulacao com id " + id + " nao encontrada");
    }
}
