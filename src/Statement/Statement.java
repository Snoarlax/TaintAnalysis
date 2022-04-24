package Statement;

import TaintAnalysisComponents.Sink;
import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;

import java.util.Arrays;
import java.util.HashSet;

public abstract class Statement {
    final String StatementName;
    protected final String[] Arguments;

    public Statement(String StatementName, String[] Arguments) {
        this.StatementName = StatementName;
        this.Arguments = Arguments;
    }
    abstract public void computeTaintFromInput(TaintMap inputTaint);
    abstract public StatementType getStatementType();
    public boolean isTaintedSink() {
        return false;
    };
    public HashSet<Variable> TaintedBy() {
        return null;
    };
    public Sink getSinkType(){
        return null;
    }

    public String getStatementName() {
        return StatementName;
    };

    public String[] getArguments() {
        return Arrays.copyOf(Arguments, Arguments.length);
    }

}
