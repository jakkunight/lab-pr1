package model;

import java.math.BigDecimal;
import java.util.Date;

public class Funcionario {

  private Integer nroFuncionario;

  private Date fechaIngreso;

  private String ips;

  private Integer hijosMenores;

  private Integer hijosMayores;

  private Long codPersona;

  private Integer activo;

  private BigDecimal incentivo;

  private BigDecimal montoAnticipo;

  public Funcionario() {}

  public Integer getNroFuncionario() {
    return nroFuncionario;
  }

  public void setNroFuncionario(Integer nroFuncionario) {
    this.nroFuncionario = nroFuncionario;
  }

  public Date getFechaIngreso() {
    return fechaIngreso;
  }

  public void setFechaIngreso(Date fechaIngreso) {
    this.fechaIngreso = fechaIngreso;
  }

  public String getIps() {
    return ips;
  }

  public void setIps(String ips) {
    this.ips = ips;
  }

  public Integer getHijosMenores() {
    return hijosMenores;
  }

  public void setHijosMenores(Integer hijosMenores) {
    this.hijosMenores = hijosMenores;
  }

  public Integer getHijosMayores() {
    return hijosMayores;
  }

  public void setHijosMayores(Integer hijosMayores) {
    this.hijosMayores = hijosMayores;
  }

  public Long getCodPersona() {
    return codPersona;
  }

  public void setCodPersona(Long codPersona) {
    this.codPersona = codPersona;
  }

  public Integer getActivo() {
    return activo;
  }

  public void setActivo(Integer activo) {
    this.activo = activo;
  }

  public BigDecimal getIncentivo() {
    return incentivo;
  }

  public void setIncentivo(BigDecimal incentivo) {
    this.incentivo = incentivo;
  }

  public BigDecimal getMontoAnticipo() {
    return montoAnticipo;
  }

  public void setMontoAnticipo(BigDecimal montoAnticipo) {
    this.montoAnticipo = montoAnticipo;
  }

  // constructor
  // getters
  // setters
}
