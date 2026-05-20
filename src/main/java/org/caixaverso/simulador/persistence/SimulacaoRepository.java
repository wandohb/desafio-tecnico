package org.caixaverso.simulador.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.caixaverso.simulador.domain.Simulacao;

import java.util.Optional;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {

    public Optional<Simulacao> findByIdComParcelas(Long id) {
        return find("FROM Simulacao s LEFT JOIN FETCH s.parcelas WHERE s.id = ?1", id)
                .firstResultOptional();
    }
}
