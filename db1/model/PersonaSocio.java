package model;

import java.util.Date;

public class PersonaSocio {

  private Integer nroSocio;

  private Long codPersona;

  private Date ingreso;

  private Integer situacionSocio;

  private Integer activo;

  private Integer idSecuenciadorTabla;

  private Integer nroMesa;

  private Integer nroSolicitudIngreso;

  public PersonaSocio(
      Integer nroSocio,
      Long codPersona,
      Date ingreso,
      Integer situacionSocio,
      Integer activo,
      Integer idSecuenciadorTabla,
      Integer nroMesa,
      Integer nroSolicitudIngreso) {

    this.nroSocio = nroSocio;

    this.codPersona = codPersona;

    this.ingreso = ingreso;

    this.situacionSocio = situacionSocio;

    this.activo = activo;

    this.idSecuenciadorTabla = idSecuenciadorTabla;

    this.nroMesa = nroMesa;

    this.nroSolicitudIngreso = nroSolicitudIngreso;
  }

  public Integer getNroSocio() {
    return nroSocio;
  }

  public void setNroSocio(Integer nroSocio) {
    this.nroSocio = nroSocio;
  }

  public Long getCodPersona() {
    return codPersona;
  }

  public void setCodPersona(Long codPersona) {
    this.codPersona = codPersona;
  }

  public Date getIngreso() {
    return ingreso;
  }

  public void setIngreso(Date ingreso) {
    this.ingreso = ingreso;
  }

  public Integer getSituacionSocio() {
    return situacionSocio;
  }

  public void setSituacionSocio(Integer situacionSocio) {
    this.situacionSocio = situacionSocio;
  }

  public Integer getActivo() {
    return activo;
  }

  public void setActivo(Integer activo) {
    this.activo = activo;
  }

  public Integer getIdSecuenciadorTabla() {
    return idSecuenciadorTabla;
  }

  public void setIdSecuenciadorTabla(Integer idSecuenciadorTabla) {
    this.idSecuenciadorTabla = idSecuenciadorTabla;
  }

  public Integer getNroMesa() {
    return nroMesa;
  }

  public void setNroMesa(Integer nroMesa) {
    this.nroMesa = nroMesa;
  }

  public Integer getNroSolicitudIngreso() {
    return nroSolicitudIngreso;
  }

  public void setNroSolicitudIngreso(Integer nroSolicitudIngreso) {
    this.nroSolicitudIngreso = nroSolicitudIngreso;
  }

  // constructor
}
