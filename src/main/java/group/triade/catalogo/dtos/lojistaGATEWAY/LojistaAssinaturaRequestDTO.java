package group.triade.catalogo.dtos.lojistaGATEWAY;

public record LojistaAssinaturaRequestDTO(
  String nomeLoja,
  String emailResponsavel,
  String nomeResponsavel,
  String documento, //CPF OU CPNJ
  String planoCodigo //BASICO_MENSAL
) {
}
