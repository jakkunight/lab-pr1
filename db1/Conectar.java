import java.sql.ResultSet;

/** Conectar */
public abstract class Conectar {
  public abstract void abrir(String user, String password) throws Exception;

  public abstract ResultSet sql(String sql) throws Exception;
}
