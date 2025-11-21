package group.triade.catalogo.controller;

import group.triade.catalogo.dtos.produto.ProdutoRequestDTO;
import group.triade.catalogo.dtos.produto.ProdutoResponseDTO;
import group.triade.catalogo.services.ProdutoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

  @Autowired
  private ProdutoService produtoService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProdutoResponseDTO> criar(@ModelAttribute ProdutoRequestDTO dto) {
    ProdutoResponseDTO salvo = produtoService.criar(dto);
    return ResponseEntity.ok(salvo);
  }

  @GetMapping("/lojas/{nomeLoja}/produtos")
  public ResponseEntity<Page<ProdutoResponseDTO>> listarProdutosPorLoja(
    @PathVariable String nomeLoja,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "nome,asc") String sort
  ) {
    String[] sortParams = sort.split(",");
    String sortField = sortParams[0];
    String sortDirection = sortParams.length > 1 ? sortParams[1] : "asc";

    Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
      ? Sort.Direction.DESC
      : Sort.Direction.ASC;

    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

    Page<ProdutoResponseDTO> produtos = produtoService.listarPorNomeLoja(nomeLoja, pageable);

    return ResponseEntity.ok(produtos);
  }


  @PutMapping("/editar/{id}")
  public ResponseEntity<ProdutoResponseDTO> editar(
    @PathVariable Long id,
    @RequestBody ProdutoRequestDTO dto) {

    ProdutoResponseDTO resp = produtoService.editar(id, dto);
    return ResponseEntity.ok(resp);
  }


  @GetMapping("/meus")
  public ResponseEntity<Page<ProdutoResponseDTO>> obterProdutosDoLojista(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "nome,asc") String sort
  ) {
    String[] sortParams = sort.split(",");
    String sortField = sortParams[0];
    String sortDirection = sortParams.length > 1 ? sortParams[1] : "asc";

    Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
      ? Sort.Direction.DESC
      : Sort.Direction.ASC;

    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

    Page<ProdutoResponseDTO> produtos = produtoService.obterProdutosDoLojista(pageable);

    return ResponseEntity.ok(produtos);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletar(@PathVariable Long id) {
    produtoService.deletar(id);
    return ResponseEntity.ok("Produto deletado com sucesso");
  }



}
