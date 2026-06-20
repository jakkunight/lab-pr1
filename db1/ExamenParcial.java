import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Scanner;

/** ExamenParcial */
public class ExamenParcial {

  private static Conectar conn;
  private static String file = "datos.txt";

  public static void main(String[] args) {
    // Leer usuario y contraseña:
    conn = new ConectarOracle();
    String user = "";
    String password = "";
    Scanner sc = new Scanner(System.in);

    try {
      System.out.println("Ingrese el usuario de la base de datos:");
      user = sc.nextLine();
      System.out.println("Ingrese su contraseña:");
      password = sc.nextLine();
      conn.abrir(user, password);
      leerBarrio();
      sc = new Scanner(new File(file));
      String barrio = sc.nextLine();
      insertarBarrios(barrio);
      ResultSet rs = recuBarrios();
      while (rs.next()) {
        System.out.println(rs.getString("BARR_DESCRIPCION"));
      }

    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }
  }

  private static void leerBarrio() {
    System.out.println("Ingresa tu barrio: ");
    Scanner sc = new Scanner(System.in);

    try {
      PrintWriter pw = new PrintWriter(new File(file));
      String barrio = sc.nextLine();
      pw.println(barrio);
      pw.flush();
      System.out.println("Procesando...");
      pw.close();
    } catch (Exception e) {
      System.out.println(e.getLocalizedMessage());
    }
    sc.close();
  }

  private static ResultSet recuBarrios() throws Exception {
    return conn.sql("SELECT BARR_DESCRIPCION FROM BARRIOS ORDER BY 1 DESC");
  }

  private static ResultSet insertarBarrios(String barrio) throws Exception {
    ResultSet rs = conn.sql("SELECT COUNT(*) AS C FROM BARRIOS");
    int id = 1;
    if (rs.next()) {
      id += rs.getInt("C");
    }
    String sql =
        "INSERT INTO BARRIOS(BARR_ID_BARRIO, BARR_DESCRIPCION, BARR_ID_SECUENCIADOR) VALUES("
            + id
            + ", '"
            + barrio
            + "', 6)";
    return conn.sql(sql);
  }
}
