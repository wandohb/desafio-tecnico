package org.caixaverso.simulador.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "simulacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorInicial;

    @Column(name = "taxa_juros_mensal", nullable = false, precision = 6, scale = 2)
    private BigDecimal taxaJurosMensal;

    @Column(name = "prazo_meses", nullable = false)
    private int prazoMeses;

    @Column(name = "valor_total_final", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalFinal;

    @Column(name = "valor_total_juros", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalJuros;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "simulacao",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @OrderBy("mes ASC")
    private List<Parcela> parcelas = new ArrayList<>();

    public Simulacao(
            BigDecimal valorInicial,
            BigDecimal taxaJurosMensal,
            int prazoMeses,
            BigDecimal valorTotalFinal,
            BigDecimal valorTotalJuros) {
        this.valorInicial = valorInicial;
        this.taxaJurosMensal = taxaJurosMensal;
        this.prazoMeses = prazoMeses;
        this.valorTotalFinal = valorTotalFinal;
        this.valorTotalJuros = valorTotalJuros;
    }

    public void adicionarParcela(Parcela parcela) {
        parcela.setSimulacao(this);
        this.parcelas.add(parcela);
    }

    public List<Parcela> getParcelas() {
        return Collections.unmodifiableList(parcelas);
    }
}
