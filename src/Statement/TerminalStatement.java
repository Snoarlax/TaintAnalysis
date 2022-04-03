package Statement;

import java.util.HashSet;

public class TerminalStatement extends Statement{
    private final String TerminalName;
    private boolean Sink;
    private boolean tainted;

    private HashSet<Variable> TaintedBy;

    public TerminalStatement(String terminalName) {
        TerminalName = terminalName;
        tainted = false;

        // the only Terminal sink i know of is Terminal_Echo, which has only one argument
        if (terminalName.equals("Terminal_Echo"))
            Sink = true;

        TaintedBy = new HashSet<>();
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        if (Sink && !tainted) {
            Variable expr = inputTaint.get(Arguments[0].split(": ", 2)[1]);

            if (expr.hasTainted(TaintType.XSS)) {
                tainted = true;
                TaintedBy.add(expr);
            }
        }
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.Terminal;
    }

    @Override
    public boolean isTaintedSink() {
        return Sink && tainted;
    }

    @Override
    public HashSet<Variable> TaintedBy() {
        return new HashSet<>(TaintedBy);
    }

    public String getTerminalName() {
        return TerminalName;
    }

    public boolean isSink() {
        return Sink;
    }

    public boolean isTainted() {
        return tainted;
    }
}
