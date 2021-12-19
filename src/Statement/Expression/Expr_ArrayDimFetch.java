package Statement.Expression;

import Statement.StatementType;
import Statement.Variable;

import java.util.HashMap;

public class Expr_ArrayDimFetch extends ExpressionStatement{
    //TODO: Implement ArrayDimFetch source detection

    public Expr_ArrayDimFetch(String Expression){
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {

    }

    @Override
    public ExpressionType getExpressionType() { return ExpressionType.Expr_ArrayDimFetch; }

    @Override
    public StatementType getStatementType() {
        return StatementType.Expr;
    }

    @Override
    public boolean isSink() {
        return false;
    }

    @Override
    public boolean isSource() {
        return false;
    }
}
