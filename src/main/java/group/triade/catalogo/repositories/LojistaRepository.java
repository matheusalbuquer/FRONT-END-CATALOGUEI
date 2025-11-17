package group.triade.catalogo.repositories;

import group.triade.catalogo.entities.Lojista;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LojistaRepository extends JpaRepository<Lojista, Long> {

    Lojista findByNome (String nome);
     Lojista findByEmail (String email);
}
