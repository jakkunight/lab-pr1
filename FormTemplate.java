import javax.swing.*;
import java.awt.*;

public class FormTemplate extends JFrame implements ActionListener {
  JLabel label_id, label_description;
  JTextField text_id, text_description;
  JPanel panel;

  public FormTemplate() {
    super("==== TITLE ====");
    // Resto de los elementos del formulario...

    panel = new JPanel();
    panel.setLayout(null);

    // Añadir los elementos previamente declarados
    // mediante `panel.add(<elemento>)`


    add(panel);
    setSize(800, 600);
    setVisible(true);
    defaultActionOnClose(EXIT_ON_CLOSE);
  }

}
