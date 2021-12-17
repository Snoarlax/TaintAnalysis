package Statement;

import java.util.HashMap;

public class StatementStatement extends Statement{
    private final String StatementName;

    public StatementStatement(String statementName) {
        StatementName = statementName;
    }
    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // From experimentation, it seems statements do not transfer taint, but manage control flow
        // May have to consider "Property"
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.STATEMENT;
    }

    public String getStatementName() {
        return StatementName;
    }
}
