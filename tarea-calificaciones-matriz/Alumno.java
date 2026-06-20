public class Alumno {

  String nombre;

  Alumno(String nombre) {
    if (nombre == null || nombre.isBlank()) {
      throw new RuntimeException("Nombre inválido.");
    }
    this.nombre = nombre;
  }
}
