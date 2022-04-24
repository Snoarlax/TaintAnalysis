package Statement;

import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.TaintType;
import TaintAnalysisComponents.Variable;

import java.util.HashSet;

public class TerminalStatement extends Statement{
    private boolean Sink;
    private boolean tainted;

    private final HashSet<Variable> TaintedBy;

    public TerminalStatement(String StatementName, String[] Arguments) {
        super(StatementName, Arguments);
        tainted = false;

        // the only Terminal sink i know of is Terminal_Echo, which has only one argument
        if (StatementName.equals("Terminal_Echo"))
            Sink = true;

        TaintedBy = new HashSet<>();
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
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

    @Override
    public TaintAnalysisComponents.Sink getSinkType() {
        return isSink() ? TaintAnalysisComponents.Sink.echo : null;
    }

    public boolean isSink() {
        return Sink;
    }

    public boolean isTainted() {
        return tainted;
    }
}
