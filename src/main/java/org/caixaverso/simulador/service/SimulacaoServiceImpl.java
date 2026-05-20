package org.caixaverso.simulador.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.caixaverso.simulador.domain.Parcela;
import org.caixaverso.simulador.domain.Simulacao;
import org.caixaverso.simulador.domain.exception.SimulacaoNaoEncontradaException;
import org.caixaverso.simulador.persistence.SimulacaoRepository;
import org.caixaverso.simulador.service.calculo.CalculoJurosService;
import org.caixaverso.simulador.service.calculo.ParcelaCalculada;
import org.caixaverso.simulador.service.calculo.ResultadoCalculo;

import java.math.BigDecimal;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService {

    private final CalculoJurosService calculo;
    private final SimulacaoRepository repository;

    @Inject
    public SimulacaoServiceImpl(CalculoJurosService calculo, SimulacaoRepository repository) {
        this.calculo = calculo;
        this.repository = repository;
    }

    @Override
    @Transactional
    public Simulacao simular(BigDecimal valorInicial, BigDecimal taxaJurosMensal, int prazoMeses) {
        ResultadoCalculo resultado = calculo.calcular(valorInicial, taxaJurosMensal, prazoMeses);

        Simulacao simulacao = new Simulacao(
                valorInicial,
                taxaJurosMensal,
                prazoMeses,
                resultado.valorTotalFinal(),
                resultado.valorTotalJuros());

        for (ParcelaCalculada pc : resultado.memoriaCalculo()) {
            simulacao.adicionarParcela(new Parcela(
                    pc.mes(),
                    pc.saldoInicial(),
                    pc.juros(),
                    pc.saldoFinal()));
        }

        repository.persist(simulacao);
        return simulacao;
    }

    @Override
    public Simulacao buscar(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new SimulacaoNaoEncontradaException(id));
    }
}
