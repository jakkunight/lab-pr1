package model;

public class Barrio {

    private Integer idBarrio;
    private String descripcion;
    private Integer idSecuenciador;

    public Barrio() {}

    public Barrio(
        Integer idBarrio,
        String descripcion,
        Integer idSecuenciador
    ) {

        this.idBarrio = idBarrio;
        this.descripcion = descripcion;
        this.idSecuenciador = idSecuenciador;
    }

    public Integer getIdBarrio() {
        return idBarrio;
    }

    public void setIdBarrio(Integer idBarrio) {
        this.idBarrio = idBarrio;
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
