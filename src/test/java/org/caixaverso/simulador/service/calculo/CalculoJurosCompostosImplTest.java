package org.caixaverso.simulador.service.calculo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CalculoJurosCompostosImpl")
class CalculoJurosCompostosImplTest {

    private final CalculoJurosCompostosImpl calculo = new CalculoJurosCompostosImpl();

    @Nested
    @DisplayName("calculo numerico")
    class CalculoNumerico {

        @Test
        @DisplayName("caso canonico: 1000.00 / 1.5% / 12 meses")
        void casoCanonico() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("1000.00"),
                    new BigDecimal("1.5"),
                    12);

            assertEquals(new BigDecimal("1195.62"), result.valorTotalFinal());
            assertEquals(new BigDecimal("195.62"), result.valorTotalJuros());
            assertEquals(12, result.memoriaCalculo().size());

            ParcelaCalculada primeira = result.memoriaCalculo().get(0);
            assertEquals(1, primeira.mes());
            assertEquals(new BigDecimal("1000.00"), primeira.saldoInicial());
            assertEquals(new BigDecimal("15.00"), primeira.juros());
            assertEquals(new BigDecimal("1015.00"), primeira.saldoFinal());

            ParcelaCalculada ultima = result.memoriaCalculo().get(11);
            assertEquals(12, ultima.mes());
            assertEquals(new BigDecimal("1177.95"), ultima.saldoInicial());
            assertEquals(new BigDecimal("17.67"), ultima.juros());
            assertEquals(new BigDecimal("1195.62"), ultima.saldoFinal());
        }

        @Test
        @DisplayName("um mes: 1000.00 / 1.5% / 1 mes")
        void umMes() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("1000.00"),
                    new BigDecimal("1.5"),
                    1);

            assertEquals(1, result.memoriaCalculo().size());
            assertEquals(new BigDecimal("15.00"), result.memoriaCalculo().get(0).juros());
            assertEquals(new BigDecimal("1015.00"), result.valorTotalFinal());
            assertEquals(new BigDecimal("15.00"), result.valorTotalJuros());
        }

        @Test
        @DisplayName("taxa zero: juros sempre 0, valor final igual ao inicial")
        void taxaZero() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("1000.00"),
                    new BigDecimal("0"),
                    12);

            assertEquals(new BigDecimal("1000.00"), result.valorTotalFinal());
            assertEquals(new BigDecimal("0.00"), result.valorTotalJuros());
            for (ParcelaCalculada p : result.memoriaCalculo()) {
                assertEquals(new BigDecimal("0.00"), p.juros());
            }
        }
    }

    @Nested
    @DisplayName("arredondamento HALF_EVEN")
    class Arredondamento {

        @Test
        @DisplayName("empate exato vai pro vizinho par (8.825 -> 8.82, nao 8.83)")
        void empateExatoCaiParaPar() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("1000.00"),
                    new BigDecimal("0.8825"),
                    1);

            ParcelaCalculada p = result.memoriaCalculo().get(0);
            assertEquals(
                    new BigDecimal("8.82"),
                    p.juros(),
                    "HALF_EVEN deve arredondar 8.825 para 8.82 (par), nao 8.83 (HALF_UP)");
            assertEquals(new BigDecimal("1008.82"), p.saldoFinal());
            assertEquals(new BigDecimal("1008.82"), result.valorTotalFinal());
            assertEquals(new BigDecimal("8.82"), result.valorTotalJuros());
        }
    }

    @Nested
    @DisplayName("invariantes do agregado")
    class Invariantes {

        @Test
        @DisplayName("memoria tem tamanho igual ao prazo")
        void memoriaTemTamanhoIgualAoPrazo() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("500.00"),
                    new BigDecimal("2"),
                    24);

            assertEquals(24, result.memoriaCalculo().size());
        }

        @Test
        @DisplayName("saldoInicial de cada mes igual ao saldoFinal do anterior")
        void coerenciaEntreMeses() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("1000.00"),
                    new BigDecimal("1.5"),
                    12);

            List<ParcelaCalculada> memoria = result.memoriaCalculo();
            for (int i = 1; i < memoria.size(); i++) {
                assertEquals(
                        memoria.get(i - 1).saldoFinal(),
                        memoria.get(i).saldoInicial(),
                        "saldoInicial do mes " + (i + 1) + " deve ser saldoFinal do mes " + i);
            }
        }

        @Test
        @DisplayName("soma dos juros das parcelas igual ao valor total de juros")
        void somaDosJurosBate() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("1000.00"),
                    new BigDecimal("1.5"),
                    12);

            BigDecimal soma = result.memoriaCalculo().stream()
                    .map(ParcelaCalculada::juros)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            assertEquals(
                    0,
                    soma.compareTo(result.valorTotalJuros()),
                    "soma das parcelas deve fechar com valorTotalJuros");
        }

        @Test
        @DisplayName("memoria de calculo retornada eh imutavel")
        void memoriaCalculoImutavel() {
            ResultadoCalculo result = calculo.calcular(
                    new BigDecimal("1000.00"),
                    new BigDecimal("1.5"),
                    1);

            ParcelaCalculada extra = new ParcelaCalculada(
                    99, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

            assertThrows(
                    UnsupportedOperationException.class,
                    () -> result.memoriaCalculo().add(extra));
        }
    }

    @Nested
    @DisplayName("validacao de entrada")
    class Validacao {

        @Test
        @DisplayName("valorInicial null lanca NullPointerException")
        void valorInicialNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> calculo.calcular(null, new BigDecimal("1.5"), 12));
        }

        @Test
        @DisplayName("taxaJurosMensal null lanca NullPointerException")
        void taxaNull() {
            assertThrows(
                    NullPointerException.class,
                    () -> calculo.calcular(new BigDecimal("1000.00"), null, 12));
        }

        @Test
        @DisplayName("valorInicial zero lanca IllegalArgumentException")
        void valorInicialZero() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculo.calcular(new BigDecimal("0"), new BigDecimal("1.5"), 12));
        }

        @Test
        @DisplayName("valorInicial negativo lanca IllegalArgumentException")
        void valorInicialNegativo() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculo.calcular(new BigDecimal("-100"), new BigDecimal("1.5"), 12));
        }

        @Test
        @DisplayName("taxa negativa lanca IllegalArgumentException")
        void taxaNegativa() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculo.calcular(new BigDecimal("1000.00"), new BigDecimal("-1.5"), 12));
        }

        @Test
        @DisplayName("prazo zero lanca IllegalArgumentException")
        void prazoZero() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculo.calcular(new BigDecimal("1000.00"), new BigDecimal("1.5"), 0));
        }

        @Test
        @DisplayName("prazo negativo lanca IllegalArgumentException")
        void prazoNegativo() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculo.calcular(new BigDecimal("1000.00"), new BigDecimal("1.5"), -3));
        }
    }
}
