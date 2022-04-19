package Statement;

import TaintAnalysisComponents.TaintMap;

public class DefaultStatement extends Statement{

    public DefaultStatement(String StatementName, String[] Arguments) {
        super(StatementName, Arguments);
    }

    // Does nothing by Default
    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {}

    @Override
    public StatementType getStatementType() {
        return StatementType.DEFAULT;
    }
}
