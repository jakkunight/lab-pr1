package model;

import java.util.Date;

public class Persona {

  private Long codPersona;

  private String serieCi;

  private Integer ci;

  private String nombre;

  private String apellido;

  private Date fechaNacimiento;

  private Integer estadoCivil;

  private Integer sexo;

  private Integer idCiudad;

  private Integer idPais;

  private Integer idSecuenciador;

  private Integer nroFuncionario;

  private Date fechaUsuario;

  private Integer idSucursal;

  private String ruc;

  public Persona() {}

  // constructor completo

  public Persona(
      Long codPersona,
      String serieCi,
      Integer ci,
      String nombre,
      String apellido,
      Date fechaNacimiento,
      Integer estadoCivil,
      Integer sexo,
      Integer idCiudad,
      Integer idPais,
      Integer idSecuenciador,
      Integer nroFuncionario,
      Date fechaUsuario,
      Integer idSucursal,
      String ruc) {

    this.codPersona = codPersona;
    this.serieCi = serieCi;
    this.ci = ci;
    this.nombre = nombre;
    this.apellido = apellido;
    this.fechaNacimiento = fechaNacimiento;
    this.estadoCivil = estadoCivil;
    this.sexo = sexo;
    this.idCiudad = idCiudad;
    this.idPais = idPais;
    this.idSecuenciador = idSecuenciador;
    this.nroFuncionario = nroFuncionario;
    this.fechaUsuario = fechaUsuario;
    this.idSucursal = idSucursal;
    this.ruc = ruc;
  }

  public Long getCodPersona() {
    return codPersona;
  }

  public void setCodPersona(Long codPersona) {
    this.codPersona = codPersona;
  }

  public String getSerieCi() {
    return serieCi;
  }

  public void setSerieCi(String serieCi) {
    this.serieCi = serieCi;
  }

  public Integer getCi() {
    return ci;
  }

  public void setCi(Integer ci) {
    this.ci = ci;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

  public Date getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(Date fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public Integer getEstadoCivil() {
    return estadoCivil;
  }

  public void setEstadoCivil(Integer estadoCivil) {
    this.estadoCivil = estadoCivil;
  }

  public Integer getSexo() {
    return sexo;
  }

  public void setSexo(Integer sexo) {
    this.sexo = sexo;
  }

  public Integer getIdCiudad() {
    return idCiudad;
  }

  public void setIdCiudad(Integer idCiudad) {
    this.idCiudad = idCiudad;
  }

  public Integer getIdPais() {
    return idPais;
  }

  public void setIdPais(Integer idPais) {
    this.idPais = idPais;
  }

  public Integer getIdSecuenciador() {
    return idSecuenciador;
  }

  public void setIdSecuenciador(Integer idSecuenciador) {
    this.idSecuenciador = idSecuenciador;
  }

  public Integer getNroFuncionario() {
    return nroFuncionario;
  }

  public void setNroFuncionario(Integer nroFuncionario) {
    this.nroFuncionario = nroFuncionario;
  }

  public Date getFechaUsuario() {
    return fechaUsuario;
  }

  public void setFechaUsuario(Date fechaUsuario) {
    this.fechaUsuario = fechaUsuario;
  }

  public Integer getIdSucursal() {
    return idSucursal;
  }

  public void setIdSucursal(Integer idSucursal) {
    this.idSucursal = idSucursal;
  }

  public String getRuc() {
    return ruc;
  }

  public void setRuc(String ruc) {
    this.ruc = ruc;
  }

  // getters y setters
}
