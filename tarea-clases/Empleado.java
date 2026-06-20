/**
 * Empleado
 */
public class Empleado extends Persona implements SueldoBase, Descuento{
  String nroEmpleado;

  Empleado(String nombre, String apellido, String cedula, String nroEmpleado) {
    super(nombre, apellido, cedula);
    this.nroEmpleado = nroEmpleado;
  }

  @Override
  public Double recuDescuento() {
    return 0.1;
  }

  @Override
  public Integer recuSueldoBase() {
    return 20000;
  }
}
