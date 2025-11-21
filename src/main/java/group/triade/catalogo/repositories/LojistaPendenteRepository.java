package group.triade.catalogo.repositories;

import group.triade.catalogo.entities.LojistaPendente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LojistaPendenteRepository extends JpaRepository<LojistaPendente, Long> {

  Optional<LojistaPendente> findByExternalReference (String externalReference);
}
