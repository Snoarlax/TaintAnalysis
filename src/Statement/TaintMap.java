package Statement;

import Statement.Variable;

import java.util.HashMap;

public class TaintMap extends HashMap<Variable, Variable> {

    public TaintMap() {
        super();
    }

    public TaintMap(HashMap<Variable, Variable> TaintMap) {
        super(TaintMap);
    }

    public Variable get(String VariableName) {
        Variable var = new Variable(VariableName);
        return getOrDefault(var,var);
    }

    public Variable get(Variable var) {
        return getOrDefault(var,var);
    }

    public boolean isTainted(String VariableName) {
        return get(VariableName).isTainted();
    }

    public boolean isTainted(Variable var) {
        return get(var).isTainted();
    }

    public void put(Variable var) {
        put(var, var);
    }
}
