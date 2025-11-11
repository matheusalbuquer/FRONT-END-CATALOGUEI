package group.triade.catalogo.dtos;

public record AuthResponseDTO(String token,Long id ,String nome, String email, String expiresAt) {
}
