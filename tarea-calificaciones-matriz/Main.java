import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);

    System.out.println("==== Reporte de Calificaciones de COLUMBIA ====");
    System.out.print("Ingresa el nro de alumnos: ");
    int alumnos = sc.nextInt();
    sc.nextLine();

    int[] totales = {20, 5, 5, 5, 5, 5, 5, 5, 5, 40}; // suma = 100

    Sistema sistema = new Sistema(alumnos, totales);

    for (int i = 0; i < alumnos; i++) {
      System.out.print("Nombre del alumno: ");
      String nombre = sc.nextLine();

      Alumno alumno = new Alumno(nombre);
      Registro registro = new Registro();
      registro.setAlumno(alumno);

      Calificacion[] calificaciones = new Calificacion[totales.length];

      for (int j = 0; j < totales.length; j++) {
        System.out.print("Ingrese puntaje (" + totales[j] + " max): ");
        int logrado = sc.nextInt();

        calificaciones[j] = new Calificacion(totales[j], logrado);
      }
      sc.nextLine();

      registro.setCalificaciones(calificaciones);
      sistema.registros[i] = registro;
    }

    System.out.println("===============================================");
    sistema.procesarResultados();
  }
}
