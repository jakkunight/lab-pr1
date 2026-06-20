import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

// =============================== BDOracle (provided) ===============================
class BDOracle {
  private Connection conn;
  private Statement stmt;

  BDOracle() {}

  public void abrir(String user, String password) throws Exception {
    String url = "jdbc:oracle:thin:@tcp://127.0.0.1:1521/FREE";
    this.conn = DriverManager.getConnection(url, user, password);
    this.stmt = this.conn.createStatement();
  }

  public ResultSet sql(String sql) throws Exception {
    return this.stmt.executeQuery(sql);
  }

  // Helper method to close resources (not strictly required, but good practice)
  public void cerrar() throws Exception {
    if (stmt != null) stmt.close();
    if (conn != null) conn.close();
  }
}

// =============================== ELM Model ===============================
class Model {
  BDOracle db; // database connection (null when disconnected)
  String username; // can be null
  String activeTable; // current table being operated on (null if none)
  String status; // one of the valid status strings

  Model() {
    this.db = null;
    this.username = null;
    this.activeTable = null;
    this.status = "Disconnected";
  }
}

// =============================== Message (simple String + data) ===============================
class Message {
  String type; // e.g. "Login", "SelectTable", "CreateTable", "GoBackReady", etc.
  Object data; // optional additional data

  Message(String type) {
    this(type, null);
  }

  Message(String type, Object data) {
    this.type = type;
    this.data = data;
  }
}

// =============================== Helper: Database Metadata ===============================
class DbMetadata {
  // Returns the list of column names that are NOT NULL (mandatory) for a given table,
  // excluding the primary key column(s) because they are auto-generated.
  public static List<String> getMandatoryColumns(BDOracle db, String tableName) throws Exception {
    List<String> mandatory = new ArrayList<>();
    DatabaseMetaData meta =
        db.sql("SELECT * FROM " + tableName + " WHERE 1=0")
            .getStatement()
            .getConnection()
            .getMetaData();
    ResultSet cols = meta.getColumns(null, null, tableName, null);
    Set<String> pkColumns = getPrimaryKeyColumns(db, tableName);
    while (cols.next()) {
      String colName = cols.getString("COLUMN_NAME");
      int nullable = cols.getInt("NULLABLE");
      if (nullable == DatabaseMetaData.columnNoNulls && !pkColumns.contains(colName)) {
        mandatory.add(colName);
      }
    }
    cols.close();
    return mandatory;
  }

  // Returns the set of primary key column names for the given table.
  public static Set<String> getPrimaryKeyColumns(BDOracle db, String tableName) throws Exception {
    Set<String> pkCols = new HashSet<>();
    DatabaseMetaData meta =
        db.sql("SELECT * FROM " + tableName + " WHERE 1=0")
            .getStatement()
            .getConnection()
            .getMetaData();
    ResultSet pk = meta.getPrimaryKeys(null, null, tableName);
    while (pk.next()) {
      pkCols.add(pk.getString("COLUMN_NAME"));
    }
    pk.close();
    return pkCols;
  }

  // Returns the next value for a single‑column primary key by querying MAX(id)+1.
  // Assumes the primary key is numeric. For composite keys, throws an exception.
  public static Object getNextPrimaryKeyValue(BDOracle db, String tableName) throws Exception {
    Set<String> pkCols = getPrimaryKeyColumns(db, tableName);
    if (pkCols.size() != 1) {
      throw new Exception(
          "Table "
              + tableName
              + " has composite primary key. Auto‑increment only supported for single‑column PK.");
    }
    String pkCol = pkCols.iterator().next();
    String sql = "SELECT NVL(MAX(" + pkCol + "), 0) + 1 AS next_id FROM " + tableName;
    ResultSet rs = db.sql(sql);
    rs.next();
    Object nextVal = rs.getObject("next_id");
    rs.close();
    return nextVal;
  }

  // Returns all existing primary key values (for single‑column PK) as a list.
  public static List<Object> getAllPrimaryKeyValues(BDOracle db, String tableName)
      throws Exception {
    Set<String> pkCols = getPrimaryKeyColumns(db, tableName);
    if (pkCols.size() != 1) {
      throw new Exception("Composite PK not supported for listing.");
    }
    String pkCol = pkCols.iterator().next();
    List<Object> values = new ArrayList<>();
    ResultSet rs = db.sql("SELECT " + pkCol + " FROM " + tableName + " ORDER BY 1");
    while (rs.next()) {
      values.add(rs.getObject(1));
    }
    rs.close();
    return values;
  }

  // Returns a map of column names to their current values for a record identified by PK.
  // pkValues must contain the exact column names and values (e.g. for composite keys).
  public static Map<String, Object> getRecordByPrimaryKey(
      BDOracle db, String tableName, Map<String, Object> pkValues) throws Exception {
    Set<String> pkCols = getPrimaryKeyColumns(db, tableName);
    StringBuilder where = new StringBuilder();
    for (String col : pkValues.keySet()) {
      if (where.length() > 0) where.append(" AND ");
      where.append(col).append(" = ?");
    }
    String sql = "SELECT * FROM " + tableName + " WHERE " + where.toString();
    PreparedStatement pstmt = db.sql(sql).getStatement().getConnection().prepareStatement(sql);
    int idx = 1;
    for (Object val : pkValues.values()) {
      pstmt.setObject(idx++, val);
    }
    ResultSet rs = pstmt.executeQuery();
    Map<String, Object> record = new HashMap<>();
    if (rs.next()) {
      ResultSetMetaData rsmd = rs.getMetaData();
      int colCount = rsmd.getColumnCount();
      for (int i = 1; i <= colCount; i++) {
        record.put(rsmd.getColumnName(i), rs.getObject(i));
      }
    }
    rs.close();
    pstmt.close();
    return record;
  }

  // Deletes the record identified by pkValues.
  public static void deleteRecord(BDOracle db, String tableName, Map<String, Object> pkValues)
      throws Exception {
    Set<String> pkCols = getPrimaryKeyColumns(db, tableName);
    StringBuilder where = new StringBuilder();
    for (String col : pkValues.keySet()) {
      if (where.length() > 0) where.append(" AND ");
      where.append(col).append(" = ?");
    }
    String sql = "DELETE FROM " + tableName + " WHERE " + where.toString();
    PreparedStatement pstmt = db.sql(sql).getStatement().getConnection().prepareStatement(sql);
    int idx = 1;
    for (Object val : pkValues.values()) {
      pstmt.setObject(idx++, val);
    }
    pstmt.executeUpdate();
    pstmt.close();
  }

  // Updates the record with given pkValues, setting all other columns from data.
  public static void updateRecord(
      BDOracle db, String tableName, Map<String, Object> pkValues, Map<String, Object> data)
      throws Exception {
    Set<String> pkCols = getPrimaryKeyColumns(db, tableName);
    List<String> setClauses = new ArrayList<>();
    List<Object> values = new ArrayList<>();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (!pkCols.contains(entry.getKey())) {
        setClauses.add(entry.getKey() + " = ?");
        values.add(entry.getValue());
      }
    }
    if (setClauses.isEmpty()) return;
    StringBuilder where = new StringBuilder();
    for (String col : pkValues.keySet()) {
      if (where.length() > 0) where.append(" AND ");
      where.append(col).append(" = ?");
    }
    String sql =
        "UPDATE "
            + tableName
            + " SET "
            + String.join(", ", setClauses)
            + " WHERE "
            + where.toString();
    PreparedStatement pstmt = db.sql(sql).getStatement().getConnection().prepareStatement(sql);
    int idx = 1;
    for (Object val : values) {
      pstmt.setObject(idx++, val);
    }
    for (Object val : pkValues.values()) {
      pstmt.setObject(idx++, val);
    }
    pstmt.executeUpdate();
    pstmt.close();
  }

  // Inserts a new record. Automatically adds the next PK value.
  public static void insertRecord(BDOracle db, String tableName, Map<String, Object> data)
      throws Exception {
    Set<String> pkCols = getPrimaryKeyColumns(db, tableName);
    if (pkCols.size() != 1) throw new Exception("Composite PK not supported for insert.");
    String pkCol = pkCols.iterator().next();
    Object nextPk = getNextPrimaryKeyValue(db, tableName);
    data.put(pkCol, nextPk);

    List<String> columns = new ArrayList<>(data.keySet());
    List<Object> values = new ArrayList<>(data.values());
    String sql =
        "INSERT INTO "
            + tableName
            + " ("
            + String.join(", ", columns)
            + ") VALUES ("
            + String.join(", ", Collections.nCopies(columns.size(), "?"))
            + ")";
    PreparedStatement pstmt = db.sql(sql).getStatement().getConnection().prepareStatement(sql);
    for (int i = 0; i < values.size(); i++) {
      pstmt.setObject(i + 1, values.get(i));
    }
    pstmt.executeUpdate();
    pstmt.close();
  }
}

// =============================== ELM Update ===============================
class Update {
  public static Model update(Model oldModel, Message msg) {
    Model newModel = new Model();
    // Copy old state
    newModel.db = oldModel.db;
    newModel.username = oldModel.username;
    newModel.activeTable = oldModel.activeTable;
    newModel.status = oldModel.status;

    switch (msg.type) {
      case "Quit":
        newModel.status = "Done";
        break;
      case "Login":
        String[] creds = (String[]) msg.data;
        String user = creds[0];
        String pass = creds[1];
        try {
          BDOracle db = new BDOracle();
          db.abrir(user, pass);
          newModel.db = db;
          newModel.username = user;
          newModel.status = "Ready";
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              null, "Login failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          newModel.status = "Disconnected";
        }
        break;
      case "SelectTable":
        String table = (String) msg.data;
        newModel.activeTable = table;
        newModel.status = "SetTable";
        break;
      case "GoBackReady":
        newModel.status = "Ready";
        newModel.activeTable = null;
        break;
      case "GoBackSetTable":
        newModel.status = "SetTable";
        break;
      case "CreateTable":
        // data is a Map<String, Object> with mandatory field values
        try {
          DbMetadata.insertRecord(
              newModel.db, newModel.activeTable, (Map<String, Object>) msg.data);
          JOptionPane.showMessageDialog(null, "Record created successfully.");
          newModel.status = "SetTable";
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              null, "Create failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          // stay in CreateTable state
        }
        break;
      case "ReadTable":
        // data is map of PK values
        try {
          Map<String, Object> record =
              DbMetadata.getRecordByPrimaryKey(
                  newModel.db, newModel.activeTable, (Map<String, Object>) msg.data);
          if (record.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No record found.");
          } else {
            StringBuilder sb = new StringBuilder("Record:\n");
            for (Map.Entry<String, Object> e : record.entrySet()) {
              sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
          }
          newModel.status = "SetTable";
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              null, "Read failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        break;
      case "UpdateTable":
        // data is a pair: first element is PK map, second is updated data map
        Object[] updateData = (Object[]) msg.data;
        Map<String, Object> pkForUpdate = (Map<String, Object>) updateData[0];
        Map<String, Object> updatedFields = (Map<String, Object>) updateData[1];
        try {
          DbMetadata.updateRecord(newModel.db, newModel.activeTable, pkForUpdate, updatedFields);
          JOptionPane.showMessageDialog(null, "Record updated successfully.");
          newModel.status = "SetTable";
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              null, "Update failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        break;
      case "DeleteTable":
        Map<String, Object> pkForDelete = (Map<String, Object>) msg.data;
        try {
          DbMetadata.deleteRecord(newModel.db, newModel.activeTable, pkForDelete);
          JOptionPane.showMessageDialog(null, "Record deleted successfully.");
          newModel.status = "SetTable";
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              null, "Delete failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        break;
      default:
        // Unknown message: ignore
        break;
    }
    return newModel;
  }
}

// =============================== Views (manual positioning) ===============================
class Views {
  private static JFrame mainFrame;
  private static Model currentModel;
  private static java.util.function.Consumer<Message> sendMessage;

  public static void init(
      JFrame frame, Model model, java.util.function.Consumer<Message> dispatcher) {
    mainFrame = frame;
    currentModel = model;
    sendMessage = dispatcher;
    mainFrame.setLayout(null); // manual positioning
    mainFrame.setSize(800, 600);
    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    mainFrame.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            sendMessage.accept(new Message("Quit"));
          }
        });
  }

  public static void render(Model model) {
    currentModel = model;
    mainFrame.getContentPane().removeAll();
    switch (model.status) {
      case "Disconnected":
        renderDisconnected();
        break;
      case "Ready":
        renderReady();
        break;
      case "SetTable":
        renderSetTable();
        break;
      case "Done":
        renderDone();
        break;
      default:
        // Create/Read/Update/Delete states are handled by separate methods called from SetTable
        if (model.status.startsWith("Create")) renderCreate();
        else if (model.status.startsWith("Read")) renderRead();
        else if (model.status.startsWith("Update")) renderUpdate();
        else if (model.status.startsWith("Delete")) renderDelete();
        else renderDisconnected();
    }
    mainFrame.revalidate();
    mainFrame.repaint();
  }

  private static void renderDisconnected() {
    JLabel userLabel = new JLabel("Username:");
    JTextField userField = new JTextField(15);
    JLabel passLabel = new JLabel("Password:");
    JPasswordField passField = new JPasswordField(15);
    JButton loginBtn = new JButton("Login");
    JButton quitBtn = new JButton("Quit");

    userLabel.setBounds(300, 200, 100, 25);
    userField.setBounds(400, 200, 150, 25);
    passLabel.setBounds(300, 240, 100, 25);
    passField.setBounds(400, 240, 150, 25);
    loginBtn.setBounds(320, 290, 100, 30);
    quitBtn.setBounds(440, 290, 100, 30);

    loginBtn.addActionListener(
        e ->
            sendMessage.accept(
                new Message(
                    "Login",
                    new String[] {userField.getText(), new String(passField.getPassword())})));
    quitBtn.addActionListener(e -> sendMessage.accept(new Message("Quit")));

    mainFrame.add(userLabel);
    mainFrame.add(userField);
    mainFrame.add(passLabel);
    mainFrame.add(passField);
    mainFrame.add(loginBtn);
    mainFrame.add(quitBtn);
  }

  private static void renderReady() {
    String[] tables = {
      "PERSONAS_SOCIOS",
      "INMUEBLES_VIVIENDAS",
      "PAISES",
      "PERSONAS",
      "BARRIOS",
      "CALLES",
      "CIUDADES",
      "FUNCIONARIOS"
    };
    int y = 50;
    for (String table : tables) {
      JButton btn = new JButton(table);
      btn.setBounds(300, y, 200, 30);
      btn.addActionListener(e -> sendMessage.accept(new Message("SelectTable", table)));
      mainFrame.add(btn);
      y += 40;
    }
    JButton quitBtn = new JButton("Quit");
    quitBtn.setBounds(300, y + 20, 200, 30);
    quitBtn.addActionListener(e -> sendMessage.accept(new Message("Quit")));
    mainFrame.add(quitBtn);
  }

  private static void renderSetTable() {
    JLabel label = new JLabel("Table: " + currentModel.activeTable);
    label.setBounds(300, 50, 300, 25);
    mainFrame.add(label);
    String[] ops = {"Create", "Read", "Update", "Delete"};
    int y = 100;
    for (String op : ops) {
      JButton btn = new JButton(op);
      btn.setBounds(350, y, 100, 30);
      btn.addActionListener(
          e -> sendMessage.accept(new Message(op + currentModel.activeTable, null)));
      mainFrame.add(btn);
      y += 50;
    }
    JButton backBtn = new JButton("Back to Tables");
    backBtn.setBounds(320, y + 20, 160, 30);
    backBtn.addActionListener(e -> sendMessage.accept(new Message("GoBackReady")));
    mainFrame.add(backBtn);
    JButton quitBtn = new JButton("Quit");
    quitBtn.setBounds(320, y + 70, 160, 30);
    quitBtn.addActionListener(e -> sendMessage.accept(new Message("Quit")));
    mainFrame.add(quitBtn);
  }

  private static void renderCreate() {
    try {
      List<String> mandatory =
          DbMetadata.getMandatoryColumns(currentModel.db, currentModel.activeTable);
      Map<String, JComponent> fields = new LinkedHashMap<>();
      int y = 50;
      for (String col : mandatory) {
        JLabel lbl = new JLabel(col + ":");
        lbl.setBounds(200, y, 150, 25);
        JTextField tf = new JTextField(20);
        tf.setBounds(360, y, 200, 25);
        mainFrame.add(lbl);
        mainFrame.add(tf);
        fields.put(col, tf);
        y += 40;
      }
      JButton saveBtn = new JButton("Save");
      saveBtn.setBounds(300, y + 20, 100, 30);
      saveBtn.addActionListener(
          e -> {
            Map<String, Object> data = new HashMap<>();
            for (Map.Entry<String, JComponent> entry : fields.entrySet()) {
              String val = ((JTextField) entry.getValue()).getText();
              // For simplicity, treat all as string. Oracle will convert.
              data.put(entry.getKey(), val);
            }
            sendMessage.accept(new Message("CreateTable", data));
          });
      JButton cancelBtn = new JButton("Cancel");
      cancelBtn.setBounds(420, y + 20, 100, 30);
      cancelBtn.addActionListener(e -> sendMessage.accept(new Message("GoBackSetTable")));
      mainFrame.add(saveBtn);
      mainFrame.add(cancelBtn);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(mainFrame, "Error loading create form: " + ex.getMessage());
      sendMessage.accept(new Message("GoBackSetTable"));
    }
  }

  private static void renderRead() {
    renderPkSelection("ReadTable");
  }

  private static void renderUpdate() {
    renderPkSelection("UpdateTable", true);
  }

  private static void renderDelete() {
    renderPkSelection("DeleteTable", false);
  }

  // Generic method to select a record by PK and then perform operation (read/update/delete)
  private static void renderPkSelection(String operationType, boolean fetchForUpdate) {
    try {
      Set<String> pkCols =
          DbMetadata.getPrimaryKeyColumns(currentModel.db, currentModel.activeTable);
      if (pkCols.size() != 1) {
        JOptionPane.showMessageDialog(mainFrame, "Composite PK not supported in this demo.");
        sendMessage.accept(new Message("GoBackSetTable"));
        return;
      }
      String pkCol = pkCols.iterator().next();
      List<Object> allPks =
          DbMetadata.getAllPrimaryKeyValues(currentModel.db, currentModel.activeTable);
      JComboBox<Object> pkCombo = new JComboBox<>(allPks.toArray());
      pkCombo.setBounds(350, 100, 200, 25);
      JLabel pkLabel = new JLabel(pkCol + ":");
      pkLabel.setBounds(250, 100, 100, 25);
      mainFrame.add(pkLabel);
      mainFrame.add(pkCombo);

      if (fetchForUpdate) {
        // additional fields for editing
        Map<String, JComponent> editFields = new LinkedHashMap<>();
        ResultSetMetaData meta =
            currentModel
                .db
                .sql("SELECT * FROM " + currentModel.activeTable + " WHERE 1=0")
                .getMetaData();
        int y = 150;
        for (int i = 1; i <= meta.getColumnCount(); i++) {
          String colName = meta.getColumnName(i);
          if (!pkCols.contains(colName)) {
            JLabel lbl = new JLabel(colName + ":");
            lbl.setBounds(200, y, 150, 25);
            JTextField tf = new JTextField(20);
            tf.setBounds(360, y, 200, 25);
            mainFrame.add(lbl);
            mainFrame.add(tf);
            editFields.put(colName, tf);
            y += 40;
          }
        }
        // load current values when selection changes
        pkCombo.addActionListener(
            ev -> {
              Object selected = pkCombo.getSelectedItem();
              if (selected != null) {
                try {
                  Map<String, Object> pkMap = new HashMap<>();
                  pkMap.put(pkCol, selected);
                  Map<String, Object> record =
                      DbMetadata.getRecordByPrimaryKey(
                          currentModel.db, currentModel.activeTable, pkMap);
                  for (Map.Entry<String, JComponent> entry : editFields.entrySet()) {
                    ((JTextField) entry.getValue())
                        .setText(String.valueOf(record.getOrDefault(entry.getKey(), "")));
                  }
                } catch (Exception ex) {
                  JOptionPane.showMessageDialog(
                      mainFrame, "Error loading record: " + ex.getMessage());
                }
              }
            });
        // manually trigger initial load
        if (allPks.size() > 0) pkCombo.setSelectedIndex(0);

        JButton updateBtn = new JButton("Update");
        updateBtn.setBounds(300, y + 20, 100, 30);
        updateBtn.addActionListener(
            e -> {
              Object selected = pkCombo.getSelectedItem();
              if (selected == null) return;
              Map<String, Object> pkMap = new HashMap<>();
              pkMap.put(pkCol, selected);
              Map<String, Object> updatedData = new HashMap<>();
              for (Map.Entry<String, JComponent> entry : editFields.entrySet()) {
                updatedData.put(entry.getKey(), ((JTextField) entry.getValue()).getText());
              }
              sendMessage.accept(new Message("UpdateTable", new Object[] {pkMap, updatedData}));
            });
        mainFrame.add(updateBtn);
      } else {
        // Read or Delete
        JButton actionBtn = new JButton(operationType.equals("ReadTable") ? "Read" : "Delete");
        actionBtn.setBounds(350, 180, 100, 30);
        actionBtn.addActionListener(
            e -> {
              Object selected = pkCombo.getSelectedItem();
              if (selected == null) return;
              Map<String, Object> pkMap = new HashMap<>();
              pkMap.put(pkCol, selected);
              sendMessage.accept(new Message(operationType, pkMap));
            });
        mainFrame.add(actionBtn);
      }
      JButton backBtn = new JButton("Back");
      backBtn.setBounds(350, 280, 100, 30);
      backBtn.addActionListener(e -> sendMessage.accept(new Message("GoBackSetTable")));
      mainFrame.add(backBtn);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(mainFrame, "Error preparing form: " + ex.getMessage());
      sendMessage.accept(new Message("GoBackSetTable"));
    }
  }

  private static void renderDone() {
    JLabel bye = new JLabel("Good bye!");
    bye.setBounds(350, 250, 200, 30);
    mainFrame.add(bye);
    Timer timer = new Timer(2000, e -> System.exit(0));
    timer.setRepeats(false);
    timer.start();
  }
}

// =============================== Main Application ===============================
public class MainApp {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          JFrame frame = new JFrame("CRUD Application - ELM Architecture");
          Model initialModel = new Model();
          // message dispatcher
          java.util.function.Consumer<Message> dispatcher =
              msg -> {
                Model newModel = Update.update(initialModel, msg);
                // update model reference
                initialModel.db = newModel.db;
                initialModel.username = newModel.username;
                initialModel.activeTable = newModel.activeTable;
                initialModel.status = newModel.status;
                Views.render(initialModel);
              };
          Views.init(frame, initialModel, dispatcher);
          Views.render(initialModel);
          frame.setVisible(true);
        });
  }
}
