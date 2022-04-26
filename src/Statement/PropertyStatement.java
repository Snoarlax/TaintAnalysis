package Statement;

import TaintAnalysisComponents.TaintMap;

public class PropertyStatement extends Statement {
    private final String PropertyName;
    private final String PropertyValue;

    public PropertyStatement(String Name, String Value){
        super("", new String[0]);
        PropertyName = Name;
        PropertyValue = Value;
    }

    @Override
    public void computeTaintFromInput(TaintMap inputTaint) {
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
