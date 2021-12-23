package Statement.Expression;

import Statement.Variable;

import java.util.HashMap;

public class Expr_FuncCall extends ExpressionStatement{
    private final boolean sink;

    public Expr_FuncCall(String Expression, String[] Arguments) {
        super(Expression);
        sink = computeSink(Arguments);
    }

    private boolean computeSink(String[] Arguments){
        // TODO: Compute if the object is a sink or not from the Expression

        return false;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {

    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_FuncCall;
    }

}
