package model;

public class Calle {

    private Integer idCalle;
    private String descripcion;
    private Integer idSecuenciador;

    public Calle() {}

    public Calle(
        Integer idCalle,
        String descripcion,
        Integer idSecuenciador
    ) {

        this.idCalle = idCalle;
        this.descripcion = descripcion;
        this.idSecuenciador = idSecuenciador;
    }

    public Integer getIdCalle() {
        return idCalle;
    }

    public void setIdCalle(Integer idCalle) {
        this.idCalle = idCalle;
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
