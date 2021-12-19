package Statement.Expression;
import Statement.*;
import java.util.HashMap;

public abstract class ExpressionStatement extends Statement {
    private final String Expression;

    ExpressionStatement(String Expression){
        this.Expression = Expression;
    }

    @Override
    public abstract void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments);

    @Override
    public StatementType getStatementType() {
        return StatementType.Expr;
    }

    public String getExpression() {
        return Expression;
    }

    public abstract ExpressionType getExpressionType();

    public abstract  boolean isSink();

    public abstract  boolean isSource();


}
