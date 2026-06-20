import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/** SQLConnector */
public interface SQLConnector {

  ResultSet query(String sql);

  Connection connect(SQLConnectionOptions config) throws SQLException;
}
