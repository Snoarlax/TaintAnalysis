package Statement;

import java.util.HashSet;

public class AssignmentStatement extends Statement{
    private final String AssignedVariable;
    private final String AssignedValue;

    public AssignmentStatement(String assignedVariable, String assignedValue) {
        AssignedVariable = assignedVariable;
        AssignedValue = assignedValue;
    }

    @Override
    HashSet<Variable> computeTaintFromInput(HashSet<Variable> inputTaint) {
        // TODO implement taint transfer for a assignment
        return null;
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
