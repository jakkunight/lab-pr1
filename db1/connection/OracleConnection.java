package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnection {

    private final String host;
    private final String port;
    private final String instance;
    private final String user;
    private final String password;

    // INFO: Default config
    public OracleConnection(
        String user,
        String password
    ) {

        this(
            user,
            password,
            "127.0.0.1",
            "1521",
            "FREE"
        );
    }

    // INFO: Custom config
    public OracleConnection(
        String user,
        String password,
        String host,
        String port,
        String instance
    ) {

        this.host = host;
        this.port = port;
        this.instance = instance;

        this.user = user;
        this.password = password;
    }

    public Connection getConnection()
        throws SQLException {

        String url =
            "jdbc:oracle:thin:@tcp://"
            + host
            + ":"
            + port
            + "/"
            + instance;

        return DriverManager.getConnection(
            url,
            user,
            password
        );
    }
}
