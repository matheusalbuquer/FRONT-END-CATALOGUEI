package group.triade.catalogo.services;

import group.triade.catalogo.dtos.lojistaGATEWAY.CheckoutResponseDTO;
import group.triade.catalogo.dtos.lojistaGATEWAY.LojistaAssinaturaRequestDTO;
import group.triade.catalogo.dtos.lojistaGATEWAY.MercadoPagoPreferenceData;
import group.triade.catalogo.entities.Lojista;
import group.triade.catalogo.entities.LojistaPendente;
import group.triade.catalogo.enums.Status;
import group.triade.catalogo.repositories.LojistaPendenteRepository;
import group.triade.catalogo.repositories.LojistaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AssinaturaLojistaService {

  @Autowired
  private LojistaRepository lojistaRepository;

  @Autowired
  private LojistaPendenteRepository lojistaPendenteRepository;

  @Autowired
  private MercadoPagoService mercadoPagoService;

  @Transactional
  public CheckoutResponseDTO iniciarAssinatura(LojistaAssinaturaRequestDTO dto) {

    LojistaPendente pendente = new LojistaPendente();
    pendente.setNomeLoja(dto.nomeLoja());
    pendente.setEmailResponsavel(dto.emailResponsavel());
    pendente.setNomeLoja(dto.nomeResponsavel());
    pendente.setDocumento(dto.documento());
    pendente.setPlano(dto.planoCodigo());
    pendente.setStatus(Status.PENDENTE);

    String externalRef = "lojista-" + UUID.randomUUID();
    pendente.setExternalReference(externalRef);

    pendente = lojistaPendenteRepository.save(pendente);

    // chama Mercado Pago de verdade
    MercadoPagoPreferenceData pref =
      mercadoPagoService.criarPreferenciaAssinatura(pendente);

    pendente.setPreferenceId(pref.preferenceId());
    lojistaPendenteRepository.save(pendente);

    return new CheckoutResponseDTO(pref.checkoutUrl());
  }

  @Transactional
  public void confirmarPagamentoAprovado(String externalReference) {

    LojistaPendente pendente = lojistaPendenteRepository
      .findByExternalReference(externalReference)
      .orElseThrow(() -> new IllegalArgumentException("Pré-cadastro não encontrado"));

    if (pendente.getStatus() == Status.APROVADO) {
      return;
    }

    Lojista lojista = new Lojista();
    lojista.setNome(pendente.getNomeLoja());
    lojista.setEmail(pendente.getEmailResponsavel());
    // ... outros campos

    lojistaRepository.save(lojista);

    pendente.setStatus(Status.APROVADO);
    lojistaPendenteRepository.save(pendente);
  }
}
