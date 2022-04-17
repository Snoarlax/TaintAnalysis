package Statement.Expression;

import TaintAnalysisComponents.Sinks;
import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.TaintType;
import TaintAnalysisComponents.Variable;

import java.util.HashSet;

public class Expr_Include extends ExpressionStatement {
    // Print is basically FuncCall that is always a sink, I have no idea why Print is not automatically marked as FuncCall as I believe it should.
    private boolean tainted;

    private final HashSet<Variable> TaintedBy;

    public Expr_Include(String Expression) {
        super(Expression);
        tainted = false;

        TaintedBy = new HashSet<>();
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        Variable expr = inputTaint.get(Arguments[0].split(": ", 2)[1]);
        if (expr.hasTainted(TaintType.DIRECTORY) && !isTaintedSink()){
            tainted = true;
            Variable result = inputTaint.get(Arguments[1].split(": ", 2)[1]);
            result.setAllTainted(expr.getTaints());
            inputTaint.put(result);

            TaintedBy.add(expr);
            result.TaintedFrom(expr);
        }
    }

    @Override
    public boolean isTaintedSink() {
        return tainted;
    }

    @Override
    public HashSet<Variable> TaintedBy() {
        return new HashSet<>(TaintedBy);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_Include;
    }

    @Override
    public Sinks getSinkType() {
        return Sinks.include;
    }
}
