package group.triade.catalogo.services;

import group.triade.catalogo.dtos.LojistaRequestDTO;
import group.triade.catalogo.dtos.LojistaResponseDTO;
import group.triade.catalogo.entities.Lojista;
import group.triade.catalogo.repositories.LojistaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LojistaService {

      @Autowired
      private LojistaRepository lojistaRepository;

      @Autowired
      private PasswordEncoder passwordEncoder;

      @Transactional
      public LojistaResponseDTO criar (LojistaRequestDTO dto){

          var senha = passwordEncoder.encode(dto.senha());

          Lojista lojista = new Lojista();
          lojista.setNome(dto.nome());
          lojista.setEmail(dto.email());
          lojista.setSenha(senha);

          Lojista salvo = lojistaRepository.save(lojista);

          return new LojistaResponseDTO(
                  salvo.getNome(),
                  salvo.getEmail(),
                  salvo.getSenha()
          );
      }





      public Optional<Lojista> buscarPorEmail(String email) {
          String loginNorm = normalizarLogin(email);
          return Optional.ofNullable(lojistaRepository.findByEmail(loginNorm));
      }


      public Lojista criarUsuarioOAuth(String email, String nome) {
          String loginNorm = normalizarLogin(email);

          // Se já existir, retorna existente
          Lojista existente = lojistaRepository.findByEmail(loginNorm);
          if (existente != null) {
              return existente;
          }

          Lojista novo = new Lojista();
          novo.setEmail(loginNorm);
          novo.setNome(nome);

          // defina a role padrão para quem entra via Google (ajuste se precisar)
          // se seu enum se chama diferente, ajuste aqui


          // Senha randômica só para cumprir not-null (não será usada no OAuth)
          String senhaRandom = "oauth-" + UUID.randomUUID();
          novo.setSenha(passwordEncoder.encode(senhaRandom));


          return lojistaRepository.save(novo);
      }

      // =========================
      private String normalizarLogin(String login) {
          if (login == null) return null;
          return login.trim().toLowerCase();
      }


}
