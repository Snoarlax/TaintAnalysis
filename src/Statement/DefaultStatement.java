package Statement;

import java.util.HashSet;

public class DefaultStatement extends Statement{
    private final String rawStatement;

    public DefaultStatement(String rawStatement) {
        this.rawStatement = rawStatement;
    }

    // Does nothing by Default
    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {}

    @Override
    public StatementType getStatementType() {
        return StatementType.DEFAULT;
    }

    @Override
    public boolean isTaintedSink() {
        return false;
    }

    @Override
    public HashSet<Variable> TaintedBy() {
        return null;
    }

    public String getRawStatement() {
        return rawStatement;
    }
}
