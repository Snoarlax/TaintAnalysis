package Statement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ExpressionStatement extends Statement{
    private final String Expression;
    private boolean sink;

    public ExpressionStatement(String expression) {
        // TODO: Subclass the Expression type; its too big
        Expression = expression;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // TODO: Identify Sources from Expr_ArrayDimFetch (Sources from $_GET[], $_SERVER ...
        // TODO: Identify Sinks
        // Pass taint from expr_assign
        if (Expression.equals("Expr_Assign")) {
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
        // Concat List -> result has taint = union of input taints of concat
        if (Expression.equals("Expr_ConcatList")){
            // Arguments are list[0], list[1] ... and ending with result

            // Variables will contain the arguments that are not the result
            Variable[] Variables = new Variable[Arguments.length-1];

            for (int i = 0; i < Variables.length; i++) {
                Variables[i] = new Variable(Arguments[i].split(": ", 2)[1]);
                Variables[i] = inputTaint.getOrDefault(Variables[i],Variables[i]);
            }

            // check if any of the arguments are tainted
            if (Arrays.stream(Variables).anyMatch(x -> x.isTainted())) {
                // Check if the resultant variable is already in the TaintMap, if not create a new Variable.
                Variable result = new Variable(Arguments[Arguments.length-1].split(": ", 2)[1], new HashSet<>());
                result = inputTaint.getOrDefault(result, result);

                // Gets taint from all arguments into the result
                for (Variable var : Variables)
                    result.setAllTainted(var.getTaints());

                inputTaint.put(result, result);
            }
        }
        // binary concat -> result has taint = union of input taints of concat
        if (Expression.equals("Expr_BinaryOp_Concat")){
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
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.EXPRESSION;
    }

    public String getExpression() {
        return Expression;
    }
}
