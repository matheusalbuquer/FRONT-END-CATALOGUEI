package group.triade.catalogo.dtos.produto;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProdutoRequestDTO (String nome,
                                 BigDecimal preco,
                                 String descricao,
                                 MultipartFile imagem,
                                 //Admin
                                 Long lojistaId
) {
}
