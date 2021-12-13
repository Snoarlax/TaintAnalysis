package Statement;

import java.util.HashMap;
import java.util.HashSet;

public class TerminalStatement extends Statement{
    private final String TerminalName;
    private boolean tainted;

    public TerminalStatement(String terminalName) {
        TerminalName = terminalName;
        tainted = false;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // TODO implement taint transfer for a terminal
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.TERMINAL;
    }

    public String getTerminalName() {
        return TerminalName;
    }
}
