package Statement.Expression;

import Statement.Variable;

import java.util.HashMap;

public class Expr_BinaryOp_Concat extends ExpressionStatement{

    public Expr_BinaryOp_Concat(String Expression){
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {
        // Arguments are left, right and result
        Variable left = Variable.getVariableFromTaintMap(Arguments[0].split(": ",2)[1], inputTaint);
        Variable right = Variable.getVariableFromTaintMap(Arguments[1].split(": ",2)[1], inputTaint);

        if (left.isTainted() || right.isTainted()) {
            // Check if the resultant variable is already in the TaintMap, if not create a new Variable.
            Variable result = Variable.getVariableFromTaintMap(Arguments[2].split(": ", 2)[1], inputTaint);

            result.setAllTainted(left.getTaints());
            result.setAllTainted(right.getTaints());

            inputTaint.put(result, result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_BinaryOp_Concat; }
}
