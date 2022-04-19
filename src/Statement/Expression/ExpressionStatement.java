package Statement.Expression;
import Statement.*;
import TaintAnalysisComponents.TaintMap;

public abstract class ExpressionStatement extends Statement {

    ExpressionStatement(String Expression, String[] Arguments){
        super(Expression, Arguments);
    }

    @Override
    public abstract void computeTaintFromInput(TaintMap inputTaint);

    @Override
    public StatementType getStatementType() {
        return StatementType.Expr;
    }

    public abstract ExpressionType getExpressionType();
}
