package TaintAnalysisComponents;

import java.util.*;

public class Variable {
    private final String VariableName;
    private final HashSet<TaintType> Taints;

    private Optional<HashSet<Variable>> TaintDependencies;
    private final HashSet<Variable> TaintedFrom;

    private final boolean isSource;

    public Variable(String VariableName, Collection<TaintType> Tainted) {
        // assumption that calling this with a sources' variable name chooses the programmers Tainted set over the default, which is all tainted.
        this.VariableName = VariableName;
        this.Taints = new HashSet<>(Tainted);

        TaintDependencies = Optional.empty();
        isSource = false;
        TaintedFrom = new HashSet<>();
    }

    public Variable(String VariableName) {
        this.VariableName = VariableName;
        this.Taints = new HashSet<>();
        if (isRealVariable() && computeSource(VariableName)) {
            Collections.addAll(Taints, TaintType.values());
            isSource = true;
        }

        else
            isSource = false;

        TaintDependencies = Optional.empty();
        TaintedFrom = new HashSet<>();
    }

    private boolean computeSource(String variableName) {
        // returns true if the variable name matches a sources name.
        return Arrays.stream(Source.values()).anyMatch(x -> variableName.endsWith("<" + x.name() + ">"));
    }

    private boolean TaintDependencyCycle(Variable DependentVariable){
        // implement cycle detection algorithm
        // If the current variable is in the DependentVariable subtree,
        // a cycle may occur if added.

        // If DependentVariable is in the tree
        Stack<Variable> DFSStack = new Stack<>();
        HashSet<Variable> elements = new HashSet<>();
        DFSStack.push(DependentVariable);
        while (!DFSStack.isEmpty() && !elements.contains(this)) {
            Variable CurrentVariable = DFSStack.pop();
            elements.add(CurrentVariable);

            if (CurrentVariable.TaintDependencies.isPresent())
                for (Variable var : CurrentVariable.TaintDependencies.get())
                    if (!elements.contains(var))
                        DFSStack.push(var);
        }

        return elements.contains(this);
    }

    public void markTaintDependency(Variable DependentVariable){
        // Enforces that Tainted(Variable) => Tainted(DependentVariable)
        if (TaintDependencies.isEmpty())
            TaintDependencies = Optional.of(new HashSet<>());

        // check for cycles
        if (!TaintDependencyCycle(DependentVariable))
            TaintDependencies.get().add(DependentVariable);
    }

    public boolean isSource() {
        return isSource;
    }
    public String getVariableName() {
        return VariableName;
    }

    public boolean hasTainted(TaintType taint){
        return Taints.contains(taint) ||
                TaintDependencies.isPresent() && TaintDependencies.get().stream().anyMatch(x -> x.hasTainted(taint)) ;
    }

    public boolean hasTainted(Collection<? extends TaintType> taints){
        return taints.stream().allMatch(this::hasTainted);
    }

    public boolean isTainted(){
        return !Taints.isEmpty() ||
                TaintDependencies.isPresent() && TaintDependencies.get().stream().anyMatch(Variable::isTainted);
    }

    public HashSet<TaintType> getTaints(){
        if (TaintDependencies.isEmpty())
            return new HashSet<>(Taints);

        HashSet<TaintType> returnTaints = new HashSet<>(Taints);
        TaintDependencies.get().forEach(x -> returnTaints.addAll(x.getTaints()));
        return returnTaints;
    }

    public void setTainted(TaintType newTaint){
        Taints.add(newTaint);
    }

    public void setAllTainted(Collection<? extends TaintType> newTaints){
        Taints.addAll(newTaints);
    }

    // do not clear dependent taints maintain over approximation.

    public void clearTainted(TaintType TaintToRemove){
        Taints.remove(TaintToRemove);
    }

    public void clearAllTainted(Collection<? extends TaintType> TaintsToRemove){
        Taints.removeAll(TaintsToRemove);
    }

    public void TaintedFrom(HashSet<Variable> VariableTaintedBy) {
        TaintedFrom.addAll(VariableTaintedBy);
    }

    public void TaintedFrom(Variable VariableTaintedBy) {
        TaintedFrom.add(VariableTaintedBy);
    }

    public HashSet<Variable> getTaintedFrom() {
        if (TaintDependencies.isEmpty())
            return new HashSet<>(TaintedFrom);

        HashSet<Variable> returnTaintedFrom = new HashSet<>(TaintedFrom);

        TaintDependencies.get().forEach(x -> returnTaintedFrom.addAll(x.getTaintedFrom()));
        // ensure we do not have cyclic tainted from
        returnTaintedFrom.remove(this);
        return returnTaintedFrom;
    }

    public boolean isRealVariable() {
        // Variables which are not just from the SSA form are styled like Var#N<$variablename>, and the custom source functions are denoted SourceFunction<functionName>
        return VariableName.contains("<$") || VariableName.contains("SourceFunction<");
    }

    public String getRealVariableName() {
        // returns the non SSA form of the variable name if possible, else returns variable name.

        return isRealVariable() ? VariableName.substring(VariableName.indexOf("<") + 1, VariableName.lastIndexOf(">")) : VariableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return VariableName.equals(variable.VariableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(VariableName);
    }
}
