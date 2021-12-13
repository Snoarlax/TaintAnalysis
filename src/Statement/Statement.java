package Statement;

import java.util.HashMap;

public abstract class Statement {

    // Modify inputTaint in place so dataflow equations apply for each different statement
    // Use HashMap so we can get the Variable (cannot do this with HashSet)
    abstract public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments);
    abstract public StatementType getStatementType();

}
