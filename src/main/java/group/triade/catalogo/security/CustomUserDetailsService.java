package group.triade.catalogo.security;

import group.triade.catalogo.entities.Admin;
import group.triade.catalogo.repositories.AdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public CustomUserDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email);

        return new org.springframework.security.core.userdetails.User(
                admin.getEmail(),
                admin.getSenha(),
                List.of() // pode adicionar roles aqui depois
        );
    }
}
