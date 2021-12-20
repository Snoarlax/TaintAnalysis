package Statement.Expression;

import Statement.Variable;

import java.util.HashMap;

public class Expr_FuncCall extends ExpressionStatement{
    private final boolean sink;

    Expr_FuncCall(String Expression) {
        super(Expression);
        sink = computeSink();
    }

    private boolean computeSink(){
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

    @Override
    public boolean isSink() {
        return sink;
    }

    @Override
    public boolean isSource() {
        return false;
    }
}
