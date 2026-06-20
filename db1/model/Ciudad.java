package model;

public class Ciudad {

    private Integer idCiudad;
    private String descripcion;
    private Integer idSecuenciador;

    public Ciudad() {}

    public Ciudad(
        Integer idCiudad,
        String descripcion,
        Integer idSecuenciador
    ) {

        this.idCiudad = idCiudad;
        this.descripcion = descripcion;
        this.idSecuenciador = idSecuenciador;
    }

    public Integer getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Integer idCiudad) {
        this.idCiudad = idCiudad;
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
