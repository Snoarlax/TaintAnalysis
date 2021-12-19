package Statement.Expression;

import Statement.Variable;

import java.util.HashMap;

public class Expr_Assign extends ExpressionStatement{

    public Expr_Assign(String Expression) {
        super(Expression);
    }
    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {
        // Arguments are var, expr, result. Var and Result inherit taint from expr
        Variable expr = new Variable(Arguments[1].split(": ",2)[1]);
        expr = inputTaint.getOrDefault(expr, expr);

        if (expr.isTainted()) {
            Variable var = new Variable(Arguments[0].split(": ", 2)[1]);
            Variable result = new Variable(Arguments[2].split(": ", 2)[1]);
            var = inputTaint.getOrDefault(var, var);
            result = inputTaint.getOrDefault(result, result);

            var.setAllTainted(expr.getTaints());
            result.setAllTainted(expr.getTaints());

            inputTaint.put(var, var);
            inputTaint.put(result, result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_Assign; }

    @Override
    public boolean isSink() {
        return false;
    }

    @Override
    public boolean isSource() {
        return false;
    }
}
