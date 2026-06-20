select dbms_metadata.get_ddl(schema, syspr1, 'syspr1')
from all_objects
where owner = 'syspr1';


select DBMS_METADATA.GET_DDL('TABLE', table_name)
from user_tables;

