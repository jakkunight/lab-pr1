public class Sistema {
  Registro[] registros;

  Sistema(int alumnos, int[] totales) {

    if (alumnos <= 0) {
      throw new RuntimeException("Debe haber por lo menos un alumno.");
    }

    int suma = 0;
    for (int t : totales) suma += t;

    if (suma != 100) {
      throw new RuntimeException("El total debe ser 100.");
    }

    registros = new Registro[alumnos];
  }

  public void procesarResultados() {
    int max = -1;

    // Calcular máximos
    for (Registro r : registros) {
      int total = calcularTotal(r);
      if (total > max) {
        max = total;
      }
    }

    // Cuadro de honor
    System.out.println("#### Cuadro de Honor ####");
    for (Registro r : registros) {
      int total = calcularTotal(r);
      if (total == max) {
        System.out.println(r.getAlumno().nombre + " - " + total);
      }
    }

    // Lista de alerta
    System.out.println("**** Lista de Alerta ****");
    for (Registro r : registros) {
      int total = calcularTotal(r);
      if (total < 60) {
        System.out.println(r.getAlumno().nombre + " - " + total);
      }
    }
  }

  private int calcularTotal(Registro r) {
    int suma = 0;
    for (Calificacion c : r.getCalificaciones()) {
      suma += c.logrado;
    }
    return suma;
  }
}
