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

        Variable expr = Variable.getVariableFromTaintMap(Arguments[1].split(": ",2)[1], inputTaint);

        if (expr.isTainted()) {
            Variable var = Variable.getVariableFromTaintMap(Arguments[0].split(": ", 2)[1], inputTaint);
            Variable result = Variable.getVariableFromTaintMap(Arguments[2].split(": ", 2)[1], inputTaint);

            var.setAllTainted(expr.getTaints());
            result.setAllTainted(expr.getTaints());

            inputTaint.put(var, var);
            inputTaint.put(result, result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_Assign; }
}
