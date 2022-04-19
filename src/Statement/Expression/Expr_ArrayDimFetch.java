package Statement.Expression;

import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;

public class Expr_ArrayDimFetch extends ExpressionStatement{

    public Expr_ArrayDimFetch(String Expression, String[] Arguments){
        super(Expression, Arguments);
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
        // if the array is tainted, then the result of getting anything from the array will be considered tainted. (Possible extension to make more fine grained)
        Variable var = inputTaint.get(Arguments[0].split(": ",2)[1]);

        if (var.isTainted()) {
            Variable result = inputTaint.get(Arguments[2].split(": ", 2)[1]);
            result.setAllTainted(var.getTaints());

            result.TaintedFrom(var);

            inputTaint.put(result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ArrayDimFetch; }
}
