
Block#1
    Expr_ArrayDimFetch
        var: Var#1<$_GET>
        dim: LITERAL('str')
        result: Var#2
    Expr_Assign
        var: Var#3<$a>
        expr: Var#2
        result: Var#4
    Expr_Print
        expr: Var#3<$a>
        result: Var#5
    Expr_ConcatList
        list[0]: LITERAL('
        ')
        list[1]: Var#3<$a>
        list[2]: LITERAL('
        ')
        result: Var#6
    Expr_Print
        expr: Var#6
        result: Var#7
    Expr_ArrayDimFetch
        var: Var#1<$_GET>
        dim: LITERAL('anotherstr')
        result: Var#8
    Expr_Print
        expr: Var#8
        result: Var#9
    Terminal_Return
