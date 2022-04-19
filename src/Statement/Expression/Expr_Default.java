package Statement.Expression;

import TaintAnalysisComponents.TaintMap;

public class Expr_Default extends ExpressionStatement{
    public Expr_Default(String Expression, String[] Arguments) {
        super(Expression, Arguments);
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {

    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_Default;
    }
}
