package org.caixaverso.simulador.domain.exception;

public abstract sealed class DominioException extends RuntimeException
        permits ParametroSimulacaoInvalidoException, SimulacaoNaoEncontradaException {

    protected DominioException(String mensagem) {
        super(mensagem);
    }
}
