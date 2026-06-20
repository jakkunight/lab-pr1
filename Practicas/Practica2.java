import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Practica2 {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    PrintWriter printer = null;
    try {
      printer = new PrintWriter("datos.txt");
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }
    System.out.println("Hello World!");
    printer.println("Hoy es viernes, y Marito lo sabe!");
    printer.flush();

    sc.close();
    try {
      sc = new Scanner(new File("datos.txt"));
      System.out.println(sc.nextLine());

    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
    }
    printer.close();
    sc.close();
  }
}
