package org.caixaverso.simulador.service;

import org.caixaverso.simulador.domain.Parcela;
import org.caixaverso.simulador.domain.Simulacao;
import org.caixaverso.simulador.domain.exception.SimulacaoNaoEncontradaException;
import org.caixaverso.simulador.persistence.SimulacaoRepository;
import org.caixaverso.simulador.service.calculo.CalculoJurosService;
import org.caixaverso.simulador.service.calculo.ParcelaCalculada;
import org.caixaverso.simulador.service.calculo.ResultadoCalculo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimulacaoServiceImpl")
class SimulacaoServiceImplTest {

    @Mock
    private CalculoJurosService calculo;

    @Mock
    private SimulacaoRepository repository;

    @InjectMocks
    private SimulacaoServiceImpl service;

    @Nested
    @DisplayName("simular")
    class Simular {

        @Test
        @DisplayName("delega o calculo com os parametros recebidos")
        void delegaCalculoComParametros() {
            BigDecimal valor = new BigDecimal("1000.00");
            BigDecimal taxa = new BigDecimal("1.5");
            int prazo = 12;
            when(calculo.calcular(valor, taxa, prazo)).thenReturn(resultadoFake());

            service.simular(valor, taxa, prazo);

            verify(calculo).calcular(valor, taxa, prazo);
        }

        @Test
        @DisplayName("constroi Simulacao com totais do resultado e persiste")
        void constroiEPersisteEntidade() {
            BigDecimal valor = new BigDecimal("1000.00");
            BigDecimal taxa = new BigDecimal("1.5");
            int prazo = 12;
            ResultadoCalculo resultado = resultadoFake();
            when(calculo.calcular(valor, taxa, prazo)).thenReturn(resultado);

            Simulacao retornada = service.simular(valor, taxa, prazo);

            ArgumentCaptor<Simulacao> captor = ArgumentCaptor.forClass(Simulacao.class);
            verify(repository).persist(captor.capture());
            Simulacao persistida = captor.getValue();

            assertSame(persistida, retornada, "service deve retornar a mesma instancia persistida");
            assertEquals(valor, persistida.getValorInicial());
            assertEquals(taxa, persistida.getTaxaJurosMensal());
            assertEquals(prazo, persistida.getPrazoMeses());
            assertEquals(resultado.valorTotalFinal(), persistida.getValorTotalFinal());
            assertEquals(resultado.valorTotalJuros(), persistida.getValorTotalJuros());
        }

        @Test
        @DisplayName("adiciona todas as parcelas do resultado mantendo a relacao bidirecional")
        void adicionaTodasAsParcelas() {
            BigDecimal valor = new BigDecimal("1000.00");
            BigDecimal taxa = new BigDecimal("1.5");
            int prazo = 2;
            ResultadoCalculo resultado = new ResultadoCalculo(
                    new BigDecimal("1030.22"),
                    new BigDecimal("30.22"),
                    List.of(
                            new ParcelaCalculada(1, new BigDecimal("1000.00"),
                                    new BigDecimal("15.00"), new BigDecimal("1015.00")),
                            new ParcelaCalculada(2, new BigDecimal("1015.00"),
                                    new BigDecimal("15.22"), new BigDecimal("1030.22"))));
            when(calculo.calcular(valor, taxa, prazo)).thenReturn(resultado);

            Simulacao persistida = service.simular(valor, taxa, prazo);

            List<Parcela> parcelas = persistida.getParcelas();
            assertEquals(2, parcelas.size());
            assertEquals(1, parcelas.get(0).getMes());
            assertEquals(new BigDecimal("15.00"), parcelas.get(0).getJuros());
            assertEquals(2, parcelas.get(1).getMes());
            assertEquals(new BigDecimal("15.22"), parcelas.get(1).getJuros());
            assertSame(persistida, parcelas.get(0).getSimulacao(),
                    "Parcela[0] deve apontar de volta pra Simulacao");
            assertSame(persistida, parcelas.get(1).getSimulacao(),
                    "Parcela[1] deve apontar de volta pra Simulacao");
        }
    }

    @Nested
    @DisplayName("buscar")
    class Buscar {

        @Test
        @DisplayName("retorna a Simulacao quando o repositorio a encontra")
        void retornaQuandoEncontrada() {
            Simulacao existente = new Simulacao(
                    new BigDecimal("500.00"),
                    new BigDecimal("2"),
                    6,
                    new BigDecimal("563.08"),
                    new BigDecimal("63.08"));
            when(repository.findByIdComParcelas(42L)).thenReturn(Optional.of(existente));

            Simulacao retornada = service.buscar(42L);

            assertSame(existente, retornada);
            verify(repository, never()).persist(any(Simulacao.class));
        }

        @Test
        @DisplayName("lanca SimulacaoNaoEncontradaException quando id nao existe")
        void lancaExceptionQuandoNaoEncontrada() {
            when(repository.findByIdComParcelas(999L)).thenReturn(Optional.empty());

            SimulacaoNaoEncontradaException ex = assertThrows(
                    SimulacaoNaoEncontradaException.class,
                    () -> service.buscar(999L));

            assertEquals("Simulacao com id 999 nao encontrada", ex.getMessage());
        }
    }

    private ResultadoCalculo resultadoFake() {
        return new ResultadoCalculo(
                new BigDecimal("1195.62"),
                new BigDecimal("195.62"),
                List.of(new ParcelaCalculada(1,
                        new BigDecimal("1000.00"),
                        new BigDecimal("15.00"),
                        new BigDecimal("1015.00"))));
    }
}
