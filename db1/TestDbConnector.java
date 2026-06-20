import java.sql.ResultSet;

/** TestDbConnector */
public class TestDbConnector {

  public static void main(String[] args) {
    BDOracle db = new BDOracle();
    try {
      db.abrir("syspr1", "oracle");
      ResultSet rs = db.sql("SELECT * FROM PAISES");
      while (rs.next()) {
        System.out.println("################################");
        System.out.println(rs.getString("PAIS_ID_PAIS"));
        System.out.println(rs.getString("PAIS_DESCRIPCION"));
        System.out.println(rs.getString("PAIS_ID_SECUENCIADOR"));
        System.out.println("################################");
      }
    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
    }
  }
}
