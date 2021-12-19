package Statement.Expression;

import Statement.Variable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Expr_ConcatList extends ExpressionStatement{

    public Expr_ConcatList(String Expression){
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {
        // Arguments are list[0], list[1] ... and ending with result

        // Variables will contain the arguments that are not the result
        Variable[] Variables = new Variable[Arguments.length-1];

        for (int i = 0; i < Variables.length; i++) {
            Variables[i] = new Variable(Arguments[i].split(": ", 2)[1]);
            Variables[i] = inputTaint.getOrDefault(Variables[i],Variables[i]);
        }

        // check if any of the arguments are tainted
        if (Arrays.stream(Variables).anyMatch(Variable::isTainted)) {
            // Check if the resultant variable is already in the TaintMap, if not create a new Variable.
            Variable result = new Variable(Arguments[Arguments.length-1].split(": ", 2)[1], new HashSet<>());
            result = inputTaint.getOrDefault(result, result);

            // Gets taint from all arguments into the result
            for (Variable var : Variables)
                result.setAllTainted(var.getTaints());

            inputTaint.put(result, result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ConcatList; }

    @Override
    public boolean isSink() {
        return false;
    }

    @Override
    public boolean isSource() {
        return false;
    }
}
