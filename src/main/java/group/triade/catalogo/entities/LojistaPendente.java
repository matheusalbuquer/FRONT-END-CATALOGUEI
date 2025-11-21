package group.triade.catalogo.entities;

import group.triade.catalogo.enums.Status;
import jakarta.persistence.*;

@Entity
public class LojistaPendente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String emailResponsavel;

  private String nomeLoja;

  private String plano;

  private String documento;

  private String externalReference;

  private String preferenceId;

  @Enumerated(EnumType.STRING)
  private Status status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmailResponsavel() {
    return emailResponsavel;
  }

  public void setEmailResponsavel(String emailResponsavel) {
    this.emailResponsavel = emailResponsavel;
  }

  public String getNomeLoja() {
    return nomeLoja;
  }

  public void setNomeLoja(String nomeLoja) {
    this.nomeLoja = nomeLoja;
  }

  public String getPlano() {
    return plano;
  }

  public void setPlano(String plano) {
    this.plano = plano;
  }

  public String getExternalReference() {
    return externalReference;
  }

  public void setExternalReference(String externalReference) {
    this.externalReference = externalReference;
  }

  public String getPreferenceId() {
    return preferenceId;
  }

  public void setPreferenceId(String preferenceId) {
    this.preferenceId = preferenceId;
  }

  public Status getStatus() {
    return status;
  }

  public String getDocumento() {
    return documento;
  }

  public void setDocumento(String documento) {
    this.documento = documento;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
