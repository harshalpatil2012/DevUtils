-- For Tables
BEGIN
   FOR t IN (SELECT table_name FROM user_tables) LOOP
      DECLARE
         ddl CLOB;
         file_handle UTL_FILE.FILE_TYPE;
      BEGIN
         ddl := DBMS_METADATA.get_ddl('TABLE',t.table_name);
         file_handle := UTL_FILE.fopen('DIRECTORY_PATH', 'TABLE_' || t.table_name || '.sql', 'w');
         UTL_FILE.put_line(file_handle, ddl);
         UTL_FILE.fclose(file_handle);
      END;
   END LOOP;
END;
/

-- For Views
BEGIN
   FOR v IN (SELECT view_name FROM user_views) LOOP
      DECLARE
         ddl CLOB;
         file_handle UTL_FILE.FILE_TYPE;
      BEGIN
         ddl := DBMS_METADATA.get_ddl('VIEW',v.view_name);
         file_handle := UTL_FILE.fopen('DIRECTORY_PATH', 'VIEW_' || v.view_name || '.sql', 'w');
         UTL_FILE.put_line(file_handle, ddl);
         UTL_FILE.fclose(file_handle);
      END;
   END LOOP;
END;
/

-- For Procedures
BEGIN
   FOR p IN (SELECT object_name FROM user_procedures WHERE object_type = 'PROCEDURE') LOOP
      DECLARE
         ddl CLOB;
         file_handle UTL_FILE.FILE_TYPE;
      BEGIN
         ddl := DBMS_METADATA.get_ddl('PROCEDURE',p.object_name);
         file_handle := UTL_FILE.fopen('DIRECTORY_PATH', 'PROCEDURE_' || p.object_name || '.sql', 'w');
         UTL_FILE.put_line(file_handle, ddl);
         UTL_FILE.fclose(file_handle);
      END;
   END LOOP;
END;
/

-- For Functions
BEGIN
   FOR f IN (SELECT object_name FROM user_procedures WHERE object_type = 'FUNCTION') LOOP
      DECLARE
         ddl CLOB;
         file_handle UTL_FILE.FILE_TYPE;
      BEGIN
         ddl := DBMS_METADATA.get_ddl('FUNCTION',f.object_name);
         file_handle := UTL_FILE.fopen('DIRECTORY_PATH', 'FUNCTION_' || f.object_name || '.sql', 'w');
         UTL_FILE.put_line(file_handle, ddl);
         UTL_FILE.fclose(file_handle);
      END;
   END LOOP;
END;
/

-- For Packages
BEGIN
   FOR p IN (SELECT object_name FROM user_procedures WHERE object_type = 'PACKAGE') LOOP
      DECLARE
         ddl CLOB;
         file_handle UTL_FILE.FILE_TYPE;
      BEGIN
         ddl := DBMS_METADATA.get_ddl('PACKAGE',p.object_name);
         file_handle := UTL_FILE.fopen('DIRECTORY_PATH', 'PACKAGE_' || p.object_name || '.sql', 'w');
         UTL_FILE.put_line(file_handle, ddl);
         UTL_FILE.fclose(file_handle);
      END;
   END LOOP;
END;
/
