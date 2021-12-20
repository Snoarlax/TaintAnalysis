package Statement.Expression;

import Statement.StatementType;
import Statement.Variable;

import java.util.HashMap;

public class Expr_ArrayDimFetch extends ExpressionStatement{

    private final boolean source;

    public Expr_ArrayDimFetch(String Expression){
        super(Expression);
        source = computeSource();
    }

    private boolean computeSource(){
        // TODO: Compute if the object is a source or not from the Expression

        return false;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {

    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ArrayDimFetch; }

    @Override
    public boolean isSink() {
        return false;
    }

    @Override
    public boolean isSource() {
        return source;
    }
}
