import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/** DatabaseConnector */
public class DatabaseConnector {
  private Connection conn;
  private Statement stmt;

  DatabaseConnector() {}

  public void abrir(String user, String password) throws Exception {
    String url = "jdbc:oracle:thin:@tcp://127.0.0.1:1521/FREE";

    this.conn = DriverManager.getConnection(url, user, password);
    this.stmt = this.conn.createStatement();
  }

  public ResultSet sql(String sql) throws Exception {
    ResultSet rs = this.stmt.executeQuery(sql);
    return rs;
  }
}
