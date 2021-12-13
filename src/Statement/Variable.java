package Statement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class Variable {
    // TODO: implement flags for different types of taint

    private final String VariableName;
    private final HashSet<TaintType> Taints;

    public Variable(String VariableName, Collection<TaintType> Tainted) {
        this.VariableName = VariableName;
        this.Taints = new HashSet<>(Tainted);
    }

    public String getVariableName() {
        return VariableName;
    }

    public boolean hasTainted(TaintType taint){
        return Taints.contains(taint);
    }

    public boolean isTainted(){
        return !Taints.isEmpty();
    }

    public HashSet<TaintType> getTaints(){
        return new HashSet<>(Taints);
    }

    public void setTainted(TaintType newTaint){
        Taints.add(newTaint);
    }

    public void setAllTainted(Collection<? extends TaintType> newTaint){
        Taints.addAll(newTaint);
    }

    @Override
    public boolean equals(Object o) {
        // State equality is equivalent to equality in the name of the Variable, not the taint status.
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(VariableName, variable.VariableName);
    }

    @Override
    public int hashCode() {
        // State equality is equivalent to equality in the name of the Variable, not the taint status.
        return Objects.hash(VariableName);
    }
}
