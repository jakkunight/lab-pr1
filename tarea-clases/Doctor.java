/**
 * Doctor
 */
public class Doctor extends Persona implements SueldoBase, Descuento{
  String idDoctor;

  Doctor(String nombre, String apellido, String cedula, String idDoctor) {
    super(nombre, apellido, cedula);
    this.idDoctor = idDoctor;
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
