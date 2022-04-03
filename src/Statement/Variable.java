package Statement;

import Statement.Expression.Sources;

import java.util.*;

public class Variable {
    private final String VariableName;
    private final HashSet<TaintType> Taints;


    private final HashSet<Variable> TaintedFrom;

    public Variable(String VariableName, Collection<TaintType> Tainted) {
        // assumption that calling this with a sources' variable name chooses the programmers Tainted set over the default, which is all tainted.
        this.VariableName = VariableName;
        this.Taints = new HashSet<>(Tainted);

        TaintedFrom = new HashSet<>();
    }

    public Variable(String VariableName) {
        this.VariableName = VariableName;
        this.Taints = new HashSet<>();
        if (computeSource(VariableName))
            Collections.addAll(Taints, TaintType.values());

        TaintedFrom = new HashSet<>();
    }

    private boolean computeSource(String variableName) {
        // returns true if the variable name matches a sources name.
        return Arrays.stream(Sources.values()).anyMatch(x -> variableName.endsWith("<" + x.name() + ">"));
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

    public void setAllTainted(Collection<? extends TaintType> newTaints){
        Taints.addAll(newTaints);
    }

    public void clearTainted(TaintType TaintToRemove){
        Taints.remove(TaintToRemove);
    }

    public void clearAllTainted(Collection<? extends TaintType> TaintsToRemove){
        Taints.removeAll(TaintsToRemove);
    }

    public void TaintedFrom(Variable VariableTaintedBy) {TaintedFrom.add(VariableTaintedBy);}

    public void TaintedFrom(HashSet<Variable> VariableTaintedBy) {TaintedFrom.addAll(VariableTaintedBy);}

    public HashSet<Variable> getTaintedFrom() {
        return new HashSet<>(TaintedFrom);
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
