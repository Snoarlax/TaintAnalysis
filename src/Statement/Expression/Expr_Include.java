package Statement.Expression;

import TaintAnalysisComponents.*;

import java.util.HashSet;
import java.util.List;

public class Expr_Include extends ExpressionStatement {
    // Print is basically FuncCall that is always a sink, I have no idea why Print is not automatically marked as FuncCall as I believe it should.

    private final HashSet<Variable> TaintedBy;

    public Expr_Include(String Expression, String[] Arguments) {
        super(Expression, Arguments);
        ComponentType = Component.Sink;
        TaintedBy = new HashSet<>();
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
        Variable expr = inputTaint.get(Arguments[0].split(": ", 2)[1]);
        if (expr.hasTainted(TaintType.DIRECTORY) && !isTaintedSink()){
            tainted = true;
            Variable result = inputTaint.get(Arguments[1].split(": ", 2)[1]);
            result.setAllTainted(List.of(TaintType.values()));
            inputTaint.put(result);

            TaintedBy.add(expr);
            result.TaintedFrom(expr);
        }
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
    public Sink getSinkType() {
        return Sink.include;
    }
}
