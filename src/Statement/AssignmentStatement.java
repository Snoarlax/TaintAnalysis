package Statement;

import java.util.HashMap;
import java.util.HashSet;

public class AssignmentStatement extends Statement{
    private final String AssignedVariable;
    private final String AssignedValue;

    public AssignmentStatement(String assignedVariable, String assignedValue) {
        AssignedVariable = assignedVariable;
        AssignedValue = assignedValue;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // TODO:  Check arguments are are A Literal( ), and if they are ignore it
        // This does not generate any Taint, only transfers it. If inputTaint is empty, we can simply return.
        if (inputTaint.isEmpty())
            return;

        // put a new Variable in the Taint Set passed on to the next Statement. Make it have no types of taint registered.
        // Check if it is already in the taint map first, and if it is use that one
        Variable AssignedVar = inputTaint.getOrDefault(new Variable(AssignedVariable, new HashSet<>()),
                new Variable(AssignedVariable, new HashSet<>()));

        // Determine Type[s] of taint present in the variable

        String[] Values;
        // Check if it is a Phi() assignment
        if (AssignedValue.startsWith("Phi(") && AssignedValue.endsWith(")"))
            Values = AssignedValue.substring(4,AssignedValue.length()-1).split(",");

        else
            Values = new String[] {AssignedValue};

        // For each of the potential values, see if it is tainted. If it is, the AssignedVariable could be tainted, so pass it on.
        for (String Value : Values){
            Variable key = new Variable(Value, new HashSet<>());
            if (inputTaint.containsKey(key) && inputTaint.get(key).isTainted()) {
                AssignedVar.setAllTainted(inputTaint.get(key).getTaints());
            }
        }


        // Add the Variable to the Taint Set if it has been tainted
        if (AssignedVar.isTainted())
            inputTaint.put(AssignedVar, AssignedVar);

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
