package group.triade.catalogo.controller;

import group.triade.catalogo.dtos.AuthRequestDTO;
import group.triade.catalogo.dtos.AuthResponseDTO;
import group.triade.catalogo.entities.Admin;
import group.triade.catalogo.repositories.AdminRepository;
import group.triade.catalogo.services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
        Admin admin = adminRepository.findByEmail(dto.email());
        if(admin == null || passwordEncoder.matches(dto.email(),admin.getSenha()) ){
            throw  new BadCredentialsException("Credenciasi invalidas");
        }

        String token = autenticacaoService.gerarTokenJWT(admin);

        AuthResponseDTO resp = new AuthResponseDTO(
                token,
                admin.getId(),
                admin.getNome(),
                admin.getEmail(),
                LocalDateTime.now().plusHours(8).toString()
        );

        return ResponseEntity.ok(resp);
    }
}
