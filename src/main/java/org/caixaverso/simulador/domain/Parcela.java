package org.caixaverso.simulador.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "parcela")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "simulacao_id", nullable = false)
    private Simulacao simulacao;

    @Column(name = "mes", nullable = false)
    private int mes;

    @Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "juros", nullable = false, precision = 15, scale = 2)
    private BigDecimal juros;

    @Column(name = "saldo_final", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoFinal;

    public Parcela(int mes, BigDecimal saldoInicial, BigDecimal juros, BigDecimal saldoFinal) {
        this.mes = mes;
        this.saldoInicial = saldoInicial;
        this.juros = juros;
        this.saldoFinal = saldoFinal;
    }
}
