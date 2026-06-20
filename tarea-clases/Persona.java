/**
 * Persona
 */
public abstract class Persona {

  String nombre, apellido, cedula;

  Persona(String nombre, String apellido, String cedula) {
    this.nombre = nombre;
    this.apellido = apellido;
    this.cedula = cedula;
  }
}
