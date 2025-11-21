package group.triade.catalogo.dtos.lojistaGATEWAY;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MercadoPagoPreferenceData(
  @JsonProperty("id") String preferenceId,
  @JsonProperty("init_point") String checkoutUrl,
  @JsonProperty("sandbox_init_point") String sandboxCheckoutUrl
) {}
