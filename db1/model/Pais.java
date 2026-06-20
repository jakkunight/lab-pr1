package model;

public class Pais {

    private Integer idPais;
    private String descripcion;
    private Integer idSecuenciador;

    public Pais() {}

    public Pais(
        Integer idPais,
        String descripcion,
        Integer idSecuenciador
    ) {

        this.idPais = idPais;
        this.descripcion = descripcion;
        this.idSecuenciador = idSecuenciador;
    }

    public Integer getIdPais() {
        return idPais;
    }

    public void setIdPais(Integer idPais) {
        this.idPais = idPais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdSecuenciador() {
        return idSecuenciador;
    }

    public void setIdSecuenciador(Integer idSecuenciador) {
        this.idSecuenciador = idSecuenciador;
    }
}
