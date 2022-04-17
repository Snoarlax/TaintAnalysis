package Statement.Expression;

import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;

public class Expr_BinaryOp_Concat extends ExpressionStatement{

    public Expr_BinaryOp_Concat(String Expression){
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // Arguments are left, right and result
        Variable left = inputTaint.get(Arguments[0].split(": ",2)[1]);
        Variable right = inputTaint.get(Arguments[1].split(": ",2)[1]);

        if (left.isTainted() || right.isTainted()) {
            // Check if the resultant variable is already in the TaintAnalysisComponents.TaintMap, if not create a new Variable.
            Variable result = inputTaint.get(Arguments[2].split(": ", 2)[1]);

            result.setAllTainted(left.getTaints());
            result.setAllTainted(right.getTaints());

            inputTaint.put(result);

            if (left.isTainted())
                result.TaintedFrom(left);

            if (right.isTainted())
                result.TaintedFrom(right);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_BinaryOp_Concat; }
}
