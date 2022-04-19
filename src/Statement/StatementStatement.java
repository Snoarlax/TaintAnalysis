package Statement;

import TaintAnalysisComponents.TaintMap;

public class StatementStatement extends Statement{

    public StatementStatement(String StatementName, String[] Arguments) {
        super(StatementName, Arguments);
    }
    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
        // From experimentation, it seems statements do not transfer taint, but manage control flow
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.Stmt;
    }
}
