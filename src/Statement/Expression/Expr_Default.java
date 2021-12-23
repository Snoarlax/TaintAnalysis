package Statement.Expression;

import Statement.TaintMap;

public class Expr_Default extends ExpressionStatement{
    public Expr_Default(String Expression) {
        super(Expression);
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {

    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_Default;
    }
}
