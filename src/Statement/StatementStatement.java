package Statement;

import java.util.HashSet;

public class StatementStatement extends Statement{
    private final String StatementName;

    public StatementStatement(String statementName) {
        StatementName = statementName;
    }
    @Override
    HashSet<Variable> computeTaintFromInput(HashSet<Variable> inputTaint) {
        // TODO implement taint transfer for a statement
        return null;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.STATEMENT;
    }

    public String getStatementName() {
        return StatementName;
    }
}
