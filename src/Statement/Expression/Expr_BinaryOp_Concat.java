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
        Variable left = new Variable(Arguments[0].split(": ",2)[1]);
        left = inputTaint.getOrDefault(left, left);
        Variable right = new Variable(Arguments[1].split(": ",2)[1]);
        right = inputTaint.getOrDefault(right, right);

        if (left.isTainted() || right.isTainted()) {
            // Check if the resultant variable is already in the TaintMap, if not create a new Variable.
            Variable result = new Variable(Arguments[2].split(": ", 2)[1]);
            result = inputTaint.getOrDefault(result, result);

            result.setAllTainted(left.getTaints());
            result.setAllTainted(right.getTaints());

            inputTaint.put(result, result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_BinaryOp_Concat; }

    @Override
    public boolean isSink() {
        return false;
    }

    @Override
    public boolean isSource() {
        return false;
    }
}