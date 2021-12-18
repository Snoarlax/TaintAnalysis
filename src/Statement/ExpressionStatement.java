package Statement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ExpressionStatement extends Statement{
    private final String Expression;

    public ExpressionStatement(String expression) {

        Expression = expression;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {

        // Identify sources via Expr_assign
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

            for (int i = 0; i < Variables.length; i++)
                Variables[i] = inputTaint.getOrDefault(new Variable(Arguments[i].split(": ",2)[1], new HashSet<>()),
                        new Variable("Default", new HashSet<>()));

            // check if any of the arguments are tainted
            if (Arrays.stream(Variables).anyMatch(x -> x.isTainted())) {
                // Check if the resultant variable is already in the TaintMap, if not create a new Variable.
                Variable result = inputTaint.getOrDefault(new Variable(Arguments[Arguments.length-1].split(": ", 2)[1], new HashSet<>()),
                        new Variable(Arguments[Arguments.length-1].split(": ", 2)[1], new HashSet<>()));

                // Gets taint from all arguments into the result
                Arrays.stream(Variables).forEach(x -> result.setAllTainted(x.getTaints()));

                inputTaint.put(result, result);
            }
        }
        // binary concat -> result has taint = union of input taints of concat
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
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.EXPRESSION;
    }

    public String getExpression() {
        return Expression;
    }
}
