public class Calificacion {

  int total;
  int logrado;

  Calificacion(int total, int logrado) {

    if (total <= 0) {
      throw new RuntimeException("Total inválido.");
    }

    if (logrado < 0 || logrado > total) {
      throw new RuntimeException("Puntaje inválido.");
    }

    this.total = total;
    this.logrado = logrado;
  }
}
