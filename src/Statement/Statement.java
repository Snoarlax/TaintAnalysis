package Statement;

import java.util.HashSet;

public abstract class Statement {

    // Modify inputTaint in place so dataflow equations apply for each different statement
    abstract public void computeTaintFromInput(HashSet<Variable> inputTaint, String[] Arguments);
    abstract public StatementType getStatementType();

}
