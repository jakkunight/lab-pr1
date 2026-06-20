import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Acceso {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Acceso().mostrarDialogoLogin());
  }

  private void mostrarDialogoLogin() {
    BDOracle db = new BDOracle();
    JDialog loginDialog = new JDialog();
    loginDialog.setTitle("Conexión a la Base de Datos");
    loginDialog.setModal(true);
    loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    loginDialog.setSize(350, 180);
    loginDialog.setLocationRelativeTo(null);
    loginDialog.setResizable(false);

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    JLabel lblUser = new JLabel("Usuario:");
    JTextField txtUser = new JTextField(15);
    JLabel lblPass = new JLabel("Contraseña:");
    JPasswordField txtPass = new JPasswordField(15);
    JButton btnLogin = new JButton("Conectar");
    JButton btnCancel = new JButton("Salir");

    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(lblUser, gbc);
    gbc.gridx = 1;
    panel.add(txtUser, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(lblPass, gbc);
    gbc.gridx = 1;
    panel.add(txtPass, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    JPanel botonesPanel = new JPanel(new FlowLayout());
    botonesPanel.add(btnLogin);
    botonesPanel.add(btnCancel);
    panel.add(botonesPanel, gbc);

    loginDialog.add(panel);

    btnLogin.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String user = txtUser.getText().trim();
            String password = new String(txtPass.getPassword());
            if (user.isEmpty() || password.isEmpty()) {
              JOptionPane.showMessageDialog(
                  loginDialog,
                  "Debe ingresar usuario y contraseña.",
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
              return;
            }
            try {
              db.abrir(user, password);
              // Conexión exitosa
              loginDialog.dispose();
              new FormPrincipal(db);
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(
                  loginDialog,
                  "Error de conexión: " + ex.getLocalizedMessage(),
                  "Error",
                  JOptionPane.ERROR_MESSAGE);
              int opcion =
                  JOptionPane.showConfirmDialog(
                      loginDialog,
                      "¿Desea intentarlo nuevamente?",
                      "Reintentar",
                      JOptionPane.YES_NO_OPTION,
                      JOptionPane.QUESTION_MESSAGE);
              if (opcion != JOptionPane.YES_OPTION) {
                System.exit(0);
              } else {
                txtUser.setText("");
                txtPass.setText("");
                txtUser.requestFocus();
              }
            }
          }
        });

    btnCancel.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });

    loginDialog.setVisible(true);
  }
}
