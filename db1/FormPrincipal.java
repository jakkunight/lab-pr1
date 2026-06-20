import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class FormPrincipal extends JFrame implements ActionListener {
  private JButton btnBarrios, btnCalles, btnPaises, btnCiudades;
  private BDOracle db;

  public FormPrincipal(BDOracle db) {
    this.db = db;
    setTitle("Administración de Datos");
    setSize(400, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(null);
    setLocationRelativeTo(null);
    initComponents();
    setBoundsComponents();
    setVisible(true);
  }

  private void initComponents() {
    btnBarrios = new JButton("Administrar Barrios");
    btnCalles = new JButton("Administrar Calles");
    btnPaises = new JButton("Administrar Países");
    btnCiudades = new JButton("Administrar Ciudades");

    btnBarrios.addActionListener(this);
    btnCalles.addActionListener(this);
    btnPaises.addActionListener(this);
    btnCiudades.addActionListener(this);

    add(btnBarrios);
    add(btnCalles);
    add(btnPaises);
    add(btnCiudades);
  }

  private void setBoundsComponents() {
    btnBarrios.setBounds(50, 30, 200, 30);
    btnCalles.setBounds(50, 80, 200, 30);
    btnPaises.setBounds(50, 130, 200, 30);
    btnCiudades.setBounds(50, 180, 200, 30);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btnBarrios) {
      new FormBarrio(db);
    } else if (e.getSource() == btnCalles) {
      new FormCalle(db);
    } else if (e.getSource() == btnPaises) {
      new FormPais(db);
    } else if (e.getSource() == btnCiudades) {
      new FormCiudad(db);
    }
  }
}
