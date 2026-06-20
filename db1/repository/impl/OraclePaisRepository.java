package repository.impl;

import connection.OracleConnection;
import model.Pais;
import repository.PaisRepository;

import java.sql.*;
import java.util.*;

public class OraclePaisRepository
    implements PaisRepository {

    private final OracleConnection db;

    public OraclePaisRepository(
        OracleConnection db
    ) {

        this.db = db;
    }

    @Override
    public List<Pais> findAll() {

        List<Pais> list =
            new ArrayList<>();

        String sql =
            "SELECT * FROM PAISES";

        try (

            Connection conn =
                db.getConnection();

            Statement stmt =
                conn.createStatement();

            ResultSet rs =
                stmt.executeQuery(sql)

        ) {

            while (rs.next()) {

                list.add(
                    new Pais(
                        rs.getInt(
                            "PAIS_ID_PAIS"
                        ),
                        rs.getString(
                            "PAIS_DESCRIPCION"
                        ),
                        rs.getInt(
                            "PAIS_ID_SECUENCIADOR"
                        )
                    )
                );
            }

        } catch (SQLException e) {

            System.out.println(
                e.getMessage()
            );
        }

        return list;
    }

    @Override
    public Optional<Pais> findById(
        Integer id
    ) {

        return Optional.empty();
    }

    @Override
    public void save(Pais entity) {}

    @Override
    public void update(Pais entity) {}

    @Override
    public void deleteById(
        Integer id
    ) {}
}
