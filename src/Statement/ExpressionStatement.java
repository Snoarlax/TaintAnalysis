package Statement;

import java.util.HashMap;
import java.util.HashSet;

public class ExpressionStatement extends Statement{
    private final String Expression;

    public ExpressionStatement(String expression) {

        Expression = expression;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // TODO implement taint transfer for a expression
        // Identify sources via Expr_assign

        // Concat List -> result has taint = union of input taints of concat
        if (Expression.equals("Expr_BinaryOp_Concat")){
            // Arguments are left, right and result
            Variable left = inputTaint.getOrDefault(new Variable(Arguments[0].split(": ",2)[1], new HashSet<>()),
                    new Variable("Default", new HashSet<>()));
            Variable right = inputTaint.getOrDefault(new Variable(Arguments[1].split(": ",2)[1], new HashSet<>()),
                    new Variable("Default", new HashSet<>()));
            if (left.isTainted() || right.isTainted()) {
                // Check if the resultant variable is already in the TaintMap, if not create a new Variable.
                Variable result = inputTaint.getOrDefault(new Variable(Arguments[2].split(": ", 2)[1], new HashSet<>()),
                        new Variable(Arguments[2].split(": ", 2)[1], new HashSet<>()));

                result.setAllTainted(left.getTaints());
                result.setAllTainted(right.getTaints());

                inputTaint.put(result, result);
            }
        }
        // binary concat -> result has taint = union of input taints of concat

    }

    @Override
    public StatementType getStatementType() {
        return StatementType.EXPRESSION;
    }

    public String getExpression() {
        return Expression;
    }
}
