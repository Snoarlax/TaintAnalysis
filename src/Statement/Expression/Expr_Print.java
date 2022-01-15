package Statement.Expression;

import Statement.TaintMap;
import Statement.Variable;

public class Expr_Print extends ExpressionStatement{
    // Print is basically FuncCall that is always a sink, I have no idea why Print is not automatically marked as FuncCall as I believe it should.
    private boolean tainted;

    public Expr_Print(String Expression) {
        super(Expression);
        tainted = false;
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        Variable expr = inputTaint.get(Arguments[0].split(": ", 2)[1]);
        if (expr.isTainted()){
            tainted = true;
            Variable result = inputTaint.get(Arguments[1].split(": ", 2)[1]);
            result.setAllTainted(expr.getTaints());
            inputTaint.put(result);
        }
    }

    @Override
    public boolean isTaintedSink() {
        return tainted;
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_Print;
    }
}
