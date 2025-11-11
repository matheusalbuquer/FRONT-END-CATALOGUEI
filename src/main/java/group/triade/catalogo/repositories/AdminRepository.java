package group.triade.catalogo.repositories;

import group.triade.catalogo.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

     Admin findByEmail (String email);
}
