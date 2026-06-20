import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.*;

public class FormPais extends JFrame implements ActionListener {
  private JLabel lblId, lblDescripcion;
  private JTextField txtId, txtDescripcion;
  private JButton btnAgregar, btnActualizar, btnEliminar, btnBuscar;
  private BDOracle db;

  public FormPais(BDOracle db) {
    this.db = db;
    setTitle("Gestión de Países");
    setSize(500, 300);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(null);
    setLocationRelativeTo(null);
    initComponents();
    setBoundsComponents();
    setVisible(true);
  }

  private void initComponents() {
    lblId = new JLabel("ID País:");
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
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btnAgregar) {
      agregarPais();
    } else if (e.getSource() == btnActualizar) {
      actualizarPais();
    } else if (e.getSource() == btnEliminar) {
      eliminarPais();
    } else if (e.getSource() == btnBuscar) {
      buscarPais();
    }
  }

  private void agregarPais() {
    try {
      String descripcion = txtDescripcion.getText().trim();
      if (descripcion.isEmpty()) {
        JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.");
        return;
      }
      ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM paises");
      int id = 0;
      if (rs.next()) {
        id = rs.getInt("N");
      }
      id = id + 1;
      db.sql(
          "INSERT INTO paises(PAIS_ID_PAIS, PAIS_DESCRIPCION, PAIS_ID_SECUENCIADOR) VALUES ("
              + id
              + ", '"
              + descripcion
              + "', 1)");
      JOptionPane.showMessageDialog(this, "País agregado exitosamente (ID: " + id + ")");
      limpiarCampos();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error al agregar: " + e.getLocalizedMessage());
    }
  }

  private void actualizarPais() {
    try {
      String idTexto = txtId.getText().trim();
      if (idTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el ID del país a actualizar.");
        return;
      }
      int id = Integer.parseInt(idTexto);
      String nuevaDescripcion = txtDescripcion.getText().trim();
      if (nuevaDescripcion.isEmpty()) {
        JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.");
        return;
      }
      ResultSet rs = db.sql("SELECT COUNT(*) AS N FROM paises WHERE PAIS_ID_PAIS = " + id);
      rs.next();
      if (rs.getInt("N") == 0) {
        JOptionPane.showMessageDialog(this, "No existe un país con ID: " + id);
        return;
      }
      db.sql(
          "UPDATE paises SET PAIS_DESCRIPCION = '"
              + nuevaDescripcion
              + "' WHERE PAIS_ID_PAIS = "
              + id);
      JOptionPane.showMessageDialog(this, "País actualizado correctamente.");
      limpiarCampos();
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getLocalizedMessage());
    }
  }

  private void eliminarPais() {
    try {
      String idTexto = txtId.getText().trim();
      if (idTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el ID del país a eliminar.");
        return;
      }
      int id = Integer.parseInt(idTexto);
      ResultSet rs = db.sql("SELECT PAIS_DESCRIPCION FROM paises WHERE PAIS_ID_PAIS = " + id);
      if (!rs.next()) {
        JOptionPane.showMessageDialog(this, "No existe un país con ID: " + id);
        return;
      }
      String descripcion = rs.getString("PAIS_DESCRIPCION");
      int opcion =
          JOptionPane.showConfirmDialog(
              this,
              "¿Desea eliminar el país?\nID: "
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
      db.sql("DELETE FROM paises WHERE PAIS_ID_PAIS = " + id);
      JOptionPane.showMessageDialog(this, "País eliminado permanentemente.");
      limpiarCampos();
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getLocalizedMessage());
    }
  }

  private void buscarPais() {
    try {
      String idTexto = txtId.getText().trim();
      if (idTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese el ID del país a buscar.");
        return;
      }
      int id = Integer.parseInt(idTexto);
      ResultSet rs =
          db.sql("SELECT PAIS_ID_PAIS, PAIS_DESCRIPCION FROM paises WHERE PAIS_ID_PAIS = " + id);
      if (rs.next()) {
        String descripcion = rs.getString("PAIS_DESCRIPCION");
        txtDescripcion.setText(descripcion);
        JOptionPane.showMessageDialog(
            this, "País encontrado.\nID: " + id + "\nDescripción: " + descripcion);
      } else {
        JOptionPane.showMessageDialog(this, "No existe un país con ID: " + id);
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
