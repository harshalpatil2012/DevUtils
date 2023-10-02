-- Create the table space (optional)
CREATE TABLESPACE my_tablespace
  DATAFILE 'my_tablespace.dbf'
  SIZE 100M
  AUTOEXTEND ON
  NEXT 10M
  MAXSIZE UNLIMITED;

-- Create a new user
CREATE USER my_user IDENTIFIED BY my_password
  DEFAULT TABLESPACE my_tablespace
  TEMPORARY TABLESPACE temp;

-- Grant necessary privileges to the user
GRANT CREATE SESSION TO my_user;
GRANT CREATE TABLE TO my_user;
GRANT UNLIMITED TABLESPACE TO my_user;

-- Connect as the new user
CONN my_user/my_password;

-- Create the session table
CREATE TABLE session_data (
  session_id NUMBER PRIMARY KEY,
  user_id NUMBER,
  session_start TIMESTAMP,
  session_end TIMESTAMP,
  created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_updated_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR2(255) DEFAULT 'ACTIVE' -- You can adjust the data type and default value
);

-- Create a sequence for generating session IDs (optional)
CREATE SEQUENCE session_id_seq
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

-- Create a trigger to populate session_id (optional, if using a sequence)
CREATE OR REPLACE TRIGGER session_data_trigger
BEFORE INSERT ON session_data
FOR EACH ROW
BEGIN
  SELECT session_id_seq.NEXTVAL
  INTO :NEW.session_id
  FROM DUAL;
END;
/
