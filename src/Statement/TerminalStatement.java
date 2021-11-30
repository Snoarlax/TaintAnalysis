package Statement;

import java.util.HashSet;

public class TerminalStatement extends Statement{
    private final String TerminalName;
    private boolean tainted;

    public TerminalStatement(String terminalName) {
        TerminalName = terminalName;
        tainted = false;
    }

    @Override
    public void computeTaintFromInput(HashSet<Variable> inputTaint, String[] Arguments) {
        // TODO implement taint transfer for a terminal
        return;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.TERMINAL;
    }

    public String getTerminalName() {
        return TerminalName;
    }
}
