package Statement;

public enum StatementType {
    DEFAULT,
    PROPERTY,
    ASSIGNMENT,
    EXPRESSION,
    STATEMENT,
    TERMINAL;

    public static StatementType ParseStatementType(String rawStatement) {
        // Determine type of statement given a raw statement
        // TODO: implement means of finding all types
        if (rawStatement.split(":",2)[0].equals("Parent"))
            return PROPERTY;
        else if (rawStatement.split("_",2)[0].equals("Expr"))
            return EXPRESSION;
        else if (rawStatement.split("=", 2).length == 2)
            return ASSIGNMENT;
        else if (rawStatement.split("_",2)[0].equals("Stmt"))
            return STATEMENT;
        else if (rawStatement.split("_",2)[0].equals("Terminal"))
            return TERMINAL;

        return DEFAULT;
    }

    public static PropertyStatement ConstructPropertyStatement(String rawStatement) {
        String PropertyName = rawStatement.split(": ", 2)[0];
        String PropertyValue = rawStatement.split(": ", 2)[1];
        return new PropertyStatement(PropertyName, PropertyValue);
    }

    public static AssignmentStatement ConstructAssignmentStatement(String rawStatement) {
        String AssignedVariable = rawStatement.split(" = ", 2)[0];
        String AssignedValue = rawStatement.split(" = ", 2)[1];
        return new AssignmentStatement(AssignedVariable, AssignedValue);
    }

    public static ExpressionStatement ConstructExpressionStatement(String rawStatement) {
        return new ExpressionStatement(rawStatement);
    }

    public static StatementStatement ConstructStatementStatement(String rawStatement) {
        return new StatementStatement(rawStatement);
    }

    public static TerminalStatement ConstructTerminalStatement(String rawStatement) {
        return new TerminalStatement(rawStatement);
    }
}

