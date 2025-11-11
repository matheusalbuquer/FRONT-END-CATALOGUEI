package group.triade.catalogo.config;



import group.triade.catalogo.services.AdminService;
import group.triade.catalogo.services.AutenticacaoService;
import org.springframework.stereotype.Service;

/**
 * Adapte este serviço para usar seus repositórios/serviços existentes.
 * Objetivo: (1) buscar/criar usuário por e-mail, (2) gerar seu JWT.
 */
@Service
public class UsuarioOAuthBridge {

    private final AutenticacaoService jwtTokenService; // OU AutenticacaoService que já gera token
    private final AdminService usuarioService;   // OU AdminService/PacienteService, conforme seu domínio

    public UsuarioOAuthBridge(AutenticacaoService jwtTokenService, AdminService usuarioService) {
        this.jwtTokenService = jwtTokenService;
        this.usuarioService = usuarioService;
    }

    public String provisionarUsuarioEGerarJwt(String email, String nome) {
        // 1) Buscar usuário por e-mail; se não existir, criar com ROLE_USER (ou conforme sua regra)
        var usuario = usuarioService.buscarPorEmail(email)
                .orElseGet(() -> usuarioService.criarUsuarioOAuth(email, nome));

        // 2) Gerar JWT da sua aplicação (mesmo formato usado no /auth)
        return jwtTokenService.gerarTokenJWT(usuario); // retorna string do JWT
    }
}
