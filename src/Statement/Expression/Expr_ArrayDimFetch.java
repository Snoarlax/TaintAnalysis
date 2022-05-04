package Statement.Expression;

import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;

public class Expr_ArrayDimFetch extends ExpressionStatement{

    public Expr_ArrayDimFetch(String Expression, String[] Arguments){
        super(Expression, Arguments);
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
        // if the array is tainted, then the result of getting anything from the array will be considered tainted.
        Variable var = inputTaint.get(Arguments[0].split(": ",2)[1]);
        Variable result = inputTaint.get(Arguments[2].split(": ", 2)[1]);

        // if the result EVER becomes tainted, the array should also become tainted!
        var.markTaintDependency(result);
        inputTaint.put(var);
        inputTaint.put(result);


        if (var.isTainted()) {
            if (!result.hasTainted(var.getTaints()))
                result.TaintedFrom(var);
                result.setAllTainted(var.getTaints());
        }


    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ArrayDimFetch; }
}
