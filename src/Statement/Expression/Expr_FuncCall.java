package Statement.Expression;

import TaintAnalysisComponents.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Expr_FuncCall extends ExpressionStatement{

    private final HashSet<Variable> TaintedBy;

    private Sink sinkType = null;

    public Expr_FuncCall(String Expression, String[] Arguments) {
        super(Expression, Arguments);
        if (computeSink(Arguments)) {
            ComponentType = Component.Sink;
            sinkType = findSinkType(Arguments);
        }
        else if (computeSanitization(Arguments))
            ComponentType = Component.Sanitization;
        else if (computeSource(Arguments))
            ComponentType = Component.Source;


        TaintedBy = new HashSet<>();


    }

    private boolean computeSource(String[] Arguments) {
        // returns true if the function name matches a Source name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(SourceFunction.values()).anyMatch(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"));
    }

    private boolean computeSanitization(String[] Arguments) {
        // returns true if the function name matches a Sanitizers name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(Sanitization.values()).anyMatch(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"));
    }

    private boolean computeSink(String[] Arguments){
        // returns true if the function name matches a sinks name.
        // Arguments[0] should be the array that the element is being fetched from
        return Arrays.stream(Sink.values()).anyMatch(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"));
    }

    private Sink findSinkType(String[] Arguments) {
        // returns the relevant Sinks enum from the Arguments
        return Arrays.stream(Sink.values()).filter(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')")).findFirst().get();
    }

    public boolean isSink() {
        return ComponentType == Component.Sink;
    }


    // Follows dataflow equation, Taintout = Taintout(pred) U gen(pred) / kill(pred)
    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
        // need to analyse function, in this case, by default a tainted argument will mean its a tainted result
        Variable[] FunctionArguments = new Variable[Arguments.length-2];
        // iterate through the arguments of the function, which are the Arguments of the statement excluding the first and last
        for (int i = 1; i < Arguments.length-1; i++)
            FunctionArguments[i-1] = inputTaint.get(Arguments[i].split(": ",2)[1]);

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
        if (ComponentType == Component.Sink) {
            if (!tainted) {
                Sink sinkType = Arrays.stream(Sink.values())
                        .filter(x -> Arguments[0].endsWith("LITERAL('" + x.name() + "')"))
                        .findFirst()
                        .get();
                for (Variable arg : FunctionArguments) {
                    if (arg.getTaints().contains(sinkType.getVulnerableTaint())) {
                        TaintedBy.add(arg);
                        tainted = true;
                    }
                }
            }
        }

        // if statement is a sanitisation function, remove the relevant taints from the result.

        else if (ComponentType == Component.Sanitization) {
             HashSet<TaintType> TaintsToClear = Arrays.stream(Sanitization.values())
                    .filter(x -> getFunctionName().equals(x.name()))
                    .findFirst()
                    .get()
                    .getTaintTypeSanitizations();

             result.clearAllTainted(TaintsToClear);
        }

        else if (ComponentType == Component.Source) {
            result.setAllTainted(List.of(TaintType.values()));
            // creates a variable for the sake of the tainted from, not a real variable
            result.TaintedFrom(new Variable("SourceFunction<" + getFunctionName() + ">"));
        }

        // put the resultant variable with its taints into the TaintMap
        if (result.isTainted())
            inputTaint.put(result);
    }

    public String getFunctionName() {
        return Arguments[0].substring(Arguments[0].indexOf("LITERAL('")+9, Arguments[0].lastIndexOf("')"));
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
