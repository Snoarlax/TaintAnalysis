package Statement;

import java.util.HashSet;

public class PropertyStatement extends Statement {
    private final String PropertyName;
    private final String PropertyValue;

    public PropertyStatement(String Name, String Value){
        PropertyName = Name;
        PropertyValue = Value;
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint, String[] Arguments) {
        // Property statements do not affect Taint
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.PROPERTY;
    }

    @Override
    public boolean isTaintedSink() {
        return false;
    }

    @Override
    public HashSet<Variable> TaintedBy() {
        return null;
    }

    public String getPropertyName() {
        return PropertyName;
    }


    public String getPropertyValue() {
        return PropertyValue;
    }
}
