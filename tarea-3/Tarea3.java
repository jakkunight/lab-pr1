import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Tarea3
 */
public class Tarea3 {

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    PrintWriter pw;
    try {
      System.out.println("Ingresa una letra:");
      char c = sc.nextLine().toUpperCase().charAt(0);
      pw = new PrintWriter("piramyd.txt");
      for (char x = c; x >= 'A'; x--) {

        for (char y = x; y >= 'A'; y--) {
          pw.print(y);
        }
        pw.println();
      }
      pw.flush();

    } catch (Exception e) {

      System.err.println(e.getLocalizedMessage());
    } finally {
      sc.close();
    }
  }
}
