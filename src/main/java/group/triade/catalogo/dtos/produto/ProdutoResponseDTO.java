package group.triade.catalogo.dtos.produto;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProdutoResponseDTO(Long id,
                                 String nome,
                                 BigDecimal preco,
                                 String descricao,
                                 String imagem
) {
}
