package group.triade.catalogo.services;

import group.triade.catalogo.dtos.LojistaRequestDTO;
import group.triade.catalogo.dtos.LojistaResponseDTO;
import group.triade.catalogo.dtos.produto.ProdutoRequestDTO;
import group.triade.catalogo.dtos.produto.ProdutoResponseDTO;
import group.triade.catalogo.entities.Lojista;
import group.triade.catalogo.entities.Produto;
import group.triade.catalogo.repositories.LojistaRepository;
import group.triade.catalogo.repositories.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ProdutoService {

  @Autowired
  private ProdutoRepository produtoRepository;
  @Autowired
  private  LojistaRepository lojistaRepository;

  @Value("${cataloguei.upload-dir}")
  private String baseUploadDir;

  @Transactional
  public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
    // pega lojista logado
    String login = SecurityContextHolder.getContext().getAuthentication().getName();

    Lojista lojista = lojistaRepository.findByEmail(login);
    if (lojista == null) {
      throw new IllegalStateException("Usuário não encontrado");
    }

    Produto produto = new Produto();
    produto.setNome(dto.nome());
    produto.setPreco(dto.preco());
    produto.setDescricao(dto.descricao());
    produto.setLojista(lojista);

    // imagem opcional: salva só se veio arquivo
    if (dto.imagem() != null && !dto.imagem().isEmpty()) {
      String url = salvarImagemLocal(dto.imagem(), "produtos"); // /uploads/produtos/...
      produto.setImgUrl(url); // grava apenas a URL no banco
    }

    Produto salvo = produtoRepository.save(produto);

    return new ProdutoResponseDTO(
      salvo.getId(),
      salvo.getNome(),
      salvo.getPreco(),
      salvo.getDescricao(),
      salvo.getImgUrl()
    );
  }

  @Transactional
  public ProdutoResponseDTO editar(Long id, ProdutoRequestDTO dto) {

    // 1) Pega o lojista logado
    String login = SecurityContextHolder.getContext()
      .getAuthentication()
      .getName();

    Lojista lojista = lojistaRepository.findByEmail(login);
    if (lojista == null) {
      throw new IllegalStateException("Usuário não encontrado");
    }

    // 2) Produto deve existir
    Produto produto = produtoRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

    // 3) Verifica se o produto pertence ao lojista logado
    if (!produto.getLojista().getId().equals(lojista.getId())) {
      throw new SecurityException("Você não tem permissão para editar este produto");
    }

    // 4) Atualiza dados básicos
    produto.setNome(dto.nome());
    produto.setDescricao(dto.descricao());
    produto.setPreco(dto.preco());

    // 5) Atualiza imagem, se veio nova
    if (dto.imagem() != null && !dto.imagem().isEmpty()) {
      // (Opcional) aqui você poderia apagar a imagem antiga do disco, se quiser
      String url = salvarImagemLocal(dto.imagem(), "produtos");
      produto.setImgUrl(url);
    }

    Produto salvo = produtoRepository.save(produto);

    // 6) Retorna DTO
    return new ProdutoResponseDTO(
      salvo.getId(),
      salvo.getNome(),
      salvo.getPreco(),
      salvo.getDescricao(),
      salvo.getImgUrl()
    );
  }



  @Transactional
  public Page<ProdutoResponseDTO> obterProdutosDoLojista(Pageable pageable) {

    String login = SecurityContextHolder.getContext().getAuthentication().getName();

    Lojista lojista = lojistaRepository.findByEmail(login);
    if (lojista == null) {
      throw new IllegalStateException("Usuário não encontrado");
    }

    Page<Produto> page = produtoRepository.findByLojista(lojista, pageable);

    return page.map(produto -> new ProdutoResponseDTO(
      produto.getId(),
      produto.getNome(),
      produto.getPreco(),
      produto.getDescricao(),
      produto.getImgUrl()
    ));
  }



  @Transactional
  public void deletar(Long id){
    String login = SecurityContextHolder.getContext()
      .getAuthentication()
      .getName();

    Lojista lojista = lojistaRepository.findByEmail(login);

    if (lojista == null) {
      throw new IllegalStateException("Usuário não encontrado");
    }

    Produto produto = produtoRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

    // Verifica se o produto pertence ao lojista logado
    if (!produto.getLojista().getId().equals(lojista.getId())) {
      throw new IllegalStateException("Você não tem permissão para excluir este produto");
    }

    produtoRepository.delete(produto);
  }


  private String salvarImagemLocal(MultipartFile file, String subpasta) {
    try {
      // valida mime
      String contentType = file.getContentType();
      if (contentType == null || !contentType.startsWith("image/")) {
        throw new IllegalArgumentException("Arquivo enviado não é uma imagem válida");
      }

      // diretórios
      Path baseDir = Paths.get(baseUploadDir).toAbsolutePath().normalize();
      Path uploadDir = baseDir.resolve(subpasta).normalize();
      Files.createDirectories(uploadDir);

      // nome do arquivo
      String original = file.getOriginalFilename();
      String limpo = (original == null ? "imagem"
        : Paths.get(original).getFileName().toString().replaceAll("[^a-zA-Z0-9._-]", "_"));
      String nomeArquivo = UUID.randomUUID() + "_" + limpo;

      Path destino = uploadDir.resolve(nomeArquivo);
      try (var in = file.getInputStream()) {
        Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
      }

      // URL pública
      return "/uploads/" + subpasta + "/" + nomeArquivo;
    } catch (IOException e) {
      throw new RuntimeException("Falha ao salvar imagem", e);
    }
  }

  @Transactional
  public Page<ProdutoResponseDTO> listarPorNomeLoja(String nomeLoja, Pageable pageable) {
    Lojista lojista = lojistaRepository.findByNome(nomeLoja);
    if (lojista == null) {
      throw new IllegalStateException("Loja não encontrada para o nome informado");
    }

    Page<Produto> page = produtoRepository.findByLojista(lojista, pageable);

    return page.map(produto -> new ProdutoResponseDTO(
      produto.getId(),
      produto.getNome(),
      produto.getPreco(),
      produto.getDescricao(),
      produto.getImgUrl()
    ));
  }
}
