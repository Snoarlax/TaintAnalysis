package Statement.Expression;

import TaintAnalysisComponents.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Expr_MethodCall extends ExpressionStatement{
    private final boolean isSink;
    private final boolean isSanitization;
    private final boolean isSource;
    private boolean isTainted;

    private final HashSet<Variable> TaintedBy;

    private final Sink sinkType;

    public Expr_MethodCall(String Expression, String[] Arguments) {
        super(Expression, Arguments);
        isTainted = false;
        isSink = computeSink(Arguments);
        isSanitization = computeSanitization(Arguments);
        isSource = computeSource(Arguments);

        TaintedBy = new HashSet<>();

        sinkType = isSink ? findSinkType(Arguments) : null;
    }

    private boolean computeSource(String[] Arguments) {
        // returns true if the function name matches a Source name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(SourceFunction.values()).anyMatch(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"));
    }

    private boolean computeSanitization(String[] Arguments) {
        // returns true if the variable name matches a Sanitizers name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(Sanitization.values()).anyMatch(x -> Arguments[1].endsWith("LITERAL('" + x.name() + "')"));
    }

    private boolean computeSink(String[] Arguments){
        // returns true if the variable name matches a sinks name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(Sink.values()).anyMatch(x -> Arguments[1].endsWith("LITERAL('" + x.name() + "')"));
    }

    private Sink findSinkType(String[] Arguments) {
        // returns the relevant Sinks enum from the Arguments
        return Arrays.stream(Sink.values()).filter(x -> Arguments[1].endsWith("LITERAL('" + x.name() + "')")).findFirst().get();
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
    public void computeTaintFromInput(TaintMap inputTaint) {
        // need to analyse function, in this case, by default a tainted argument will mean its a tainted result
        Variable[] FunctionArguments = new Variable[Arguments.length-3];
        // iterate through the arguments of the function, which are the Arguments of the statement excluding the first and last
        for (int i = 2; i < Arguments.length-1; i++)
            FunctionArguments[i-2] = inputTaint.get(Arguments[i].split(": ",2)[1]);


        Variable result = inputTaint.get(Arguments[Arguments.length-1].split(": ",2)[1]);

        // check if any of the arguments are tainted
        if (Arrays.stream(FunctionArguments).anyMatch(Variable::isTainted)) {
            // Gets taint from all arguments into the result
            for (Variable var : FunctionArguments) {
                if (var.isTainted()) {
                    result.setAllTainted(var.getTaints());
                    result.TaintedFrom(var);
                }
            }

            inputTaint.put(result);
        }



        // if the statement is a sink and is not already tainted, check if the any taint matches sinktype, and mark statement as tainted if this is the case
        if (!isTaintedSink() && isSink()) {
            Sink sinkType = Arrays.stream(Sink.values())
                    .filter(x -> Arguments[1].endsWith("LITERAL('" + x.name() + "')"))
                    .findFirst()
                    .get();
            for (Variable arg : FunctionArguments) {
                if (arg.getTaints().contains(sinkType.getVulnerableTaint())) {
                    TaintedBy.add(arg);
                    isTainted = true;
                }
            }
        }

        // if statement is a sanitisation function, remove the relevant taints from the result.

        else if (isSanitization) {
             HashSet<TaintType> TaintsToClear = Arrays.stream(Sanitization.values())
                    .filter(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"))
                    .findFirst()
                    .get()
                    .getTaintTypeSanitizations();

             result.clearAllTainted(TaintsToClear);
        }

        else if (isSource)
            result.setAllTainted(List.of(TaintType.values()));

        // put the resultant variable with its taints into the TaintMap
        if (result.isTainted())
            inputTaint.put(result);
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.Expr_FuncCall;
    }

    @Override
    public HashSet<Variable> TaintedBy() {
        return new HashSet<>(TaintedBy);
    }

    @Override
    public Sink getSinkType() {
        return sinkType;
    }

}
