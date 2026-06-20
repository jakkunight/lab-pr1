-- Tabla de todas las tablas.
-- En Oracle XE, el punto y coma final no es obligatorio.
select * from DBA_TABLES;
-- Tabla de todos los usuarios.
-- En Oracle XE, el punto y coma final no es obligatorio.

select * from DBA_USERS;
-- Lista tablas que guardan información operacional de
-- la base de datos.
select * from DICTIONARY;

---- USERS MANAGEMENT ----

drop user USERNAME cascade ;
create user username
identified by oracle ;
default tablespace USERS ;
temporary tablespace TEMP ;

grant permission to username ;

-- set oracle_sid=xe
-- imp system/oracle@xe fromuser=syscoop touser=syspr1 file=na28092019.dmp tables=personas

