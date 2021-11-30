package Statement;

import java.util.HashSet;

public abstract class Statement {

    abstract HashSet<Variable> computeTaintFromInput(HashSet<Variable> inputTaint);
    abstract public StatementType getStatementType();

}
