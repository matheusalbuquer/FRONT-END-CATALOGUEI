package group.triade.catalogo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import group.triade.catalogo.dtos.lojistaGATEWAY.MercadoPagoPreferenceData;
import group.triade.catalogo.entities.LojistaPendente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MercadoPagoService {

  private final WebClient webClient;

  @Value("${mercadopago.access-token}")
  private String accessToken;

  public MercadoPagoService(WebClient.Builder builder) {
    this.webClient = builder
      .baseUrl("https://api.mercadopago.com")
      .build();
  }

  public MercadoPagoPreferenceData criarPreferenciaAssinatura(LojistaPendente lojista) {

    Map<String, Object> body = new HashMap<>();

    Map<String, Object> item = new HashMap<>();
    item.put("title", "Plano mensal - " + lojista.getNomeLoja());
    item.put("quantity", 1);
    item.put("currency_id", "BRL");
    item.put("unit_price", 1.00);

    body.put("items", List.of(item));

    Map<String, String> backUrls = new HashMap<>();
    backUrls.put("success", "http://localhost:5173/sucesso");
    backUrls.put("failure", "http://localhost:5173/erro");
    backUrls.put("pending", "http://localhost:5173/pendente");
    body.put("back_urls", backUrls);

    // 🔴 NÃO usar auto_return em localhost
    // body.put("auto_return", "approved");
// 🔥 LOG DO JSON SENDO ENVIADO 🔥
    try {
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(body);
      System.out.println("===== JSON ENVIADO AO MERCADO PAGO =====");
      System.out.println(json);
      System.out.println("========================================");
    } catch (Exception e) {
      e.printStackTrace();
    }


    try {
      return webClient.post()
        .uri("/checkout/preferences")
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(MercadoPagoPreferenceData.class)
        .block();

    } catch (WebClientResponseException e) {
      System.out.println("==== ERRO MERCADO PAGO ====");
      System.out.println("Status: " + e.getStatusCode());
      System.out.println("Body: " + e.getResponseBodyAsString());
      System.out.println("==== FIM ERRO MERCADO PAGO ====");
      throw e;
    }
  }

}
