package Statement.Expression;
import Statement.*;
import TaintAnalysisComponents.TaintMap;

public abstract class ExpressionStatement extends Statement {
    private final String Expression;

    ExpressionStatement(String Expression){
        this.Expression = Expression;
    }

    @Override
    public abstract void computeTaintFromInput(TaintMap inputTaint, String[] Arguments);

    @Override
    public StatementType getStatementType() {
        return StatementType.Expr;
    }

    public String getExpression() {
        return Expression;
    }

    public abstract ExpressionType getExpressionType();
}
