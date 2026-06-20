/** App */
public class App extends JPanel {

  static void update(String message, Model model) {}

  static JComponent view(Model model) {
    JComponent a = new JPanel();
    return a;
  }

  public static void main(String[] args) {
    Model model = new Model();
    view(model);
    while (!model.status.equals("DONE")) {}
  }
}
