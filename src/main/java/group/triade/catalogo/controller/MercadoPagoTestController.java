package group.triade.catalogo.controller;

import group.triade.catalogo.entities.LojistaPendente;
import group.triade.catalogo.services.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mp")
public class MercadoPagoTestController {

  @Autowired
  private MercadoPagoService mercadoPagoService;

  @GetMapping("/test")
  public ResponseEntity<?> test() {
    // cria um LojistaPendente fake só pra teste
    LojistaPendente lp = new LojistaPendente();
    lp.setNomeLoja("Loja Teste");
    lp.setExternalReference("teste-123"); // qualquer string

    var pref = mercadoPagoService.criarPreferenciaAssinatura(lp);
    return ResponseEntity.ok(pref);
  }
}
