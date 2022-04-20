package TaintAnalysisComponents;

import java.util.HashMap;

public class TaintMap extends HashMap<String, Variable> {

    public TaintMap() {
        super();
    }

    public TaintMap(HashMap<String, Variable> TaintMap) {
        super(TaintMap);
    }

    public Variable get(String VariableName) {
        return getOrDefault(VariableName,new Variable(VariableName));
    }

    // shorthand for getting variable by name first
    public boolean isTainted(String VariableName) {
        return get(VariableName).isTainted();
    }

    public void put(Variable var) {
        put(var.getVariableName(), var);
    }
}
