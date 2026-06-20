package repository.impl;

import connection.OracleConnection;
import model.Persona;
import repository.PersonaRepository;
import java.sql.*;
import java.util.*;

public class OraclePersonaRepository
    implements PersonaRepository {

    private final OracleConnection db;

    public OraclePersonaRepository(
        OracleConnection db
    ) {

        this.db = db;
    }

    @Override
    public void save(Persona p) {

        String sql =
            """
            INSERT INTO PERSONAS (
                PERS_COD_PERSONA,
                PERS_SERIE_CI,
                PERS_CI,
                PERS_NOMBRE,
                PERS_APELLIDO,
                PERS_FECHA_NACIMIENTO,
                PERS_ESTADO_CIVIL,
                PERS_SEXO,
                PERS_ID_CIUDAD,
                PERS_ID_PAIS,
                PERS_ID_SECUENCIADOR,
                PERS_RUC
            )
            VALUES (
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            """;

        try (

            PreparedStatement stmt =
                db.getConnection()
                    .prepareStatement(sql)

        ) {

            stmt.setLong(
                1,
                p.getCodPersona()
            );

            stmt.setString(
                2,
                p.getSerieCi()
            );

            stmt.setInt(
                3,
                p.getCi()
            );

            stmt.setString(
                4,
                p.getNombre()
            );

            stmt.setString(
                5,
                p.getApellido()
            );

            stmt.setDate(
                6,
                new java.sql.Date(
                    p.getFechaNacimiento()
                        .getTime()
                )
            );

            stmt.setInt(
                7,
                p.getEstadoCivil()
            );

            stmt.setInt(
                8,
                p.getSexo()
            );

            stmt.setInt(
                9,
                p.getIdCiudad()
            );

            stmt.setInt(
                10,
                p.getIdPais()
            );

            stmt.setInt(
                11,
                p.getIdSecuenciador()
            );

            stmt.setString(
                12,
                p.getRuc()
            );

            stmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                e.getMessage()
            );
        }
    }

    @Override
    public Optional<Persona> findById(
        Long id
    ) {

        String sql =
            """
            SELECT *
            FROM PERSONAS
            WHERE PERS_COD_PERSONA = ?
            """;

        try (

            PreparedStatement stmt =
                db.getConnection()
                    .prepareStatement(sql)

        ) {

            stmt.setLong(1, id);

            ResultSet rs =
                stmt.executeQuery();

            if (rs.next()) {

                return Optional.of(
                    map(rs)
                );
            }

        } catch (SQLException e) {

            System.out.println(
                e.getMessage()
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Persona> findAll() {

        List<Persona> list =
            new ArrayList<>();

        String sql =
            "SELECT * FROM PERSONAS";

        try (

            Statement stmt =
                db.getConnection()
                    .createStatement();

            ResultSet rs =
                stmt.executeQuery(sql)

        ) {

            while (rs.next()) {

                list.add(
                    map(rs)
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
    public void update(Persona p) {

        String sql =
            """
            UPDATE PERSONAS
            SET
                PERS_NOMBRE = ?,
                PERS_APELLIDO = ?,
                PERS_RUC = ?
            WHERE PERS_COD_PERSONA = ?
            """;

        try (

            PreparedStatement stmt =
                db.getConnection()
                    .prepareStatement(sql)

        ) {

            stmt.setString(
                1,
                p.getNombre()
            );

            stmt.setString(
                2,
                p.getApellido()
            );

            stmt.setString(
                3,
                p.getRuc()
            );

            stmt.setLong(
                4,
                p.getCodPersona()
            );

            stmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                e.getMessage()
            );
        }
    }

    @Override
    public void deleteById(
        Long id
    ) {

        String sql =
            """
            DELETE FROM PERSONAS
            WHERE PERS_COD_PERSONA = ?
            """;

        try (

            PreparedStatement stmt =
                db.getConnection()
                    .prepareStatement(sql)

        ) {

            stmt.setLong(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                e.getMessage()
            );
        }
    }

    private Persona map(
        ResultSet rs
    ) throws SQLException {

        return new Persona(
            rs.getLong(
                "PERS_COD_PERSONA"
            ),
            rs.getString(
                "PERS_SERIE_CI"
            ),
            rs.getInt(
                "PERS_CI"
            ),
            rs.getString(
                "PERS_NOMBRE"
            ),
            rs.getString(
                "PERS_APELLIDO"
            ),
            rs.getDate(
                "PERS_FECHA_NACIMIENTO"
            ),
            rs.getInt(
                "PERS_ESTADO_CIVIL"
            ),
            rs.getInt(
                "PERS_SEXO"
            ),
            rs.getInt(
                "PERS_ID_CIUDAD"
            ),
            rs.getInt(
                "PERS_ID_PAIS"
            ),
            rs.getInt(
                "PERS_ID_SECUENCIADOR"
            ),
            rs.getInt(
                "PERS_NRO_FUNCIONARIO"
            ),
            rs.getDate(
                "PERS_FECHA_USUARIO"
            ),
            rs.getInt(
                "PERS_ID_SUCURSAL"
            ),
            rs.getString(
                "PERS_RUC"
            )
        );
    }
}
