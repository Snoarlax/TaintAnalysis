package Statement;

import java.util.HashMap;
import java.util.HashSet;

public class TerminalStatement extends Statement{
    private final String TerminalName;
    private boolean Sink;
    private boolean tainted;

    public TerminalStatement(String terminalName) {
        TerminalName = terminalName;
        tainted = false;

        if (terminalName.equals("Terminal_Echo"))
            Sink = true;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // TODO: if it is a return, need to implement tracking of taint between php functions later
        if (Sink) {
            // the only Terminal sink i know of is Terminal_Echo, which has only one argument
            Variable expr = Variable.getVariableFromTaintMap(Arguments[0].split(": ", 2)[1], inputTaint);

            if (expr.isTainted())
                tainted = true;
        }
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.Terminal;
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
