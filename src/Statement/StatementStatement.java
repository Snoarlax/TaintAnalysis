package Statement;

import java.util.HashMap;
import java.util.HashSet;

public class StatementStatement extends Statement{
    private final String StatementName;

    public StatementStatement(String statementName) {
        StatementName = statementName;
    }
    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // TODO implement taint transfer for a statement
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.STATEMENT;
    }

    public String getStatementName() {
        return StatementName;
    }
}
