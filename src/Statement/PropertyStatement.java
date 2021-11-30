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
    HashSet<Variable> computeTaintFromInput(HashSet<Variable> inputTaint) {
        // TODO: implement taint transfer for a Property Node
        return null;
    }

    @Override
    public StatementType getStatementType() {
        return StatementType.PROPERTY;
    }

    public String getPropertyName() {
        return PropertyName;
    }

    public String getPropertyValue() {
        return PropertyValue;
    }
}
