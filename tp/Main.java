import javax.swing.JOptionPane;

/** Main */
public class Main {

  public static void main(String[] args) {
    bool conn = false;
    BDOracle db = new BDOracle();
    while (!conn) {
      try {
        db.abrir();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
        continue;
      }
    }
  }
}
