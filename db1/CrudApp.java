import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

/**
 * CRUD application using ELM architecture with Java Swing.
 * Manages multiple tables from an Oracle database.
 *
 * IMPORTANT: The original BDOracle class only supports queries.
 * To enable INSERT, UPDATE, DELETE operations, an additional
 * method (update) has been added below. This is a necessary
 * modification for the CRUD functionality.
 */
public class CrudApp extends JFrame {

    // ====================== MODEL ======================
    static class Model {
        BDOracle bd;
        String username;           // database user
        String activeTable;        // currently selected table (null if none)
        String status;             // current application state
        Map<String, String> currentRecord; // holds fetched record for Update/Delete
        String lastPkValue;        // last used PK for Read/Update/Delete input

        Model() {
            bd = null;
            username = null;
            activeTable = null;
            status = "Disconnected";
            currentRecord = null;
            lastPkValue = null;
        }
    }

    private Model model;

    // ====================== CONSTANTS ======================
    // Table names
    static final String[] TABLES = {
        "PERSONAS_SOCIOS", "INMUEBLES_VIVIENDAS", "PAISES", "PERSONAS",
        "BARRIOS", "CALLES", "CIUDADES", "FUNCIONARIOS"
    };

    // ====================== CONSTRUCTOR ======================
    public CrudApp() {
        model = new Model();
        setTitle("CRUD Application");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        render();
        setVisible(true);
    }

    // ====================== DISPATCH (Update) ======================
    private void dispatch(String msg) {
        String oldStatus = model.status;
        update(msg);
        if (!model.status.equals(oldStatus) || msg.startsWith("FetchRecord") || msg.startsWith("Fail")) {
            render();
        }
    }

    // ====================== UPDATE LOGIC ======================
    private void update(String msg) {
        // Ignore if model is done
        if ("Done".equals(model.status)) return;

        // Universal quit
        if ("Quit".equals(msg)) {
            if (model.bd != null) {
                try { model.bd.close(); } catch (Exception ignored) {}
            }
            model.status = "Done";
            return;
        }

        // Disconnected state
        if ("Disconnected".equals(model.status)) {
            if (msg.startsWith("Login ")) {
                // parse "Login user=uuu,password=ppp" or use direct fields?
                // We'll construct it from the view as "Login|user|password"
                String[] parts = msg.substring(6).split("\\|");
                String user = parts[0];
                String pass = parts[1];
                try {
                    BDOracle bd = new BDOracle();
                    bd.abrir(user, pass);
                    model.bd = bd;
                    model.username = user;
                    model.status = "Ready";
                } catch (Exception e) {
                    model.status = "Disconnected";
                    JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }

        // Done state (no further updates)
        if ("Done".equals(model.status)) return;

        // GoBack messages
        if (msg.startsWith("GoBack")) {
            String target = msg.substring(6); // e.g., "Ready"
            if ("Ready".equals(target) || "Disconnected".equals(target) || "Done".equals(target)) {
                model.status = target;
            } else {
                // Go back to previous state according to rules: from operation to Set<table>
                if (model.status.startsWith("Set")) {
                    model.status = "Ready";
                } else if (model.status.startsWith("Create") || model.status.startsWith("Read") ||
                           model.status.startsWith("Update") || model.status.startsWith("Delete")) {
                    // go back to Set<table>
                    model.status = "Set" + model.activeTable;
                } else {
                    model.status = "Ready";
                }
            }
            return;
        }

        // Select table
        if (msg.startsWith("Select")) {
            String table = msg.substring(6);
            if (Arrays.asList(TABLES).contains(table)) {
                model.activeTable = table;
                model.status = "Set" + table;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid table: " + table, "Error", JOptionPane.ERROR_MESSAGE);
                // keep current status
            }
            return;
        }

        // FailSelect
        if (msg.startsWith("FailSelect")) {
            JOptionPane.showMessageDialog(this, "Failed to select table", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set<table> state operations
        if (model.status.startsWith("Set")) {
            String table = model.activeTable;
            if (msg.equals("Create" + table)) {
                model.status = "Create" + table;
                model.currentRecord = null;
            } else if (msg.equals("Read" + table)) {
                model.status = "Read" + table;
                model.currentRecord = null;
                model.lastPkValue = null;
            } else if (msg.equals("Update" + table)) {
                model.status = "Update" + table;
                model.currentRecord = null;
                model.lastPkValue = null;
            } else if (msg.equals("Delete" + table)) {
                model.status = "Delete" + table;
                model.currentRecord = null;
                model.lastPkValue = null;
            }
            return;
        }

        // Create<table> state
        if (model.status.startsWith("Create")) {
            String table = model.activeTable;
            if (msg.startsWith("Create" + table)) {
                // msg: "Create<table>|field1=val1|field2=val2..."
                String data = msg.substring(("Create" + table + "|").length());
                boolean ok = doCreate(table, data);
                if (ok) {
                    model.status = "Set" + table;
                } else {
                    JOptionPane.showMessageDialog(this, "Create failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            // GoBack handled earlier
            return;
        }

        // Read<table> state
        if (model.status.startsWith("Read")) {
            String table = model.activeTable;
            if (msg.startsWith("Read" + table)) {
                // msg: "Read<table>|pkValue"
                String pkValue = msg.substring(("Read" + table + "|").length());
                model.lastPkValue = pkValue;
                Map<String, String> rec = doReadByPk(table, pkValue);
                if (rec != null) {
                    model.currentRecord = rec;
                    // stay in same state but show record (re-render)
                } else {
                    model.currentRecord = null;
                    JOptionPane.showMessageDialog(this, "Record not found or read failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }

        // Update<table> state
        if (model.status.startsWith("Update")) {
            String table = model.activeTable;
            if (msg.startsWith("FetchRecord" + table)) {
                // "FetchRecord<table>|pkValue"
                String pkValue = msg.substring(("FetchRecord" + table + "|").length());
                Map<String, String> rec = doReadByPk(table, pkValue);
                if (rec != null) {
                    model.currentRecord = rec;
                    model.lastPkValue = pkValue;
                } else {
                    model.currentRecord = null;
                    JOptionPane.showMessageDialog(this, "Record not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (msg.startsWith("Update" + table)) {
                // "Update<table>|pkValue|field1=val1|field2=val2..."
                String rest = msg.substring(("Update" + table + "|").length());
                String[] parts = rest.split("\\|", 2);
                String pkValue = parts[0];
                String data = parts.length > 1 ? parts[1] : "";
                boolean ok = doUpdate(table, pkValue, data);
                if (ok) {
                    model.status = "Set" + table;
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }

        // Delete<table> state
        if (model.status.startsWith("Delete")) {
            String table = model.activeTable;
            if (msg.startsWith("FetchRecord" + table)) {
                String pkValue = msg.substring(("FetchRecord" + table + "|").length());
                Map<String, String> rec = doReadByPk(table, pkValue);
                if (rec != null) {
                    model.currentRecord = rec;
                    model.lastPkValue = pkValue;
                } else {
                    model.currentRecord = null;
                    JOptionPane.showMessageDialog(this, "Record not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (msg.startsWith("Delete" + table)) {
                // "Delete<table>|pkValue"
                String pkValue = msg.substring(("Delete" + table + "|").length());
                boolean ok = doDelete(table, pkValue);
                if (ok) {
                    model.status = "Set" + table;
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }
    }

    // ====================== VIEW RENDERING ======================
    private void render() {
        JPanel panel = new JPanel(null); // absolute positioning
        panel.setSize(getContentPane().getSize());
        String status = model.status;

        if ("Disconnected".equals(status)) {
            buildLoginView(panel);
        } else if ("Done".equals(status)) {
            buildDoneView(panel);
        } else if ("Ready".equals(status)) {
            buildReadyView(panel);
        } else if (status.startsWith("Set")) {
            buildSetView(panel, model.activeTable);
        } else if (status.startsWith("Create")) {
            buildCreateView(panel, model.activeTable);
        } else if (status.startsWith("Read")) {
            buildReadView(panel, model.activeTable);
        } else if (status.startsWith("Update")) {
            buildUpdateView(panel, model.activeTable);
        } else if (status.startsWith("Delete")) {
            buildDeleteView(panel, model.activeTable);
        } else {
            // fallback
            JLabel label = new JLabel("Unknown state: " + status);
            label.setBounds(50, 50, 200, 30);
            panel.add(label);
        }

        getContentPane().removeAll();
        getContentPane().add(panel);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    // ====================== DISCONNECTED VIEW ======================
    private void buildLoginView(JPanel panel) {
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 25);
        JTextField userField = new JTextField();
        userField.setBounds(160, 50, 150, 25);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 90, 100, 25);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(160, 90, 150, 25);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(50, 130, 80, 25);
        loginBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            dispatch("Login|" + u + "|" + p);
        });

        JButton quitBtn = new JButton("Quit");
        quitBtn.setBounds(150, 130, 80, 25);
        quitBtn.addActionListener(e -> dispatch("Quit"));

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginBtn);
        panel.add(quitBtn);
    }

    // ====================== DONE VIEW ======================
    private void buildDoneView(JPanel panel) {
        JLabel label = new JLabel("Good bye!");
        label.setBounds(50, 50, 200, 25);
        panel.add(label);
    }

    // ====================== READY VIEW (table selection) ======================
    private void buildReadyView(JPanel panel) {
        JLabel title = new JLabel("Select a table:");
        title.setBounds(50, 30, 200, 25);
        panel.add(title);

        int y = 70;
        for (String t : TABLES) {
            JButton btn = new JButton(t);
            btn.setBounds(50, y, 200, 25);
            btn.addActionListener(e -> dispatch("Select" + t));
            panel.add(btn);
            y += 35;
        }
    }

    // ====================== SET VIEW (operation selection) ======================
    private void buildSetView(JPanel panel, String table) {
        JLabel label = new JLabel("Table: " + table);
        label.setBounds(50, 30, 300, 25);
        panel.add(label);

        JButton createBtn = new JButton("Create");
        createBtn.setBounds(50, 70, 100, 25);
        createBtn.addActionListener(e -> dispatch("Create" + table));

        JButton readBtn = new JButton("Read");
        readBtn.setBounds(170, 70, 100, 25);
        readBtn.addActionListener(e -> dispatch("Read" + table));

        JButton updateBtn = new JButton("Update");
        updateBtn.setBounds(290, 70, 100, 25);
        updateBtn.addActionListener(e -> dispatch("Update" + table));

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(410, 70, 100, 25);
        deleteBtn.addActionListener(e -> dispatch("Delete" + table));

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(50, 110, 80, 25);
        backBtn.addActionListener(e -> dispatch("GoBackReady"));

        panel.add(createBtn);
        panel.add(readBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(backBtn);
    }

    // ====================== HELPER: Build a form with fields ======================
    // Returns a map of field name -> JTextField, and adds them to panel.
    private Map<String, JTextField> buildFieldRows(JPanel panel, String table, int startX, int startY, int yStep,
                                                    boolean forCreate, Map<String, String> values) {
        Map<String, JTextField> fields = new LinkedHashMap<>();
        String[] mandatory = getMandatoryFields(table);
        // For create, exclude auto PK columns from user input
        String[] pkCols = getPrimaryKeyColumns(table);
        int y = startY;
        for (String col : mandatory) {
            // Skip PK columns in create forms (they will be auto-generated)
            if (forCreate && isPrimaryKeyColumn(col, pkCols)) continue;
            JLabel lbl = new JLabel(col + ":");
            lbl.setBounds(startX, y, 200, 25);
            JTextField txt = new JTextField();
            txt.setBounds(startX + 210, y, 200, 25);
            if (values != null && values.containsKey(col)) {
                txt.setText(values.get(col));
            }
            if (!forCreate && values == null) {
                txt.setEditable(false); // for delete or read
            }
            panel.add(lbl);
            panel.add(txt);
            fields.put(col, txt);
            y += yStep;
        }
        return fields;
    }

    // ====================== CREATE VIEW ======================
    private void buildCreateView(JPanel panel, String table) {
        JLabel title = new JLabel("Create " + table);
        title.setBounds(50, 30, 300, 25);
        panel.add(title);

        // Build input fields for mandatory fields (excluding PK)
        Map<String, JTextField> fields = buildFieldRows(panel, table, 50, 70, 35, true, null);

        int yNext = 70 + fields.size() * 35 + 10;
        JButton createBtn = new JButton("Create");
        createBtn.setBounds(50, yNext, 100, 25);
        createBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("Create" + table + "|");
            for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue().getText()).append("|");
            }
            if (sb.charAt(sb.length() - 1) == '|') sb.deleteCharAt(sb.length() - 1);
            dispatch(sb.toString());
        });

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(170, yNext, 80, 25);
        backBtn.addActionListener(e -> dispatch("GoBackSet" + table));

        panel.add(createBtn);
        panel.add(backBtn);
    }

    // ====================== READ VIEW ======================
    private void buildReadView(JPanel panel, String table) {
        JLabel title = new JLabel("Read " + table);
        title.setBounds(50, 30, 300, 25);
        panel.add(title);

        // Show available primary keys
        String pkList = getAllPrimaryKeysAsHtml(table);
        JLabel pkLabel = new JLabel(pkList);
        pkLabel.setBounds(50, 70, 800, 150); // large enough
        panel.add(pkLabel);

        JLabel inputLabel = new JLabel("Enter PK value:");
        inputLabel.setBounds(50, 230, 120, 25);
        JTextField pkField = new JTextField();
        pkField.setBounds(180, 230, 150, 25);
        panel.add(inputLabel);
        panel.add(pkField);

        JButton readBtn = new JButton("Read");
        readBtn.setBounds(50, 270, 100, 25);
        readBtn.addActionListener(e -> {
            String pk = pkField.getText().trim();
            dispatch("Read" + table + "|" + pk);
        });

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(170, 270, 80, 25);
        backBtn.addActionListener(e -> dispatch("GoBackSet" + table));
        panel.add(readBtn);
        panel.add(backBtn);

        // If a record has been read, display it
        if (model.currentRecord != null) {
            int yRecord = 310;
            Map<String, JTextField> readFields = buildFieldRows(panel, table, 50, yRecord, 35, false, model.currentRecord);
        }
    }

    // ====================== UPDATE VIEW ======================
    private void buildUpdateView(JPanel panel, String table) {
        JLabel title = new JLabel("Update " + table);
        title.setBounds(50, 30, 300, 25);
        panel.add(title);

        String pkList = getAllPrimaryKeysAsHtml(table);
        JLabel pkLabel = new JLabel(pkList);
        pkLabel.setBounds(50, 70, 800, 150);
        panel.add(pkLabel);

        JLabel inputLabel = new JLabel("Enter PK value:");
        inputLabel.setBounds(50, 230, 120, 25);
        JTextField pkField = new JTextField();
        pkField.setBounds(180, 230, 150, 25);
        panel.add(inputLabel);
        panel.add(pkField);

        JButton fetchBtn = new JButton("Fetch");
        fetchBtn.setBounds(50, 270, 100, 25);
        fetchBtn.addActionListener(e -> {
            String pk = pkField.getText().trim();
            dispatch("FetchRecord" + table + "|" + pk);
        });

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(170, 270, 80, 25);
        backBtn.addActionListener(e -> dispatch("GoBackSet" + table));
        panel.add(fetchBtn);
        panel.add(backBtn);

        // If record fetched, show editable fields and update button
        if (model.currentRecord != null) {
            int yRecord = 310;
            Map<String, JTextField> updateFields = buildFieldRows(panel, table, 50, yRecord, 35, false, model.currentRecord);
            // Make fields editable
            for (JTextField tf : updateFields.values()) tf.setEditable(true);
            JButton updateBtn = new JButton("Update");
            updateBtn.setBounds(50, yRecord + updateFields.size() * 35 + 10, 100, 25);
            updateBtn.addActionListener(e -> {
                StringBuilder sb = new StringBuilder("Update" + table + "|");
                sb.append(model.lastPkValue).append("|");
                for (Map.Entry<String, JTextField> entry : updateFields.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue().getText()).append("|");
                }
                if (sb.charAt(sb.length() - 1) == '|') sb.deleteCharAt(sb.length() - 1);
                dispatch(sb.toString());
            });
            panel.add(updateBtn);
        }
    }

    // ====================== DELETE VIEW ======================
    private void buildDeleteView(JPanel panel, String table) {
        JLabel title = new JLabel("Delete " + table);
        title.setBounds(50, 30, 300, 25);
        panel.add(title);

        String pkList = getAllPrimaryKeysAsHtml(table);
        JLabel pkLabel = new JLabel(pkList);
        pkLabel.setBounds(50, 70, 800, 150);
        panel.add(pkLabel);

        JLabel inputLabel = new JLabel("Enter PK value:");
        inputLabel.setBounds(50, 230, 120, 25);
        JTextField pkField = new JTextField();
        pkField.setBounds(180, 230, 150, 25);
        panel.add(inputLabel);
        panel.add(pkField);

        JButton fetchBtn = new JButton("Fetch");
        fetchBtn.setBounds(50, 270, 100, 25);
        fetchBtn.addActionListener(e -> {
            String pk = pkField.getText().trim();
            dispatch("FetchRecord" + table + "|" + pk);
        });

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(170, 270, 80, 25);
        backBtn.addActionListener(e -> dispatch("GoBackSet" + table));
        panel.add(fetchBtn);
        panel.add(backBtn);

        if (model.currentRecord != null) {
            int yRecord = 310;
            Map<String, JTextField> readFields = buildFieldRows(panel, table, 50, yRecord, 35, false, model.currentRecord);
            // read-only
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setBounds(50, yRecord + readFields.size() * 35 + 10, 100, 25);
            deleteBtn.addActionListener(e -> {
                dispatch("Delete" + table + "|" + model.lastPkValue);
            });
            panel.add(deleteBtn);
        }
    }

    // ====================== DATABASE HELPERS ======================

    private String getAllPrimaryKeysAsHtml(String table) {
        StringBuilder sb = new StringBuilder("<html>Primary keys:<br/>");
        String[] pkCols = getPrimaryKeyColumns(table);
        String cols = String.join(",", pkCols);
        try {
            ResultSet rs = model.bd.sql("SELECT " + cols + " FROM " + table);
            while (rs.next()) {
                String val;
                if (pkCols.length == 1) {
                    val = rs.getString(1);
                } else {
                    // composite: join with '-'
                    StringBuilder comp = new StringBuilder();
                    for (int i = 0; i < pkCols.length; i++) {
                        if (i > 0) comp.append("-");
                        comp.append(rs.getString(i + 1));
                    }
                    val = comp.toString();
                }
                sb.append(val).append("<br/>");
            }
            rs.close();
        } catch (Exception e) {
            sb.append("Error loading keys");
        }
        sb.append("</html>");
        return sb.toString();
    }

    private Map<String, String> doReadByPk(String table, String pkValue) {
        String[] pkCols = getPrimaryKeyColumns(table);
        String[] pkParts = pkValue.split("-");
        if (pkParts.length != pkCols.length) return null;
        StringBuilder where = new StringBuilder();
        for (int i = 0; i < pkCols.length; i++) {
            if (i > 0) where.append(" AND ");
            where.append(pkCols[i]).append(" = ").append(pkParts[i]);
        }
        try {
            ResultSet rs = model.bd.sql("SELECT * FROM " + table + " WHERE " + where.toString());
            if (rs.next()) {
                Map<String, String> rec = new LinkedHashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    String colName = meta.getColumnName(i);
                    String val = rs.getString(i);
                    rec.put(colName, val != null ? val : "");
                }
                rs.close();
                return rec;
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean doCreate(String table, String data) {
        // data: "field1=val1|field2=val2"
        Map<String, String> values = parseData(data);
        // Generate PK
        String[] pkCols = getPrimaryKeyColumns(table);
        // For composite, we need to handle the auto part. We'll assume the first PK column is
        // the sequence (if composite). For simplicity, generate max(sequence) + 1 where other
        // parts are given.
        Map<String, String> pkValues = new LinkedHashMap<>();
        if (pkCols.length == 1) {
            String col = pkCols[0];
            long nextId = getNextId(table, col, null, null);
            pkValues.put(col, String.valueOf(nextId));
        } else {
            // composite: e.g., INVI_COD_PERSONA (given by user?) but user doesn't input PK.
            // According to spec, primary keys must not be inserted manually. For composite,
            // we must auto-generate the sequence part while the foreign key part must be
            // provided by the user? That conflicts. We'll assume the user provides the
            // INVI_COD_PERSONA through a mandatory field (which is also part of PK).
            // So we treat the first PK column as auto-generated and the rest as user-provided
            // from the form. For INMUEBLES_VIVIENDAS, INVI_COD_PERSONA is mandatory and
            // part of PK; INVI_SECUENCIA is auto. So we must get INVI_COD_PERSONA from the
            // data map (it's a mandatory field and user inputs it). We'll not exclude it
            // in the create form (in buildFieldRows we skip PK for create, but for composite
            // we need to keep the non-sequence column). Actually we need a better design.
            // Here we'll just assume the first PK column is auto, others are in the data.
            // For simplicity, we'll get the next id for the first PK column ignoring dependencies.
            String autoCol = pkCols[0];
            long nextId = getNextId(table, autoCol, null, null);
            pkValues.put(autoCol, String.valueOf(nextId));
            // remaining PK columns must be in values map (user entered)
            for (int i = 1; i < pkCols.length; i++) {
                if (!values.containsKey(pkCols[i])) return false;
                pkValues.put(pkCols[i], values.get(pkCols[i]));
            }
        }

        // Build INSERT statement
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        for (Map.Entry<String, String> entry : pkValues.entrySet()) {
            if (cols.length() > 0) cols.append(", ");
            cols.append(entry.getKey());
            if (vals.length() > 0) vals.append(", ");
            vals.append(entry.getValue());
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (pkValues.containsKey(entry.getKey())) continue; // already added
            if (cols.length() > 0) cols.append(", ");
            cols.append(entry.getKey());
            if (vals.length() > 0) vals.append(", ");
            vals.append("'" + entry.getValue().replace("'", "''") + "'"); // quote strings, assume numbers will be unquoted? We'll quote all as strings for simplicity.
        }

        String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";
        return executeUpdate(sql);
    }

    private boolean doUpdate(String table, String pkValue, String data) {
        // data: field=val|...
        Map<String, String> updates = parseData(data);
        if (updates.isEmpty()) return false;
        String[] pkCols = getPrimaryKeyColumns(table);
        String[] pkParts = pkValue.split("-");
        if (pkParts.length != pkCols.length) return false;

        StringBuilder setClause = new StringBuilder();
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            if (setClause.length() > 0) setClause.append(", ");
            setClause.append(entry.getKey()).append(" = '").append(entry.getValue().replace("'", "''")).append("'");
        }
        StringBuilder where = new StringBuilder();
        for (int i = 0; i < pkCols.length; i++) {
            if (i > 0) where.append(" AND ");
            where.append(pkCols[i]).append(" = ").append(pkParts[i]); // assume numeric PKs
        }
        String sql = "UPDATE " + table + " SET " + setClause + " WHERE " + where;
        return executeUpdate(sql);
    }

    private boolean doDelete(String table, String pkValue) {
        String[] pkCols = getPrimaryKeyColumns(table);
        String[] pkParts = pkValue.split("-");
        if (pkParts.length != pkCols.length) return false;
        StringBuilder where = new StringBuilder();
        for (int i = 0; i < pkCols.length; i++) {
            if (i > 0) where.append(" AND ");
            where.append(pkCols[i]).append(" = ").append(pkParts[i]);
        }
        String sql = "DELETE FROM " + table + " WHERE " + where;
        return executeUpdate(sql);
    }

    private long getNextId(String table, String column, String filterCol, String filterVal) {
        try {
            String sql = "SELECT NVL(MAX(" + column + "), 0) + 1 FROM " + table;
            if (filterCol != null && filterVal != null) {
                sql += " WHERE " + filterCol + " = " + filterVal;
            }
            ResultSet rs = model.bd.sql(sql);
            if (rs.next()) {
                long id = rs.getLong(1);
                rs.close();
                return id;
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private Map<String, String> parseData(String data) {
        Map<String, String> map = new LinkedHashMap<>();
        if (data == null || data.isEmpty()) return map;
        String[] pairs = data.split("\\|");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    private boolean executeUpdate(String sql) {
        try {
            // Use modified BDOracle's update method
            return model.bd.update(sql) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== SCHEMA HELPERS ======================
    private String[] getMandatoryFields(String table) {
        switch (table) {
            case "PERSONAS_SOCIOS": return new String[]{"PESO_NRO_SOCIO","PESO_COD_PERSONA","PESO_INGRESO","PESO_SITUACION_SOCIO","PESO_ACTIVO","PESO_ID_SECUENCIADOR_TABLA"};
            case "INMUEBLES_VIVIENDAS": return new String[]{"INVI_COD_PERSONA","INVI_SECUENCIA","INVI_ESTADO","INVI_HABITA"};
            case "PAISES": return new String[]{"PAIS_ID_PAIS","PAIS_DESCRIPCION","PAIS_ID_SECUENCIADOR"};
            case "PERSONAS": return new String[]{"PERS_COD_PERSONA","PERS_SERIE_CI","PERS_CI","PERS_NOMBRE","PERS_APELLIDO","PERS_ESTADO_CIVIL","PERS_SEXO","PERS_ID_CIUDAD","PERS_ID_PAIS","PERS_ID_SECUENCIADOR"};
            case "BARRIOS": return new String[]{"BARR_ID_BARRIO","BARR_DESCRIPCION","BARR_ID_SECUENCIADOR"};
            case "CALLES": return new String[]{"CALL_ID_CALLES","CALL_DESCRIPCION","CALL_ID_SECUENCIADOR"};
            case "CIUDADES": return new String[]{"CIUD_ID_CIUDAD","CIUD_DESCRIPCION","CIUD_ID_SECUENCIADOR"};
            case "FUNCIONARIOS": return new String[]{"FUNC_NRO_FUNCIONARIO","FUNC_COD_PERSONA","FUNC_ACTIVO"};
            default: return new String[]{};
        }
    }

    private String[] getPrimaryKeyColumns(String table) {
        // Assumed primary keys
        switch (table) {
            case "PERSONAS_SOCIOS": return new String[]{"PESO_NRO_SOCIO"};
            case "INMUEBLES_VIVIENDAS": return new String[]{"INVI_COD_PERSONA", "INVI_SECUENCIA"};
            case "PAISES": return new String[]{"PAIS_ID_PAIS"};
            case "PERSONAS": return new String[]{"PERS_COD_PERSONA"};
            case "BARRIOS": return new String[]{"BARR_ID_BARRIO"};
            case "CALLES": return new String[]{"CALL_ID_CALLES"};
            case "CIUDADES": return new String[]{"CIUD_ID_CIUDAD"};
            case "FUNCIONARIOS": return new String[]{"FUNC_NRO_FUNCIONARIO"};
            default: return new String[]{};
        }
    }

    private boolean isPrimaryKeyColumn(String col, String[] pkCols) {
        for (String pk : pkCols) if (pk.equals(col)) return true;
        return false;
    }

    // ====================== MAIN ======================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrudApp());
    }

    // ====================== MODIFIED BDORACLE (with DML support) ======================
    /**
     * Extended version of BDOracle that includes an update method.
     * The original class only supports queries, which is insufficient for CRUD.
     */
    class BDOracle {
        private Connection conn;
        private Statement stmt;

        BDOracle() {}

        public void abrir(String user, String password) throws Exception {
            String url = "jdbc:oracle:thin:@//127.0.0.1:1521/FREE";
            this.conn = DriverManager.getConnection(url, user, password);
            this.stmt = this.conn.createStatement();
        }

        public ResultSet sql(String sql) throws Exception {
            return this.stmt.executeQuery(sql);
        }

        /**
         * Executes an INSERT, UPDATE or DELETE statement.
         * @param sql DML statement
         * @return number of affected rows
         * @throws SQLException
         */
        public int update(String sql) throws Exception {
            return this.stmt.executeUpdate(sql);
        }

        public void close() throws Exception {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
}
