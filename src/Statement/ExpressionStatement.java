package Statement;

import java.util.HashSet;

public class ExpressionStatement extends Statement{
    private final String Expression;

    public ExpressionStatement(String expression) {

        Expression = expression;
    }

    @Override
    HashSet<Variable> computeTaintFromInput(HashSet<Variable> inputTaint) {
        // TODO implement taint transfer for a expression
        return null;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.EXPRESSION;
    }

    public String getExpression() {
        return Expression;
    }
}
