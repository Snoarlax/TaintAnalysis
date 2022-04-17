package Statement;

import TaintAnalysisComponents.TaintMap;

public class StatementStatement extends Statement{
    private final String StatementName;

    public StatementStatement(String statementName) {
        StatementName = statementName;
    }
    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // From experimentation, it seems statements do not transfer taint, but manage control flow
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.Stmt;
    }

    public String getStatementName() {
        return StatementName;
    }
}
