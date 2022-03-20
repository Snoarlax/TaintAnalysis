package Statement;

public class TerminalStatement extends Statement{
    private final String TerminalName;
    private boolean Sink;
    private boolean tainted;

    public TerminalStatement(String terminalName) {
        TerminalName = terminalName;
        tainted = false;

        // the only Terminal sink i know of is Terminal_Echo, which has only one argument
        if (terminalName.equals("Terminal_Echo"))
            Sink = true;
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // TODO: if it is a return, need to implement tracking of taint between php functions later
        if (Sink) {
            Variable expr = inputTaint.get(Arguments[0].split(": ", 2)[1]);

            if (expr.hasTainted(TaintType.XSS))
                tainted = true;
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
