package group.triade.catalogo.services;

import group.triade.catalogo.dtos.AdminRequestDTO;
import group.triade.catalogo.dtos.AdminResponseDTO;
import group.triade.catalogo.entities.Admin;
import group.triade.catalogo.repositories.AdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public AdminResponseDTO criar (AdminRequestDTO dto){

        var senha = passwordEncoder.encode(dto.senha());

        Admin admin = new Admin();
        admin.setNome(dto.nome());
        admin.setEmail(dto.email());
        admin.setSenha(senha);

        Admin salvo = adminRepository.save(admin);

        return new AdminResponseDTO(
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getSenha()
        );
    }




    public Optional<Admin> buscarPorEmail(String email) {
        String loginNorm = normalizarLogin(email);
        return Optional.ofNullable(adminRepository.findByEmail(loginNorm));
    }


    public Admin criarUsuarioOAuth(String email, String nome) {
        String loginNorm = normalizarLogin(email);

        // Se já existir, retorna existente
        Admin existente = adminRepository.findByEmail(loginNorm);
        if (existente != null) {
            return existente;
        }

        Admin novo = new Admin();
        novo.setEmail(loginNorm);
        novo.setNome(nome);

        // defina a role padrão para quem entra via Google (ajuste se precisar)
        // se seu enum se chama diferente, ajuste aqui


        // Senha randômica só para cumprir not-null (não será usada no OAuth)
        String senhaRandom = "oauth-" + UUID.randomUUID();
        novo.setSenha(passwordEncoder.encode(senhaRandom));


        return adminRepository.save(novo);
    }

    // =========================
    private String normalizarLogin(String login) {
        if (login == null) return null;
        return login.trim().toLowerCase();
    }


}
