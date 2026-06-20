You are a seasoned software developer. I want you to implement a CRUD
application, following the ELM architecture and using Java Swing.

## Database schema

Consider the following database to be managed by the CRUD application:

### Tables

- PAISES
- BARRIOS
- CALLES
- CIUDADES

### Schemas

```plaintext
DESCRIBE PAISES;
 Name					  Null?    Type
 ----------------------------------------- -------- ----------------------------
 PAIS_ID_PAIS				  NOT NULL NUMBER(4)
 PAIS_DESCRIPCION			  NOT NULL VARCHAR2(40)
 PAIS_ID_SECUENCIADOR			  NOT NULL NUMBER(4)

DESCRIBE BARRIOS;
 Name					  Null?    Type
 ----------------------------------------- -------- ----------------------------
 BARR_ID_BARRIO 			  NOT NULL NUMBER(4)
 BARR_DESCRIPCION			  NOT NULL VARCHAR2(40)
 BARR_ID_SECUENCIADOR			  NOT NULL NUMBER(4)

DESCRIBE CALLES;
 Name					  Null?    Type
 ----------------------------------------- -------- ----------------------------
 CALL_ID_CALLES 			  NOT NULL NUMBER(5)
 CALL_DESCRIPCION			  NOT NULL VARCHAR2(45)
 CALL_ID_SECUENCIADOR			  NOT NULL NUMBER(4)

DESCRIBE CIUDADES;
 Name					  Null?    Type
 ----------------------------------------- -------- ----------------------------
 CIUD_ID_CIUDAD 			  NOT NULL NUMBER(4)
 CIUD_DESCRIPCION			  NOT NULL VARCHAR2(40)
 CIUD_ID_SECUENCIADOR			  NOT NULL NUMBER(4)
```

### Database connection class

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/** BDOracle */
public class BDOracle {
  private Connection conn;
  private Statement stmt;

  BDOracle() {}

  public void abrir(String user, String password) throws Exception {
    String url = "jdbc:oracle:thin:@tcp://127.0.0.1:1521/FREE";

    this.conn = DriverManager.getConnection(url, user, password);
    this.stmt = this.conn.createStatement();
  }

  public ResultSet sql(String sql) throws Exception {
    ResultSet rs = this.stmt.executeQuery(sql);
    return rs;
  }
}
```

## Constraints

- You must only use simple Swing elements. Use text input fields, password input
  fields, labels and buttons to make the application UI.
- DO NOT USE ENUMS for the messages or the state indicators for the model. Just
  use plain strings as messages. Annotate their meaning into the comments.
- Only take into account the 'NOT NULL' or mandatory fields to make the forms.
- DO NOT USE RECORDS. Just define another class for those data types or use
  magic values annotated into the comments.
- DO NOT USE GENERICS. Just use overloading or duplicate the required code.
- The PRIMARY KEYS MUST NOT BE INSERTED MANUALLY. The app must query the last
  available PRIMARY KEY from the selected table and increment it and used to
  insert new record into the table.
- The app must only use the BDOracle class as is.
- DO NOT USE ANY LAYOUTS. USE ONLY MANUAL POSITIONING AND SIZING OF THE ELEMENTS
- AVOID THE USE OF LAMBDAS. Don't use lambda functions. IN THE FORMS.
- If needed, create a new class/subform.

## Model

- BDOracle (database connection class)
- Username (can be null if not user is authenticated)
- Avtive table (can be null).
- Status (the appication state)

### Valid Statuses

The application's valid statuses. This expresses the workflow of the application
itself

- Disconnected (the login form. Should require the username and password for the
  database user/schema)
- Done (application finishes the execution)
- Ready (the main menu of the application)
- SetTable (the menu to "CRUD" the on the table 'Table')
- CreateTable (the "create" form for 'Table')
- ReadTable (the "read" form for 'Table')
- UpdateTable (the "update" form for 'Table')
- DeleteTable (the "delete" form for 'Table')

The 'Table' states must be declared for each table.

## Messages

- Quit (the user exits the application)
- GoBack<state> (a message to go back to the previous state <state>)
- Login (the user logged in)
- FailLogin (the login failed)
- SelectTable (message with the table to 'CRUD')
- FailSelectTable (message with the failure of setting as active table )
- CreateTable (message with the data to insert a new record into Table)
- ReadTable (message to read a specific record or records from Table)
- UpdateTable (message to update a specific record in the table Table)
- DeleteTable (message to delete a specific record in the table Table)
- FailCreateTable (message with the failure of a create operation over Table)
- FailReadTable (message with the failure of a read operation over Table)
- FailUpdateTable (message with the failure of a update operation over Table)
- FailDeleteTable (message with the failure of a delete operation over Table)

## Business Rules

- When a Fail<some_operation> is received on a <operation> state, keep the same
  state and show the error in a JOptionPane.
- When a an operation result message is received on a <operation> state, return
  to the previous SetTable state.
- When a GoBack is received in any state that's different from the Ready,
  Disconnected, or Done states, return to the previous state.

## Views

- Disconnected => A form with a username and password inputs, a button to quit
  the app, and a login button to try the login.
- Done => Render a "good bye" message.
- Ready => Shows a form with buttons that displays the names of the tables in
  the database. Used to select the active table and go selecting the available
  operations of the table.
- SetTable => Shows a form with a button for each CRUD operation to apply to the
  active table.
- CreateTable => A form to insert a new Table record. Only takes into account
  the mandatory fields
- ReadTable => A form to query for specific record on Table by the primary key.
  It must also display the available primary keys to be queried.
- UpdateTable => A form to edit the data of a record queried by the primary key.
  This form must first query and show the current value of the record to edit.
  It must also display the available primary keys to be queried.
- DeleteTable => A form to delete a record from the Table by the primary key.
  This form must first query and show the current value of the record to edit.
  It must also display the available primary keys to be queried.
