package repository.impl;

import connection.OracleConnection;

import model.Ciudad;

import repository.CiudadRepository;

import java.sql.*;

import java.util.*;

public class OracleCiudadRepository
    implements CiudadRepository {

    private final OracleConnection db;

    public OracleCiudadRepository(
        OracleConnection db
    ) {

        this.db = db;
    }

    @Override
    public List<Ciudad> findAll() {

        List<Ciudad> list =
            new ArrayList<>();

        String sql =
            "SELECT * FROM CIUDADES";

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
                    new Ciudad(
                        rs.getInt(
                            "CIUD_ID_CIUDAD"
                        ),
                        rs.getString(
                            "CIUD_DESCRIPCION"
                        ),
                        rs.getInt(
                            "CIUD_ID_SECUENCIADOR"
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
    public Optional<Ciudad> findById(
        Integer id
    ) {

        return Optional.empty();
    }

    @Override
    public void save(Ciudad entity) {}

    @Override
    public void update(Ciudad entity) {}

    @Override
    public void deleteById(
        Integer id
    ) {}
}
