public class Variable {
    // TODO: implement different types of taint

    private boolean Tainted;
    private final String VariableName;

    Variable(String VariableName, boolean Tainted) {
        this.VariableName = VariableName;
        this.Tainted = Tainted;
    }

    public String getVariableName() {
        return VariableName;
    }

    public boolean isTainted() {
        return Tainted;
    }

    public void setTaint(boolean tainted) {
        Tainted = tainted;
    }
}
