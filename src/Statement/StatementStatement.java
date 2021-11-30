package Statement;

import java.util.HashSet;

public class StatementStatement extends Statement{
    private final String StatementName;

    public StatementStatement(String statementName) {
        StatementName = statementName;
    }
    @Override
    public void computeTaintFromInput(HashSet<Variable> inputTaint, String[] Arguments) {
        // TODO implement taint transfer for a statement
        return;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.STATEMENT;
    }

    public String getStatementName() {
        return StatementName;
    }
}
