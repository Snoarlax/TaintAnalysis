package Statement.Expression;

import Statement.TaintMap;
import Statement.Variable;

public class Expr_Assign extends ExpressionStatement{

    public Expr_Assign(String Expression) {
        super(Expression);
    }
    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // Arguments are var, expr, result. Var and Result inherit taint from expr

        Variable expr = inputTaint.get(Arguments[1].split(": ",2)[1]);

        if (expr.isTainted()) {
            Variable var = inputTaint.get(Arguments[0].split(": ", 2)[1]);
            Variable result = inputTaint.get(Arguments[2].split(": ", 2)[1]);

            var.setAllTainted(expr.getTaints());
            result.setAllTainted(expr.getTaints());

            inputTaint.put(var);
            inputTaint.put(result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_Assign; }
}
