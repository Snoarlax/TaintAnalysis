package Statement;

import java.util.HashSet;

public class TerminalStatement extends Statement{
    private final String TerminalName;

    public TerminalStatement(String terminalName) {
        TerminalName = terminalName;
    }

    @Override
    HashSet<Variable> computeTaintFromInput(HashSet<Variable> inputTaint) {
        // TODO implement taint transfer for a terminal
        return null;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.TERMINAL;
    }

    public String getTerminalName() {
        return TerminalName;
    }
}
