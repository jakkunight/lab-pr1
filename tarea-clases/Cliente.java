/**
 * Cliente
 */
public class Cliente extends Persona implements Descuento {
  String idCliente;

  Cliente(String nombre, String apellido, String cedula, String idCliente) {
    super(nombre, apellido, cedula);
    this.idCliente = idCliente;
  }

  @Override
  public Double recuDescuento() {
    return 0.15;
  }
}
