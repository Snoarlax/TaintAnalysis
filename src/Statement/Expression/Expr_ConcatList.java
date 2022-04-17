package Statement.Expression;

import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;

import java.util.Arrays;

public class Expr_ConcatList extends ExpressionStatement{

    public Expr_ConcatList(String Expression){
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // Arguments are list[0], list[1] ... and ending with result

        // Variables will contain the arguments that are not the result
        Variable[] Variables = new Variable[Arguments.length-1];

        for (int i = 0; i < Variables.length; i++)
            Variables[i] = inputTaint.get(Arguments[i].split(": ", 2)[1]);

        // check if any of the arguments are tainted
        if (Arrays.stream(Variables).anyMatch(Variable::isTainted)) {
            // Check if the resultant variable is already in the TaintAnalysisComponents.TaintMap, if not create a new Variable.
            Variable result = inputTaint.get(Arguments[Arguments.length-1].split(": ", 2)[1]);

            // Gets taint from all arguments into the result
            for (Variable var : Variables) {
                if (var.isTainted()) {
                    result.setAllTainted(var.getTaints());
                    result.TaintedFrom(var);
                }
            }

            inputTaint.put(result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ConcatList; }
}
