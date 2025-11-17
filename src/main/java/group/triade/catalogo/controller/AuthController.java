package group.triade.catalogo.controller;

import group.triade.catalogo.dtos.AuthRequestDTO;
import group.triade.catalogo.dtos.AuthResponseDTO;
import group.triade.catalogo.entities.Lojista;
import group.triade.catalogo.repositories.LojistaRepository;
import group.triade.catalogo.services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private LojistaRepository adminRepository;

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO dto) {
      // 1) Busca lojista pelo email
      Lojista lojista = adminRepository.findByEmail(dto.email());
      if (lojista == null) {
        throw new BadCredentialsException("Credenciais inválidas");
      }

      // 2) Compara a SENHA enviada com a SENHA criptografada
      boolean senhaCorreta = passwordEncoder.matches(dto.senha(), lojista.getPassword());
      if (!senhaCorreta) {
        throw new BadCredentialsException("Credenciais inválidas");
      }

      // 3) Gera token
      String token = autenticacaoService.gerarTokenJWT(lojista);

      AuthResponseDTO resp = new AuthResponseDTO(
        token,
        lojista.getId(),
        lojista.getNome(),
        lojista.getEmail(),
        LocalDateTime.now().plusHours(8).toString()
      );

      return ResponseEntity.ok(resp);
    }

}
