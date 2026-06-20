/** Model */
public class Model {

  BDOracle bd;
  String activeTable = null;
  String user = null;
  String status = "BEGIN";

  Model() {
    this.bd = new BDOracle();
  }
}

// Valid App states:
// - BEGIN
// - DONE
// - AUTHENTICATED
// - MANAGING(TABLE)
// - ADDING_RECORD(TABLE)
// - UPDATING_RECORD(TABLE)
// - DELETING_RECORD(TABLE)
