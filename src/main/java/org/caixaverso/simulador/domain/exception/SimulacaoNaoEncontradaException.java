package org.caixaverso.simulador.domain.exception;

public final class SimulacaoNaoEncontradaException extends DominioException {

    public SimulacaoNaoEncontradaException(Long id) {
        super("Simulacao com id " + id + " nao encontrada");
    }
}
