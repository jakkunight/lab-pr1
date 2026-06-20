// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.sql.ResultSet;
// import javax.swing.JButton;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JOptionPane;
// import javax.swing.JTextField;

// public class FormCalle extends JFrame implements ActionListener {
//   // Componentes de la interfaz
//   private JLabel lblId, lblDescripcion;
//   private JTextField txtId, txtDescripcion;
//   private JButton btnAgregar, btnActualizar, btnEliminar, btnBuscar;
//   // Conexión a la base de datos
//   private BDOracle db = null;

//   public FormCalle() {
//     // Configuración de la ventana
//     setTitle("Gestión de Calles");
//     setSize(500, 300);
//     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//     setLayout(null); // Sin layout, posicionamiento manual
//     setLocationRelativeTo(null); // Centrar ventana

//     // Inicializar componentes
//     initComponents();
//     // Establecer posiciones y tamaños manualmente
//     setBoundsComponents();
//     // Conectar a la base de datos
//     conectarBD();
//     // Hacer visible la ventana
//     setVisible(true);
//   }

//   private void initComponents() {
//     // Etiquetas␍
//     lblId = new JLabel("ID Calle:");
//     lblDescripcion = new JLabel("Descripción:");
//     // Campos de texto␍
//     txtId = new JTextField();
//     txtDescripcion = new JTextField();

//     // Botones
//     btnAgregar = new JButton("Agregar");
//     btnActualizar = new JButton("Actualizar");
//     btnEliminar = new JButton("Eliminar");
//     btnBuscar = new JButton("Buscar");

//     // Agregar ActionListener a los botones␍
//     btnAgregar.addActionListener(this);
//     btnActualizar.addActionListener(this);
//     btnEliminar.addActionListener(this);
//     btnBuscar.addActionListener(this);

//     // Agregar componentes al JFrame␍
//     add(lblId);
//     add(txtId);
//     add(lblDescripcion);
//     add(txtDescripcion);
//     add(btnAgregar);
//     add(btnActualizar);
//     add(btnEliminar);
//     add(btnBuscar);
//   }

//   private void setBoundsComponents() {
//     // Posiciones y tamaños manuales (x, y, width, height)␍
//     lblId.setBounds(30, 30, 80, 25);
//     txtId.setBounds(120, 30, 150, 25);

//     lblDescripcion.setBounds(30, 70, 80, 25);
//     txtDescripcion.setBounds(120, 70, 250, 25);

//     btnBuscar.setBounds(290, 30, 100, 25);

//     btnAgregar.setBounds(30, 130, 100, 30);
//     btnActualizar.setBounds(150, 130, 110, 30);
//     btnEliminar.setBounds(280, 130, 100, 30);
//   }

//   private void conectarBD() {
//     if (db == null) {
//       // No DB driver passed.
//       return;
//     }
//     while (true) {
//       try {
//         String user = JOptionPane.showInputDialog(this, "Ingrese su nombre de usuario:");
//         String password = JOptionPane.showInputDialog(this, "Ingrese su contraseña:");
//         db.abrir(user, password);
//         break;
//       } catch (Exception e) {
//         JOptionPane.showMessageDialog(this, e.getLocalizedMessage());
//         int opcion = JOptionPane.showConfirmDialog(this, "¿Desea intentarlo nuevamente?");
//         if (opcion == JOptionPane.NO_OPTION) {
//           System.err.println("No se pudo conectar a la base de datos.");
//           System.exit(0);
//         }
//       }
//     }
//   }

//   @Override
//   public void actionPerformed(ActionEvent e) {
//     if (e.getSource() == btnAgregar) {
//       agregarCalle();
//     } else if (e.getSource() == btnActualizar) {
//       actualizarCalle();
//     } else if (e.getSource() == btnEliminar) {
//       eliminarCalle();
//     } else if (e.getSource() == btnBuscar) {
//       buscarCalle();
//     }
//   }

//   // Método para agregar una nueva calle (INSERT)␍
//   private void agregarCalle() {
//     try {
//       String descripcion = txtDescripcion.getText().trim();
//       if (descripcion.isEmpty()) {
//         JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.");
//         return;
//       }

//       // Obtener el siguiente ID disponible␍
//       ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM calles");
//       int id = 0;
//       if (rs.next()) {
//         id = rs.getInt("N");
//       }
//       id = id + 1;

//       // Ejecutar INSERT␍
//       db.sql(
//           "INSERT INTO calles(CALL_ID_CALLES, CALL_DESCRIPCION, CALL_ID_SECUENCIADOR) VALUES ("
//               + id
//               + ", '"
//               + descripcion
//               + "', 1)");

//       JOptionPane.showMessageDialog(this, "Calle agregada exitosamente (ID: " + id + ")");
//       limpiarCampos();
//     } catch (Exception e) {
//       JOptionPane.showMessageDialog(this, "Error al agregar: " + e.getLocalizedMessage());
//     }
//   }

//   // Método para actualizar una calle existente (UPDATE)␍
//   private void actualizarCalle() {
//     try {
//       String idTexto = txtId.getText().trim();
//       if (idTexto.isEmpty()) {
//         JOptionPane.showMessageDialog(this, "Ingrese el ID de la calle a actualizar.");
//         return;
//       }

//       int id = Integer.parseInt(idTexto);
//       String nuevaDescripcion = txtDescripcion.getText().trim();
//       if (nuevaDescripcion.isEmpty()) {
//         JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.");
//         return;
//       }

//       // Verificar si el ID existe␍
//       ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM calles WHERE CALL_ID_CALLES = " + id);
//       rs.next();
//       if (rs.getInt("N") == 0) {
//         JOptionPane.showMessageDialog(this, "No existe una calle con ID: " + id);
//         return;
//       }
//       // Ejecutar UPDATE␍
//       db.sql(
//           "UPDATE calles SET CALL_DESCRIPCION = '"
//               + nuevaDescripcion
//               + "' WHERE CALL_ID_CALLES = "
//               + id);

//       JOptionPane.showMessageDialog(this, "Calle actualizada correctamente.");
//       limpiarCampos();
//     } catch (NumberFormatException e) {
//       JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
//     } catch (Exception e) {
//       JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getLocalizedMessage());
//     }
//   }

//   // Método para eliminar una calle (DELETE)␍
//   private void eliminarCalle() {
//     try {
//       String idTexto = txtId.getText().trim();
//       if (idTexto.isEmpty()) {
//         JOptionPane.showMessageDialog(this, "Ingrese el ID de la calle a eliminar.");
//         return;
//       }

//       int id = Integer.parseInt(idTexto);

//       // Obtener la descripción actual para mostrarla en la confirmación␍
//       ResultSet rs = db.sql("SELECT CALL_DESCRIPCION FROM calles WHERE CALL_ID_CALLES = " + id);
//       if (!rs.next()) {
//         JOptionPane.showMessageDialog(this, "No existe una calle con ID: " + id);
//         return;
//       }
//       String descripcion = rs.getString("CALL_DESCRIPCION");

//       // Confirmar eliminación␍
//       int opcion =
//           JOptionPane.showConfirmDialog(
//               this,
//               "¿Desea eliminar la calle?\nID: "
//                   + id
//                   + "\nDescripción: "
//                   + descripcion
//                   + "\n\n[ADVERTENCIA] Esta acción no se puede deshacer.",
//               "Confirmar eliminación",
//               JOptionPane.YES_NO_OPTION);

//       if (opcion != JOptionPane.YES_OPTION) {
//         JOptionPane.showMessageDialog(this, "Operación cancelada.");
//         return;
//       }

//       // Ejecutar DELETE␍
//       db.sql("DELETE FROM calles WHERE CALL_ID_CALLES = " + id);

//       JOptionPane.showMessageDialog(this, "Calle eliminada permanentemente.");
//       limpiarCampos();
//     } catch (NumberFormatException e) {
//       JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
//     } catch (Exception e) {
//       JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getLocalizedMessage());
//     }
//   }

//   // Método para buscar una calle por ID (SELECT)␍
//   private void buscarCalle() {
//     try {
//       String idTexto = txtId.getText().trim();
//       if (idTexto.isEmpty()) {
//         JOptionPane.showMessageDialog(this, "Ingrese el ID de la calle a buscar.");
//         return;
//       }

//       int id = Integer.parseInt(idTexto);

//       // Ejecutar SELECT usando el método sql() de BDOracle␍
//       ResultSet rs =
//           db.sql(
//               "SELECT CALL_ID_CALLES, CALL_DESCRIPCION FROM calles WHERE CALL_ID_CALLES = " +
// id);

//       if (rs.next()) {
//         String descripcion = rs.getString("CALL_DESCRIPCION");
//         txtDescripcion.setText(descripcion);
//         JOptionPane.showMessageDialog(
//             this, "Calle encontrada.\nID: " + id + "\nDescripción: " + descripcion);
//       } else {
//         JOptionPane.showMessageDialog(this, "No existe una calle con ID: " + id);
//         txtDescripcion.setText("");
//       }
//     } catch (NumberFormatException e) {
//       JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
//     } catch (Exception e) {
//       JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getLocalizedMessage());
//     }
//   }

//   private void limpiarCampos() {
//     txtId.setText("");
//     txtDescripcion.setText("");
//     txtId.requestFocus();
//   }

//   // Método main para ejecutar el formulario␍
//   public static void main(String[] args) {
//     new FormCalle();
//   }
// }
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.*;

public class FormCalle extends JFrame implements ActionListener {
  private JLabel lblId, lblDescripcion;
  private JTextField txtId, txtDescripcion;
  private JButton btnAgregar, btnActualizar, btnEliminar, btnBuscar;
  private BDOracle db;

  public FormCalle(BDOracle db) {
    this.db = db;
    setTitle("Gestión de Calles");
    setSize(500, 300);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(null);
    setLocationRelativeTo(null);
    initComponents();
    setBoundsComponents();
    setVisible(true);
  }

  private void initComponents() {
    lblId = new JLabel("ID Calle:");
    lblDescripcion = new JLabel("Descripción:");
    txtId = new JTextField();
    txtDescripcion = new JTextField();

    btnAgregar = new JButton("Agregar");
    btnActualizar = new JButton("Actualizar");
    btnEliminar = new JButton("Eliminar");
    btnBuscar = new JButton("Buscar");

    btnAgregar.addActionListener(this);
    btnActualizar.addActionListener(this);
    btnEliminar.addActionListener(this);
    btnBuscar.addActionListener(this);

    add(lblId);
    add(txtId);
    add(lblDescripcion);
    add(txtDescripcion);
    add(btnAgregar);
    add(btnActualizar);
    add(btnEliminar);
    add(btnBuscar);
  }

  private void setBoundsComponents() {
    lblId.setBounds(30, 30, 80, 25);
    txtId.setBounds(120, 30, 150, 25);
    lblDescripcion.setBounds(30, 70, 80, 25);
    txtDescripcion.setBounds(120, 70, 250, 25);
    btnBuscar.setBounds(290, 30, 100, 25);
    btnAgregar.setBounds(30, 130, 100, 30);
    btnActualizar.setBounds(150, 130, 110, 30);
    btnEliminar.setBounds(280, 130, 100, 30);
    btnCerrar = new JButton("Cerrar");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btnAgregar) {
      agregarCalle();
    } else if (e.getSource() == btnActualizar) {
      actualizarCalle();
    } else if (e.getSource() == btnEliminar) {
      eliminarCalle();
    } else if (e.getSource() == btnBuscar) {
      buscarCalle();
    }
  }

  private void agregarCalle() {
    try {
      String descripcion = txtDescripcion.getText().trim();
      if (descripcion.isEmpty()) {
        JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.");
        return;
      }
      ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM calles");
      int id = 0;
      if (rs.next()) {
        id = rs.getInt("N");
      }
      id = id + 1;
      db.sql(
          "INSERT INTO calles(CALL_ID_CALLES, CALL_DESCRIPCION, CALL_ID_SECUENCIADOR) VALUES ("
              + id
              + ", '"
              + descripcion
              + "', 1)");
      JOptionPane.showMessageDialog(this, "Calle agregada exitosamente (ID: " + id + ")");
      limpiarCampos();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error al agregar: " + e.getLocalizedMessage());
    }
  }

  private void actualizarCalle() {
    try {
      String idTexto = txtId.getText().trim();
      if (idTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el ID de la calle a actualizar.");
        return;
      }
      int id = Integer.parseInt(idTexto);
      String nuevaDescripcion = txtDescripcion.getText().trim();
      if (nuevaDescripcion.isEmpty()) {
        JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.");
        return;
      }
      ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM calles WHERE CALL_ID_CALLES = " + id);
      rs.next();
      if (rs.getInt("N") == 0) {
        JOptionPane.showMessageDialog(this, "No existe una calle con ID: " + id);
        return;
      }
      db.sql(
          "UPDATE calles SET CALL_DESCRIPCION = '"
              + nuevaDescripcion
              + "' WHERE CALL_ID_CALLES = "
              + id);
      JOptionPane.showMessageDialog(this, "Calle actualizada correctamente.");
      limpiarCampos();
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getLocalizedMessage());
    }
  }

  private void eliminarCalle() {
    try {
      String idTexto = txtId.getText().trim();
      if (idTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el ID de la calle a eliminar.");
        return;
      }
      int id = Integer.parseInt(idTexto);
      ResultSet rs = db.sql("SELECT CALL_DESCRIPCION FROM calles WHERE CALL_ID_CALLES = " + id);
      if (!rs.next()) {
        JOptionPane.showMessageDialog(this, "No existe una calle con ID: " + id);
        return;
      }
      String descripcion = rs.getString("CALL_DESCRIPCION");
      int opcion =
          JOptionPane.showConfirmDialog(
              this,
              "¿Desea eliminar la calle?\nID: "
                  + id
                  + "\nDescripción: "
                  + descripcion
                  + "\n\n[ADVERTENCIA] Esta acción no se puede deshacer.",
              "Confirmar eliminación",
              JOptionPane.YES_NO_OPTION);
      if (opcion != JOptionPane.YES_OPTION) {
        JOptionPane.showMessageDialog(this, "Operación cancelada.");
        return;
      }
      db.sql("DELETE FROM calles WHERE CALL_ID_CALLES = " + id);
      JOptionPane.showMessageDialog(this, "Calle eliminada permanentemente.");
      limpiarCampos();
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getLocalizedMessage());
    }
  }

  private void buscarCalle() {
    try {
      String idTexto = txtId.getText().trim();
      if (idTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el ID de la calle a buscar.");
        return;
      }
      int id = Integer.parseInt(idTexto);
      ResultSet rs =
          db.sql(
              "SELECT CALL_ID_CALLES, CALL_DESCRIPCION FROM calles WHERE CALL_ID_CALLES = " + id);
      if (rs.next()) {
        String descripcion = rs.getString("CALL_DESCRIPCION");
        txtDescripcion.setText(descripcion);
        JOptionPane.showMessageDialog(
            this, "Calle encontrada.\nID: " + id + "\nDescripción: " + descripcion);
      } else {
        JOptionPane.showMessageDialog(this, "No existe una calle con ID: " + id);
        txtDescripcion.setText("");
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getLocalizedMessage());
    }
  }

  private void limpiarCampos() {
    txtId.setText("");
    txtDescripcion.setText("");
    txtId.requestFocus();
  }
}
