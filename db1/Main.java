import java.sql.ResultSet;
import javax.swing.JOptionPane;

/** Main */
public class Main {
  public static boolean open(BDOracle db) {
    while (true) {
      try {
        db.abrir(
            JOptionPane.showInputDialog("Ingrese su nombre de usuario"),
            JOptionPane.showInputDialog("Ingrese su contraseña"));
        break;
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        if (JOptionPane.showConfirmDialog(null, "Desea intentarlo nuevamente?")
            == JOptionPane.NO_OPTION) {
          System.err.println("No se pudo instanciar la conexión a la base de datos.");
          return false;
        }
        continue;
      }
    }
    return true;
  }

  public static char readOption() {
    while (true) {
      String input =
          JOptionPane.showInputDialog(
              "Ingrese una inputción válida:\n"
                  + "1. Insertar calle\n"
                  + "2. Actualizar calle\n"
                  + "3. Eliminar calle\n"
                  + "0. Salir de la aplicación");
      char cond = input.charAt(input.length() - 1);
      if (cond != '1' && cond != '2' && cond != '3' && cond != '0') {
        JOptionPane.showMessageDialog(
            null, "Ha ingresado una opción inválida.\nIntentelo de nuevo.");
        continue;
      }
      return cond;
    }
  }

  // FIELDS:
  // - CALL_ID_CALLES
  // - CALL_DESCRIPCION
  // - CALL_ID_SECUENCIADOR
  public static void insertCalle(BDOracle db) {
    try {
      int id = 0;
      ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM calles");
      if (rs.next()) {
        id = rs.getInt("N");
      }

      String desc = JOptionPane.showInputDialog("Ingrese un nombre para la nueva calle");
      if (desc.equals("")) {
        JOptionPane.showMessageDialog(null, "Operación cancelada.");
        return;
      }
      db.sql(
          "INSERT INTO calles(CALL_ID_CALLES, CALL_DESCRIPCION, CALL_ID_SECUENCIADOR) VALUES ("
              + (id + 1)
              + ", '"
              + desc
              + "', "
              + "1)");
      JOptionPane.showMessageDialog(null, "Nueva calle insertada.");

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    }
  }

  public static void editCalle(BDOracle db) {
    try {
      ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM calles");
      int maxId = (rs.next() ? rs.getInt("N") : 0);
      if (maxId == 0) {
        JOptionPane.showMessageDialog(null, "No hay elemenmtos para editar.");
        return;
      }
      String input;
      while (true) {
        String out = "Ingrese el ID de calle para editar (1-" + maxId + ")";
        input = JOptionPane.showInputDialog(out);
        if (Conver.toInt(input) > maxId || Conver.toInt(input) < 1) {
          JOptionPane.showMessageDialog(
              null, "Ha ingresado una opción no válida. Vuelva a intentarlo.");
          continue;
        }
        break;
      }
      String current = "NULL";
      rs =
          db.sql(
              "SELECT CALL_ID_CALLES, CALL_DESCRIPCION FROM calles WHERE CALL_ID_CALLES = "
                  + input);
      rs.next();
      current = rs.getString("CALL_DESCRIPCION");

      String value =
          JOptionPane.showInputDialog(
              "Ingresa la descripción de la calle\nValor actual: " + current);
      if (value.equals("")) {
        JOptionPane.showMessageDialog(null, "Operación cancelada.");
        return;
      }
      db.sql(
          "UPDATE calles SET CALL_DESCRIPCION = '" + value + "' WHERE CALL_ID_CALLES = " + input);
      JOptionPane.showMessageDialog(null, "Calle actualizada");

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    }
  }

  public static void deleteCalle(BDOracle db) {
    try {
      ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM calles");
      int maxId = (rs.next() ? rs.getInt("N") : 0);
      if (maxId == 0) {
        JOptionPane.showMessageDialog(null, "No hay elemenmtos para eliminar.");
        return;
      }
      String input;
      while (true) {
        String out = "Ingrese el ID de calle para eliminar (1-" + maxId + ")";
        input = JOptionPane.showInputDialog(out);
        if (Conver.toInt(input) > maxId || Conver.toInt(input) < 1) {
          JOptionPane.showMessageDialog(
              null, "Ha ingresado una opción no válida. Vuelva a intentarlo.");
          continue;
        }
        break;
      }
      String current = "NULL";
      rs =
          db.sql(
              "SELECT CALL_ID_CALLES, CALL_DESCRIPCION FROM calles WHERE CALL_ID_CALLES = "
                  + input);
      rs.next();
      current = rs.getString("CALL_DESCRIPCION");

      int op =
          JOptionPane.showConfirmDialog(
              null,
              "Desea eliminar este elemento ("
                  + input
                  + ". "
                  + current
                  + ") de forma PERMANENTE?\n"
                  + "[ ADVERTENCIA ] ESTA ACCIÓN NO SE PUEDE DESHACER!");
      if (op == JOptionPane.CANCEL_OPTION || op == JOptionPane.NO_OPTION) {
        JOptionPane.showMessageDialog(null, "Operación cancelada.");
        return;
      }
      db.sql("DELETE FROM calles WHERE CALL_ID_CALLES = " + input);
      JOptionPane.showMessageDialog(null, "Calle eliminada permanentemente");

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    }
  }

  public static void main(String[] args) {
    BDOracle db = new BDOracle();
    if (!open(db)) {
      return;
    }
    while (true) {
      char op = readOption();
      if (op == '0') {
        return;
      }
      if (op == '1') {
        insertCalle(db);
      }
      if (op == '2') {
        editCalle(db);
      }
      if (op == '3') {
        deleteCalle(db);
      }
    }
  }
}
