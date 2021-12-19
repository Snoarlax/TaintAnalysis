package Statement.Expression;

import Statement.StatementType;

public enum ExpressionType {
    Expr_Assign,
    Expr_ConcatList,
    Expr_BinaryOp_Concat,
    Expr_ArrayDimFetch,
    Expr_Default;

    public static ExpressionType ParseExpressionType(String rawStatement) {
        // Determine type of statement given a raw statement
        for (ExpressionType type : values())
            if (type.name().equals(rawStatement.split("_",2)[0]))
                return type;

        return Expr_Default;
    }
}