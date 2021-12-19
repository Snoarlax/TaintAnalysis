package Statement.Expression;

import Statement.Variable;

import java.util.HashMap;

public class Expr_Default extends ExpressionStatement{
    public Expr_Default(String Expression) {
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {

    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_Default;
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
