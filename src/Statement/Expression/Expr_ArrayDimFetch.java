package Statement.Expression;

import Statement.StatementType;
import Statement.TaintType;
import Statement.Variable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Expr_ArrayDimFetch extends ExpressionStatement{

    public Expr_ArrayDimFetch(String Expression){
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {
        // if the array is tainted, then the result of getting anything from the array will be considered tainted. (Possible extension to make more fine grained)
        Variable var = Variable.getVariableFromTaintMap(Arguments[0].split(": ",2)[1], inputTaint);

        if (var.isTainted()) {
            Variable result = Variable.getVariableFromTaintMap(Arguments[2].split(": ", 2)[1], inputTaint);
            result.setAllTainted(var.getTaints());

            inputTaint.put(result, result);
        }
    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ArrayDimFetch; }
}
