package Statement.Expression;

import Statement.Variable;

import java.util.Arrays;
import java.util.HashMap;

public class Expr_FuncCall extends ExpressionStatement{
    private final boolean sink;
    private boolean tainted;
    public Expr_FuncCall(String Expression, String[] Arguments) {
        super(Expression);
        tainted = false;
        sink = computeSink(Arguments);
    }

    private boolean computeSink(String[] Arguments){
        // returns true if the variable name matches a sinks name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(Sinks.values()).anyMatch(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"));
    }

    public boolean isSink() {
        return sink;
    }

    public boolean isTainted() {
        return sink && tainted;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable, Variable> inputTaint, String[] Arguments) {
        // todo: implement detection of sanitisation functions!
        // need to analyse function, in this case, by default a tainted argument will mean its a tainted result
        Variable[] FunctionArguments = new Variable[Arguments.length-2];
        // iterate through the arguments of the function, which are the Arguments of the statement excluding the first and last
        for (int i = 1; i < Arguments.length-1; i++)
            FunctionArguments[i-1] = Variable.getVariableFromTaintMap(Arguments[i].split(": ",2)[1], inputTaint);

        Variable result = Variable.getVariableFromTaintMap(Arguments[Arguments.length-1].split(": ",2)[1], inputTaint);
        for (Variable arg : FunctionArguments)
            result.setAllTainted(arg.getTaints());

        if (result.isTainted())
            inputTaint.put(result, result);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_FuncCall;
    }

}
