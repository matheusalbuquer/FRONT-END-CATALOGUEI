package group.triade.catalogo.repositories;

import group.triade.catalogo.entities.Lojista;
import group.triade.catalogo.entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProdutoRepository extends JpaRepository<Produto, Long> {

  Page<Produto> findByLojista(Lojista lojista, Pageable pageable);
}
