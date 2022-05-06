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


        inputTaint.put(var);
        inputTaint.put(result);

        // only taint the result if the result was not the reason the array is tainted.
        if (!var.getTaintedFrom().contains(result) && !result.hasTainted(var.getTaints()))
            // alternatively, if the result gets tainted from the array...
        {
            result.TaintedFrom(var);
            result.setAllTainted(var.getTaints());
        }

        // if the result EVER becomes tainted int the future, the array should also become tainted if it is not already!
        else var.markTaintDependency(result);

    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ArrayDimFetch; }
}
