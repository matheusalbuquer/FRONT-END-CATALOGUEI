package group.triade.catalogo.services;

import group.triade.catalogo.dtos.AuthRequestDTO;
import group.triade.catalogo.entities.Admin;
import group.triade.catalogo.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AutenticacaoService {

    @Autowired
    private AdminRepository adminRepository;


    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Admin usuario = adminRepository.findByEmail(login);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }



        return usuario;
    }

    public String gerarTokenJWT(Admin admin) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("my-secret");

            return JWT.create()
                    .withIssuer("itapissuma")
                    .withSubject(admin.getEmail())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algorithm);
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("Erro ao tentar gerar o token: " + exception.getMessage());
        }
    }


    public String obterToken(AuthRequestDTO authDto) {
        Admin usuario = adminRepository.findByEmail(authDto.email());

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }


        return gerarTokenJWT(usuario);
    }


    public String validaToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("my-secret");

            return JWT.require(algorithm)
                    .withIssuer("itapissuma")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (IllegalArgumentException exception) {
            return "Token invalido";
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now()
                .plusHours(8)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
}
