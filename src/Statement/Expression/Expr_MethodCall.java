package Statement.Expression;

import Statement.TaintMap;
import Statement.TaintType;
import Statement.Variable;

import java.util.Arrays;
import java.util.HashSet;

public class Expr_MethodCall extends ExpressionStatement{
    private final boolean isSink;
    private final boolean isSanitization;
    private boolean isTainted;

    public Expr_MethodCall(String Expression, String[] Arguments) {
        super(Expression);
        isTainted = false;
        isSink = computeSink(Arguments);
        isSanitization = computeSanitization(Arguments);
    }

    private boolean computeSanitization(String[] Arguments) {
        // returns true if the variable name matches a Sanitizers name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(Sanitizations.values()).anyMatch(x -> Arguments[1].endsWith("LITERAL('" + x.name() + "')"));
    }

    private boolean computeSink(String[] Arguments){
        // returns true if the variable name matches a sinks name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(Sinks.values()).anyMatch(x -> Arguments[1].endsWith("LITERAL('" + x.name() + "')"));
    }

    public boolean isSink() {
        return isSink;
    }

    @Override
    public boolean isTaintedSink() {
        return isSink && isTainted;
    }

    // Follows dataflow equation, Taintout = Taintout(pred) U gen(pred) / kill(pred)
    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // need to analyse function, in this case, by default a tainted argument will mean its a tainted result
        Variable[] FunctionArguments = new Variable[Arguments.length-3];
        // iterate through the arguments of the function, which are the Arguments of the statement excluding the first and last
        for (int i = 2; i < Arguments.length-1; i++)
            FunctionArguments[i-2] = inputTaint.get(Arguments[i].split(": ",2)[1]);

        Variable result = inputTaint.get(Arguments[Arguments.length-1].split(": ",2)[1]);
        for (Variable arg : FunctionArguments)
            result.setAllTainted(arg.getTaints());



        // if the statement is a sink and is not already tainted, check if the any taint matches sinktype, and mark statement as tainted if this is the case
        if (!isTaintedSink() && isSink()) {
            Sinks sinkType = Arrays.stream(Sinks.values())
                    .filter(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"))
                    .findFirst()
                    .get();
            if (result.getTaints().stream()
                    .anyMatch(x -> sinkType.getVulnerableTaints().contains(x))) {
                isTainted = true;
            }
        }

        // if statement is a sanitisation function, remove the relevant taints from the result.

        else if (isSanitization) {
             HashSet<TaintType> TaintsToClear = Arrays.stream(Sanitizations.values())
                    .filter(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"))
                    .findFirst()
                    .get()
                    .getTaintTypeSanitizations();

             result.clearAllTainted(TaintsToClear);
        }

        // put the resultant variable with its taints into the TaintMap
        if (result.isTainted())
            inputTaint.put(result);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_FuncCall;
    }

}
