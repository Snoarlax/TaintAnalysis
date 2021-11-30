package Statement;

public class Variable {
    // TODO: implement flags for different types of taint

    private final String VariableName;

    Variable(String VariableName, boolean Tainted) {
        this.VariableName = VariableName;
    }

    public String getVariableName() {
        return VariableName;
    }
}
