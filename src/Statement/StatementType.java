package Statement;

import Statement.Expression.*;
import org.junit.jupiter.params.provider.Arguments;

public enum StatementType {
    Expr,
    Stmt,
    Terminal,
    DEFAULT,
    PROPERTY,
    ASSIGNMENT;

    public static StatementType ParseStatementType(String rawStatement) {
        // Determine type of statement given a raw statement
        if (rawStatement.split("=", 2).length == 2)
            return ASSIGNMENT;
        if (rawStatement.split(":", 2)[0].equals("Parent"))
            return PROPERTY;

        for (StatementType type : values())
            if (type.name().equals(rawStatement.split("_",2)[0]))
                return type;

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

    public static ExpressionStatement ConstructExpressionStatement(String rawStatement, String[] Arguments) {
        ExpressionType ExprType = ExpressionType.ParseExpressionType(rawStatement);

        if (ExprType == ExpressionType.Expr_ConcatList){
            return new Expr_ConcatList(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_Assign){
            return new Expr_Assign(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_BinaryOp_Concat){
            return new Expr_BinaryOp_Concat(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_ArrayDimFetch){
            return new Expr_ArrayDimFetch(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_FuncCall){
            return new Expr_FuncCall(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_MethodCall){
            return new Expr_MethodCall(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_Eval) {
            return new Expr_Eval(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_Print) {
            return new Expr_Print(rawStatement, Arguments);
        }
        else if (ExprType == ExpressionType.Expr_Include) {
            return new Expr_Include(rawStatement, Arguments);
        }

        return new Expr_Default(rawStatement, Arguments);
    }

    public static StatementStatement ConstructStatementStatement(String rawStatement, String[] Arguments) {
        return new StatementStatement(rawStatement, Arguments);
    }

    public static TerminalStatement ConstructTerminalStatement(String rawStatement, String[] Arguments) {
        return new TerminalStatement(rawStatement, Arguments);
    }

    public static DefaultStatement ConstructDefaultStatement(String rawStatement, String[] Arguments) {
        return new DefaultStatement(rawStatement, Arguments);
    }
}

