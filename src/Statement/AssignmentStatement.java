package Statement;

import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.TaintType;
import TaintAnalysisComponents.Variable;

import java.util.HashSet;

public class AssignmentStatement extends Statement{
    private final String AssignedVariable;
    private final String AssignedValue;

    public AssignmentStatement(String assignedVariable, String assignedValue) {
        super("", new String[0]);
        AssignedVariable = assignedVariable;
        AssignedValue = assignedValue;

    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
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

        // If the Values are empty, then just create a new variable with name equal to the assigned variable
        // For each of the potential values, see if it is tainted. If it is, the AssignedVariable could be tainted, so pass it on.

        HashSet<TaintType> TaintsBefore = AssignedVar.getTaints();
        for (String Value : Values){
            Variable var = inputTaint.get(Value);
            // To remove cycles, TaintedFrom is only marked when a variable introduces new taints
            if (var.isTainted() && !TaintsBefore.containsAll(var.getTaints())) {
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

    public String getAssignedValue() {
        return AssignedValue;
    }

    public String getAssignedVariable() {
        return AssignedVariable;
    }
}
