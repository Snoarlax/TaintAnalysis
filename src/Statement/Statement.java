package Statement;

import TaintAnalysisComponents.Component;
import TaintAnalysisComponents.Sink;
import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;

import java.util.Arrays;
import java.util.HashSet;

public abstract class Statement {
    final String StatementName;
    protected boolean tainted;
    protected final String[] Arguments;
    protected Component ComponentType = Component.Other;

    public Statement(String StatementName, String[] Arguments) {
        this.StatementName = StatementName;
        this.Arguments = Arguments;
    }

    abstract public void computeTaintFromInput(TaintMap inputTaint);
    abstract public StatementType getStatementType();


    public HashSet<Variable> TaintedBy() {
        return null;
    };
    public Sink getSinkType(){
        return null;
    }

    public final String getStatementName() {
        return StatementName;
    };
    public final boolean isTaintedSink() {
        return ComponentType == Component.Sink && tainted;
    };
    public final String[] getArguments() {
        return Arrays.copyOf(Arguments, Arguments.length);
    }
    public final Component getComponentType() { return ComponentType; }

}
