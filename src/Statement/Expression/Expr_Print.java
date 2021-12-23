package Statement.Expression;

import Statement.Variable;

import java.util.HashMap;

public class Expr_Print extends ExpressionStatement{
    // Print is basically FuncCall that is always a sink, I have no idea why Print is not automatically marked as FuncCall as I believe it should.
    private boolean tainted;

    public Expr_Print(String Expression) {
        super(Expression);
        tainted = false;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {
        Variable expr = Variable.getVariableFromTaintMap(Arguments[0].split(": ", 2)[1], inputTaint);
        if (expr.isTainted()){
            tainted = true;
            Variable result = Variable.getVariableFromTaintMap(Arguments[1].split(": ", 2)[1], inputTaint);
            result.setAllTainted(expr.getTaints());
            inputTaint.put(result, result);
        }
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_Print;
    }
}
