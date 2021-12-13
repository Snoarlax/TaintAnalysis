package Statement;

import java.util.HashMap;
import java.util.HashSet;

public class PropertyStatement extends Statement {
    private final String PropertyName;
    private final String PropertyValue;

    public PropertyStatement(String Name, String Value){
        PropertyName = Name;
        PropertyValue = Value;
    }

    @Override
    public void computeTaintFromInput(HashMap<Variable,Variable> inputTaint, String[] Arguments) {
        // Property statements do not affect Taint
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
