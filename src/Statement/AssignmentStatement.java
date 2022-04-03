package Statement;

import java.util.HashSet;

public class AssignmentStatement extends Statement{
    private final String AssignedVariable;
    private final String AssignedValue;

    public AssignmentStatement(String assignedVariable, String assignedValue) {
        super();
        AssignedVariable = assignedVariable;
        AssignedValue = assignedValue;

    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // put a new Variable in the Taint Set passed on to the next Statement. Make it have no types of taint registered.
        // Check if it is already in the taint map first, and if it is use that one

        Variable AssignedVar = inputTaint.get(AssignedVariable);

        // Determine Type[s] of taint present in the variable

        String[] Values;
        // Check if it is a Phi() assignment
        if (AssignedValue.startsWith("Phi(") && AssignedValue.endsWith(")"))
            Values = AssignedValue.substring(4,AssignedValue.length()-1).split(", ");

        else
            Values = new String[] {AssignedValue};

        // For each of the potential values, see if it is tainted. If it is, the AssignedVariable could be tainted, so pass it on.
        for (String Value : Values){
            Variable var = inputTaint.get(Value);
            if (var.isTainted()) {
                AssignedVar.setAllTainted(var.getTaints());
                AssignedVar.TaintedFrom(var);
            }
        }


        // Add the Variable to the Taint Set if it has been tainted
        if (AssignedVar.isTainted())
            inputTaint.put(AssignedVar);

    }

    @Override
    public StatementType getStatementType() {
        return StatementType.ASSIGNMENT;
    }

    @Override
    public boolean isTaintedSink() {
        return false;
    }

    @Override
    public HashSet<Variable> TaintedBy() {
        return null;
    }

    public String getAssignedValue() {
        return AssignedValue;
    }

    public String getAssignedVariable() {
        return AssignedVariable;
    }
}
