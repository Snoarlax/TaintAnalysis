package Statement;

import java.util.HashSet;

public abstract class Statement {

    // Modify inputTaint in place so dataflow equations apply for each different statement. Also add tainted type to the variables Taints
    // Use HashMap so we can get the Variable (cannot do this with HashSet)

    abstract public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments);
    abstract public StatementType getStatementType();
    abstract public boolean isTaintedSink();
    abstract public HashSet<Variable> TaintedBy();

}
