package Statement;

import java.util.HashMap;
import java.util.HashSet;

public class ExpressionStatement extends Statement{
    private final String Expression;

    public ExpressionStatement(String expression) {

        Expression = expression;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // TODO implement taint transfer for a expression
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.EXPRESSION;
    }

    public String getExpression() {
        return Expression;
    }
}
