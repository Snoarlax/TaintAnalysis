import Statement.Expression.*;
import Statement.Statement;
import TaintAnalysisComponents.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import Statement.*;

import java.util.*;

public class UnitTests {
    @Test
    @DisplayName("Tests that CFGParser returns a block for each block in the .dat file")
    public void CFGParser_TestParsing() throws InvalidFileException {
        // Arrange
        CFGLexer parser = new CFGLexer("tests/test1.dat");
        // Act
        int NoBlocks = parser.getBlocks().length;
        // Assert
        assertEquals(NoBlocks, 4);
    }

    @Test
    @DisplayName("Tests that CFGParser throws an error on the .dat file being invalid")
    public void CFGParser_TestInvalidFileException() {
        // Arrange
        String expectedMessage = "The file is not a valid CFG .dat file. ";

        // Act
        Exception exception = assertThrows(InvalidFileException.class, () -> {
            CFGLexer parser = new CFGLexer("tests/InvalidFile.dat");
        });
        String actualMessage = exception.getMessage();

        // Assert
        assertEquals(actualMessage, expectedMessage);

    }

    @Test
    @DisplayName("Test getBlock from CFGParser")
    public void CFGParser_TestGetBlock() throws InvalidFileException {
        // Arrange
        CFGLexer parser = new CFGLexer("tests/test1.dat");
        // Act
        Block Block2 = parser.getBlock("Block#2");
        // Assert
        assertEquals(Block2.getBlockName(), "Block#2");
    }

    @Test
    @DisplayName("Test getSucc from CFGParser")
    public void CFGParser_TestGetSucc() throws InvalidFileException {
        // Arrange
        CFGLexer parser = new CFGLexer("tests/test1.dat");
        // Act
        Block Block1 = parser.getBlock("Block#1");
        Block[] Successors = Block1.getSucc();

        // Assert
        assertTrue(Successors[0].getBlockName().equals("Block#2") && Successors[1].getBlockName().equals("Block#3") || Successors[0].getBlockName().equals("Block#3") && Successors[1].getBlockName().equals("Block#2"));
    }

    @Test
    @DisplayName("Test getPred from CFGParser")
    public void CFGParser_TestGetPred() throws InvalidFileException {
        // Arrange
        CFGLexer parser = new CFGLexer("tests/test1.dat");
        // Act
        Block Block4 = parser.getBlock("Block#4");
        Block[] Predecessors = Block4.getPred();

        // Assert
        assertTrue(Predecessors[0].getBlockName().equals("Block#2") && Predecessors[1].getBlockName().equals("Block#3") || Predecessors[0].getBlockName().equals("Block#3") && Predecessors[1].getBlockName().equals("Block#2"));
    }

    @Test
    @DisplayName("Test GetStatementType from Statement.StatementType")
    public void StatementType_TestGetStatementType() throws InvalidFileException {
        // Arrange
        CFGLexer parser = new CFGLexer("tests/test1.dat");

        // Act
        Block Block3 = parser.getBlock("Block#3");
        Statement[] statements = Block3.getStatements();
        StatementType[] types = new StatementType[statements.length];
        for (int i = 0; i < statements.length; i++)
            types[i] = statements[i].getStatementType();

        // Assert
        assertTrue(types[0] == StatementType.PROPERTY && types[1] == StatementType.ASSIGNMENT && types[2] == StatementType.Expr && types[3] == StatementType.Terminal && types[4] == StatementType.Stmt);
    }

    @Test
    @DisplayName("Test ParseExpressionType from Expression.ExpressionType")
    public void ExpressionType_TestParseExpressionType() throws InvalidFileException {

        // Arrange
        CFGLexer parser = new CFGLexer("tests/test2.dat");

        // Act
        Block Block = parser.getBlock("Block#1");
        Statement[] statements = Block.getStatements();
        ExpressionType[] types = new ExpressionType[statements.length];
        for (int i = 0; i < statements.length; i++) {
            if (statements[i].getStatementType() == StatementType.Expr)
                types[i] = ((ExpressionStatement) statements[i]).getExpressionType();
        }
        // Assert
        assertTrue(types[0] == ExpressionType.Expr_Assign && types[1] == ExpressionType.Expr_BinaryOp_Concat && types[2] == ExpressionType.Expr_ConcatList && types[3] == ExpressionType.Expr_ArrayDimFetch && types[4] == ExpressionType.Expr_Default);
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment without a Phi function and tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_noPhi_WithTaint(){
        // Arrange
        AssignmentStatement StatementWithTaint = new AssignmentStatement("Var1", "Var2");

        TaintMap TaintMap = new TaintMap();
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Var2.setTainted(TaintType.XSS);

        TaintMap.put("Var2", Var2);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertTrue(TaintMap.isTainted("Var1"));
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment without a Phi function and no tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_noPhi_NoTaint(){
        // Arrange
        AssignmentStatement StatementWithNoTaint = new AssignmentStatement("Var1", "Var2");
        TaintMap TaintMap = new TaintMap();
        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertFalse(TaintMap.isTainted("Var1"));
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment with a Phi function and tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_Phi_WithTaint(){
        // Arrange
        AssignmentStatement StatementWithTaint = new AssignmentStatement("Var1", "Phi(Var2, Var3, Var4, Var5)");

        TaintMap TaintMap = new TaintMap();
        Variable Var2 = new Variable("Var2", new HashSet<>());

        Var2.setTainted(TaintType.XSS);
        TaintMap.put("Var2", Var2);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertTrue(TaintMap.isTainted("Var1"));
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment with a Phi function and no tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_Phi_NoTaint(){
        // Arrange
        AssignmentStatement StatementWithNoTaint = new AssignmentStatement("Var1", "Phi(Var2, Var3, Var4, Var5)");

        TaintMap TaintMap = new TaintMap();

        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertFalse(TaintMap.isTainted("Var1"));
    }

    @Test
    @DisplayName("Found a bug when including new Lines in Literals, this checks a file with the bug to check for correct parsing. ")
    public void Test_NewLineInLiteral_BugFix() throws InvalidFileException {
        // Upon Experimenting with the bug, it seems you cannot inject an injection for this bug that results in a normally parsed program (See ExprTest.dat for my attempt,
        // Created by parsing [ echo $a."fake')\nFakeArgument : LITERAL('FakeValue"; ]

        // Arrange
        CFGLexer parser = new CFGLexer("tests/ExprTest.dat");
        // Act + Assert
        for (Block block : parser.getBlocks()){
            for (Statement statement : block.getStatements()){
                String[] arguments = statement.getArguments();
                for (String argument : arguments){
                    // Check each argument's value, if it starts with LITERAL(' it must end with ')
                    String value = argument.split(": ",2)[1];
                    if (value.startsWith("LITERAL('")){
                        if (!value.endsWith("')"))
                            Assert.fail();
                    }
                }
            }
        }
    }


    @Test
    @DisplayName("Check that Expr_BinaryOp_Concat passes on taint correctly when given tainted arguments")
    public void ExpressionStatement_ExprBinaryOpConcat_computeTaintFromInput_WithTaintedArguments() {
        // Arrange

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1");

        Var1.setTainted(TaintType.XSS);
        TaintMap.put("Var1", Var1);

        String[] Arguments = new String[]{"left: Var1","right: LITERAL('')", "result: Var2"};
        ExpressionStatement StatementWithTaint = new Expr_BinaryOp_Concat("Expr_BinaryOp_Concat", Arguments);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_BinaryOp_Concat passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprBinaryOpConcat_computeTaintFromInput_NoTaintedArguments() {
        // Arrange
        TaintMap TaintMap = new TaintMap();
        Variable Var2 = new Variable("Var2");
        String[] Arguments = new String[]{"left: Var1","right: LITERAL('')", "result: Var2"};
        ExpressionStatement StatementWithNoTaint = new Expr_BinaryOp_Concat("Expr_BinaryOp_Concat", Arguments);


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_ConcatList passes on taint correctly when given tainted arguments")
    public void ExpressionStatement_ExprConcatList_computeTaintFromInput_WithTaintedArguments() {
        // Arrange

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1");

        Var1.setTainted(TaintType.XSS);
        TaintMap.put("Var1", Var1);

        String[] Arguments = new String[]{"list[0]: Var1","list[1]: LITERAL('')","list[0]: Var2", "result: Var3"};
        ExpressionStatement StatementWithTaint = new Expr_ConcatList("Expr_ConcatList", Arguments);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertTrue(TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_ConcatList passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprConcatList_computeTaintFromInput_NoTaintedArguments() {
        // Arrange
        TaintMap TaintMap = new TaintMap();
        String[] Arguments = new String[]{"list[0]: Var1","list[1]: LITERAL('')","list[0]: Var2", "result: Var3"};
        ExpressionStatement StatementWithNoTaint = new Expr_ConcatList("Expr_ConcatList", Arguments);

        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertFalse(TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given tainted arguments")
    public void ExpressionStatement_ExprAssign_computeTaintFromInput_WithTaintedArguments() {
        // Arrange

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1", new HashSet<>());

        Var1.setTainted(TaintType.XSS);
        TaintMap.put("Var1", Var1);

        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};
        ExpressionStatement StatementWithTaint = new Expr_Assign("Expr_Assign", Arguments);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertTrue(TaintMap.isTainted("Var2") &&
                TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprAssign_computeTaintFromInput_NoTaintedArguments() {
        // Arrange

        TaintMap TaintMap = new TaintMap();

        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};
        ExpressionStatement StatementWithNoTaint = new Expr_ConcatList("Expr_ConcatList", Arguments);


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertFalse(TaintMap.isTainted("Var2")
               || TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given untainted arguments and already tainted variables")
    public void ExpressionStatement_ExprAssign_computeTaintFromInput_TaintedResults() {
        // Arrange

        TaintMap TaintMap = new TaintMap();
        Variable Var2 = new Variable("Var2");


        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};
        ExpressionStatement StatementWithNoTaint = new Expr_Assign("Expr_Assign", Arguments);

        Var2.setTainted(TaintType.XSS);
        TaintMap.put("Var2", Var2);

        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertTrue(TaintMap.isTainted("Var2") && !TaintMap.isTainted("Var3") && !TaintMap.isTainted("Var1"));
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as tainted if given a tainted argument. ")
    public void TerminalStatement_computeTaintFromInput_CorrectlyRegistersTaintedArguments() {
        // Arrange

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1", new HashSet<>());

        String[] Arguments = new String[]{"expr: Var1"};
        TerminalStatement StatementWithTaint = new TerminalStatement("Terminal_Echo", Arguments);

        Var1.setTainted(TaintType.XSS);
        TaintMap.put("Var1", Var1);

        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertTrue(StatementWithTaint.isTainted());
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as untainted if given an untainted argument. ")
    public void TerminalStatement_computeTaintFromInput_CorrectlyRegistersUntaintedArguments() {
        // Arrange

        TaintMap TaintMap = new TaintMap();
        String[] Arguments = new String[]{"expr: Var1"};
        TerminalStatement StatementWithTaint = new TerminalStatement("Terminal_Echo", Arguments);

        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap);

        // Assert
        assertFalse(StatementWithTaint.isTainted());
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as a sink if it is Terminal_Echo. ")
    public void TerminalStatement_TerminalEcho_MarkedAs_Sink() {
        // Arrange + Act
        TerminalStatement Statement = new TerminalStatement("Terminal_Echo", new String[] {});

        // Assert
        assertTrue(Statement.isSink());
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as a not a sink if it is not Terminal_Echo. ")
    public void TerminalStatement_NotTerminalEcho_NotMarkedAs_Sink() {
        // Arrange + Act
        TerminalStatement Statement = new TerminalStatement("Terminal_NotEcho", new String[] {});

        // Assert
        assertFalse(Statement.isSink());
    }

    @Test
    @DisplayName("Check that Expr_ArrayDimFetch correctly assigns sources. ")
    public void ExprArrayDimFetch_MarksOnSource() {
        // Arrange
        String[] Arguments = new String[] {"var: Var#1<$_GET>","dim: LITERAL('str')","result: Var#2"};
        TaintMap TaintMap = new TaintMap();
        Expr_ArrayDimFetch SourceStatement = new Expr_ArrayDimFetch("Expr_ArrayDimFetch", Arguments);

        // Act
        SourceStatement.computeTaintFromInput(TaintMap);
        // Assert
        assertTrue(TaintMap.isTainted("Var#2"));
    }

    @Test
    @DisplayName("Check that Expr_ArrayDimFetch correctly assigns non sources. ")
    public void ExprArrayDimFetch_NoMarkOnNonSource() {
        // Arrange
        String[] Arguments = new String[] {"var: Var#1<SAFE>","dim: LITERAL('str')","result: Var#2"};
        TaintMap TaintMap = new TaintMap();
        Expr_ArrayDimFetch SourceStatement = new Expr_ArrayDimFetch("Expr_ArrayDimFetch", Arguments);

        // Act
        SourceStatement.computeTaintFromInput(TaintMap);
        // Assert
        assertFalse(TaintMap.isTainted("Var#2"));
    }

    @Test
    @DisplayName("Check that Expr_Print computeTaintFromInput works correctly on a tainted argument. ")
    public void ExprPrint_computeTaintFromInput_withTaint() {
        // Arrange
        String[] Arguments = new String[] { "expr: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);
        TaintMap.put("Var1", Var1);

        Expr_Print PrintStatement = new Expr_Print("Expr_Print", Arguments);

        // Act
        PrintStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }
    @Test
    @DisplayName("Check that Expr_Include computeTaintFromInput works correctly on a tainted argument. ")
    public void ExprInclude_computeTaintFromInput_withTaint() {
        // Arrange
        String[] Arguments = new String[] { "expr: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.DIRECTORY);
        TaintMap.put("Var1", Var1);

        Expr_Include IncludeStatement = new Expr_Include("Expr_Include", Arguments);

        // Act
        IncludeStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_Eval computeTaintFromInput works correctly on a tainted argument. ")
    public void ExprEval_computeTaintFromInput_withTaint() {
        // Arrange
        String[] Arguments = new String[] { "expr: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.INJECTION);
        TaintMap.put("Var1", Var1);

        Expr_Eval EvalStatement = new Expr_Eval("Expr_Eval", Arguments);

        // Act
        EvalStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_Print computeTaintFromInput works correctly on a untainted argument. ")
    public void ExprPrint_computeTaintFromInput_noTaint() {
        // Arrange
        String[] Arguments = new String[] { "expr: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();
        Expr_Print PrintStatement = new Expr_Print("Expr_Print", Arguments);

        // Act
        PrintStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_Include computeTaintFromInput works correctly on a untainted argument. ")
    public void ExprInclude_computeTaintFromInput_noTaint() {
        // Arrange
        String[] Arguments = new String[] { "expr: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();
        Expr_Include IncludeStatement = new Expr_Include("Expr_Include", Arguments);

        // Act
        IncludeStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_Eval computeTaintFromInput works correctly on a untainted argument. ")
    public void ExprEval_computeTaintFromInput_noTaint() {
        // Arrange
        String[] Arguments = new String[] { "expr: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();
        Expr_Eval EvalStatement = new Expr_Eval("Expr_Eval", Arguments);

        // Act
        EvalStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_FuncCall computeTaintFromInput works correctly on a tainted argument. ")
    public void FuncCall_computeTaintFromInput_withTaint() {
        // Arrange
        String[] Arguments = new String[] { "name: LITERAL('function')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);
        TaintMap.put("Var1", Var1);

        Expr_FuncCall FuncCall = new Expr_FuncCall("Expr_FuncCall", Arguments);

        // Act
        FuncCall.computeTaintFromInput(TaintMap);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_Print computeTaintFromInput works correctly on a untainted argument. ")
    public void FuncCall_computeTaintFromInput_noTaint() {
        // Arrange
        String[] Arguments = new String[] { "name: LITERAL('function')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();
        Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);

        // Act
        FuncCallStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that FuncCalls get correctly marked as sinks. ")
    public void FuncCall_MarkedAsSink() {
        // Arrange
        String[] Arguments = new String[] { "name: LITERAL('exec')" };
        // Act
        Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
        // Assert
        assertTrue(FuncCallStatement.isSink());

    }

    @Test
    @DisplayName("Check that FuncCalls get correctly marked as not a sink. ")
    public void FuncCall_NotMarkedAsSink() {
        // Arrange
        String[] Arguments = new String[] { "name: LITERAL('notsink')" };
        // Act
        Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
        // Assert
        assertFalse(FuncCallStatement.isSink());

    }

    @Test
    @DisplayName("Check that FuncCalls over all the sinks are marked as sinks. ")
    public void AllSinks_FuncCall_MarkedAsSink() {
        // Arrange
        String[] Sinks = new String[] {"eval","include","include_once","require","require_once","echo","print","printf","file_put_contents","fopen","opendir","file","mysql_query","mysqli_query","sqlite_query","sqlite_single_query","oci_parse","query","prepare","system","exec","proc_open","passthru","shell_exec"};
        for (String sink : Sinks) {
            String[] Arguments = new String[] { "name: LITERAL('" + sink + "')" };
            // Act
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Assert
            assertTrue(FuncCallStatement.isSink());
        }
    }

    @Test
    @DisplayName("Check that FuncCalls over all the sinks are marked as the right sink. ")
    public void AllSinks_FuncCall_MarkedAsCorrectSink() {
        // Arrange
        String[] SinksList = new String[] {"eval","include","include_once","require","require_once","echo","print","printf","file_put_contents","fopen","opendir","file","mysql_query","mysqli_query","sqlite_query","sqlite_single_query","oci_parse","query","prepare","system","exec","proc_open","passthru","shell_exec"};
        for (String sink : SinksList) {
            String[] Arguments = new String[] { "name: LITERAL('" + sink + "')" };
            // Act
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Assert
            assertEquals(FuncCallStatement.getSinkType(), Sink.valueOf(sink));
        }
    }

    @Test
    @DisplayName("Check that Expr_MethodCall computeTaintFromInput works correctly on a tainted argument. ")
    public void MethodCall_computeTaintFromInput_withTaint() {
        // Arrange
        String[] Arguments = new String[] { "var: var","name: LITERAL('function')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);
        TaintMap.put("Var1", Var1);

        Expr_MethodCall MethodCall = new Expr_MethodCall("Expr_MethodCall", Arguments);

        // Act
        MethodCall.computeTaintFromInput(TaintMap);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_MethodCall computeTaintFromInput works correctly on a untainted argument. ")
    public void MethodCall_computeTaintFromInput_noTaint() {
        // Arrange
        String[] Arguments = new String[] { "var: var","name: LITERAL('function')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();
        Expr_MethodCall MethodCallStatement = new Expr_MethodCall("Expr_MethodCall", Arguments);

        // Act
        MethodCallStatement.computeTaintFromInput(TaintMap);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that FuncCalls get correctly marked as sinks. ")
    public void MethodCall_MarkedAsSink() {
        // Arrange
        String[] Arguments = new String[] { "var: var","name: LITERAL('exec')" };
        // Act
        Expr_MethodCall MethodCallStatement = new Expr_MethodCall("Expr_MethodCall", Arguments);
        // Assert
        assertTrue(MethodCallStatement.isSink());

    }

    @Test
    @DisplayName("Check that MethodCalls get correctly marked as not a sink. ")
    public void MethodCall_NotMarkedAsSink() {
        // Arrange
        String[] Arguments = new String[] { "var: Var#2<$conn>","name: LITERAL('notsink')" };
        // Act
        Expr_MethodCall MethodCallStatement = new Expr_MethodCall("Expr_MethodCall", Arguments);
        // Assert
        assertFalse(MethodCallStatement.isSink());

    }

    @Test
    @DisplayName("Check that MethodCalls over all the sinks are marked as sinks. ")
    public void AllSinks_MethodCall_MarkedAsSink() {
        // Arrange
        String[] Sinks = new String[] {"eval","include","include_once","require","require_once","echo","print","printf","file_put_contents","fopen","opendir","file","mysql_query","mysqli_query","sqlite_query","sqlite_single_query","oci_parse","query","prepare","system","exec","proc_open","passthru","shell_exec"};
        for (String sink : Sinks) {
            String[] Arguments = new String[] { "var: var","name: LITERAL('" + sink + "')" };
            // Act
            Expr_MethodCall MethodCallStatement = new Expr_MethodCall("Expr_MethodCall", Arguments);
            // Assert
            assertTrue(MethodCallStatement.isSink());
        }
    }

    @Test
    @DisplayName("Check that MethodCalls over all the sinks are marked as the right sink. ")
    public void AllSinks_MethodCall_MarkedAsCorrectSink() {
        // Arrange
        String[] SinksList = new String[] {"eval","include","include_once","require","require_once","echo","print","printf","file_put_contents","fopen","opendir","file","mysql_query","mysqli_query","sqlite_query","sqlite_single_query","oci_parse","query","prepare","system","exec","proc_open","passthru","shell_exec"};
        for (String sink : SinksList) {
            String[] Arguments = new String[] { "var: var","name: LITERAL('" + sink + "')" };
            // Act
            Expr_MethodCall MethodCallStatement = new Expr_MethodCall("Expr_MethodCall", Arguments);
            // Assert
            assertEquals(MethodCallStatement.getSinkType(), Sink.valueOf(sink));
        }
    }

    @Test
    @DisplayName("Check that getTaintTypes() works for different sinks. ")
    public void getTaintTypes_Sinks(){
        // Arrange
        Sink[] SinksToTest = new Sink[] {Sink.shell_exec, Sink.include, Sink.echo, Sink.mysql_query};
        TaintType[] TaintTypesToCheck = new TaintType[] {
            TaintType.INJECTION,
                    TaintType.DIRECTORY,
                    TaintType.XSS,
                    TaintType.SQLI
        };

        // ACT + ASSERT

        for (int i = 0; i < SinksToTest.length; i++)
            assertEquals(SinksToTest[i].getVulnerableTaint(), TaintTypesToCheck[i]);
    }

    @Test
    @DisplayName("Check the XSS sanitizations return the correct set of Sanitizations. ")
    public void Sanitizations_XSS_MarkedCorrectly() {
        // Arrange
        Sanitization[] XSSSanitizations = new Sanitization[] {Sanitization.htmlspecialchars, Sanitization.htmlentities};
        HashSet<TaintType> TaintsToRemove = new HashSet<>(List.of(TaintType.XSS));

        // Act + Assert
        assertTrue(Arrays.stream(XSSSanitizations).allMatch(x -> x.getTaintTypeSanitizations().equals(TaintsToRemove)));
    }

    @Test
    @DisplayName("Check the Injection sanitizations return the correct set of Sanitizations. ")
    public void Sanitizations_Injection_MarkedCorrectly() {
        // Arrange
        Sanitization[] InjectionSanitizations = new Sanitization[] {Sanitization.escapeshellcmd};
        HashSet<TaintType> TaintsToRemove = new HashSet<>(List.of(TaintType.INJECTION));

        // Act + Assert
        assertTrue(Arrays.stream(InjectionSanitizations).allMatch(x -> x.getTaintTypeSanitizations().equals(TaintsToRemove)));
    }

    @Test
    @DisplayName("Check the Traversal sanitizations return the correct set of Sanitizations. ")
    public void Sanitizations_Traversal_MarkedCorrectly() {
        // Arrange
        Sanitization[] TraversalSanitizations = new Sanitization[] {Sanitization.realpath};
        HashSet<TaintType> TaintsToRemove = new HashSet<>(List.of(TaintType.DIRECTORY));

        // Act + Assert
        assertTrue(Arrays.stream(TraversalSanitizations).allMatch(x -> x.getTaintTypeSanitizations().equals(TaintsToRemove)));
    }

    @Test
    @DisplayName("Check the SQLI sanitizations return the correct set of Sanitizations. ")
    public void Sanitizations_SQLI_MarkedCorrectly() {
        // Arrange
        Sanitization[] SQLISanitizations = new Sanitization[] {    Sanitization.addcslashes, //unsure
                Sanitization.addslashes,
                Sanitization.mysql_escape_string,
                Sanitization.mysql_real_escape_string,
                Sanitization.mysqli_escape_string,
                Sanitization.escape_string,
                Sanitization.mysqli_real_escape_string,
                Sanitization.real_escape_string,
                Sanitization.sqlite_escape_string,
                Sanitization.escapeString,
                Sanitization.quote };
        HashSet<TaintType> TaintsToRemove = new HashSet<>(List.of(TaintType.SQLI));

        // Act + Assert
        assertTrue(Arrays.stream(SQLISanitizations).allMatch(x -> x.getTaintTypeSanitizations().equals(TaintsToRemove)));
    }

    @Test
    @DisplayName("Check the sanitisation functions work for SQLI. ")
    public void Sanitizations_SQLI_Works() {
        // Arrange
        Sanitization[] SQLISanitizations = new Sanitization[] {    Sanitization.addcslashes, //unsure
                Sanitization.addslashes, //unsure,
                Sanitization.mysql_escape_string,
                Sanitization.mysql_real_escape_string,
                Sanitization.mysqli_escape_string,
                Sanitization.escape_string,
                Sanitization.mysqli_real_escape_string,
                Sanitization.real_escape_string,
                Sanitization.sqlite_escape_string,
                Sanitization.escapeString,
                Sanitization.quote };

        for (Sanitization SQLISan : SQLISanitizations) {
            TaintMap SQLITaintedMap = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.SQLI));
            SQLITaintedMap.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + SQLISan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(SQLITaintedMap);
            // Assert
            Assert.assertFalse(SQLITaintedMap.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check the sanitisation functions work for Traversal. ")
    public void Sanitizations_Traversal_Works() {
        // Arrange
        Sanitization[] DirectorySanitizations = new Sanitization[] {Sanitization.realpath};

        for (Sanitization TraversalSan : DirectorySanitizations) {
            TaintMap TraversalSanitization = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.DIRECTORY));
            TraversalSanitization.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + TraversalSan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(TraversalSanitization);
            // Assert
            Assert.assertFalse(TraversalSanitization.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check the sanitisation functions work for Injection. ")
    public void Sanitizations_Injection_Works() {
        // Arrange
        Sanitization[] InjectionSanitizations = new Sanitization[] {Sanitization.escapeshellcmd};

        for (Sanitization InjectionSan : InjectionSanitizations) {
            TaintMap InjectionTaintedMap = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.INJECTION));
            InjectionTaintedMap.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + InjectionSan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(InjectionTaintedMap);
            // Assert
            Assert.assertFalse(InjectionTaintedMap.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check the sanitisation functions work for XSS. ")
    public void Sanitizations_XSS_Works() {
        // Arrange
        Sanitization[] XSSSanitizations = new Sanitization[] {Sanitization.htmlspecialchars, Sanitization.htmlentities};

        for (Sanitization XSSsan : XSSSanitizations) {
            TaintMap XSSTaintedMap = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.XSS));
            XSSTaintedMap.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + XSSsan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(XSSTaintedMap);
            // Assert
            Assert.assertFalse(XSSTaintedMap.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check the sanitisation functions fails correctly for SQLI. ")
    public void Sanitizations_SQLI_Fails() {
        // Arrange
        Sanitization[] SQLISanitizations = new Sanitization[] {    Sanitization.addcslashes, //unsure
                Sanitization.addslashes, //unsure,
                Sanitization.mysql_escape_string,
                Sanitization.mysql_real_escape_string,
                Sanitization.mysqli_escape_string,
                Sanitization.escape_string,
                Sanitization.mysqli_real_escape_string,
                Sanitization.real_escape_string,
                Sanitization.sqlite_escape_string,
                Sanitization.escapeString,
                Sanitization.quote };

        for (Sanitization SQLISan : SQLISanitizations) {
            TaintMap SQLITaintedMap = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.XSS));
            SQLITaintedMap.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + SQLISan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(SQLITaintedMap);
            // Assert
            Assert.assertTrue(SQLITaintedMap.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check the sanitisation functions fails correctly for Traversal. ")
    public void Sanitizations_Traversal_Fails() {
        // Arrange
        Sanitization[] directorySanitizations = new Sanitization[] {Sanitization.realpath};

        for (Sanitization TraversalSan : directorySanitizations) {
            TaintMap TraversalSanitization = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.XSS));
            TraversalSanitization.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + TraversalSan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(TraversalSanitization);
            // Assert
            Assert.assertTrue(TraversalSanitization.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check the sanitisation functions fails for Injection. ")
    public void Sanitizations_Injection_Fails() {
        // Arrange
        Sanitization[] InjectionSanitizations = new Sanitization[] {Sanitization.escapeshellcmd};

        for (Sanitization InjectionSan : InjectionSanitizations) {
            TaintMap InjectionTaintedMap = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.XSS));
            InjectionTaintedMap.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + InjectionSan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(InjectionTaintedMap);
            // Assert
            Assert.assertTrue(InjectionTaintedMap.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check the sanitisation functions fails for XSS. ")
    public void Sanitizations_XSS_Fails() {
        // Arrange
        Sanitization[] XSSSanitizations = new Sanitization[] {Sanitization.htmlspecialchars, Sanitization.htmlentities};

        for (Sanitization XSSsan : XSSSanitizations) {
            TaintMap XSSTaintedMap = new TaintMap();
            Variable tainted = new Variable("tainted", List.of(TaintType.SQLI));
            XSSTaintedMap.put(tainted);
            String[] Arguments = new String[]{"name: LITERAL('" + XSSsan.name() + "')", "args[0]: tainted", "result: result"};
            Expr_FuncCall FuncCallStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);
            // Act
            FuncCallStatement.computeTaintFromInput(XSSTaintedMap);
            // Assert
            Assert.assertTrue(XSSTaintedMap.isTainted("result"));
        }
    }

    @Test
    @DisplayName("Check that the Assignment statement result tracks where it gets its taint from. ")
    public void AssignmentStatement_NoPhi_TracksTaintedFrom() {
        // Arrange
        AssignmentStatement statement = new AssignmentStatement("Var2", "Var1");

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);
        Variable Var2 = inputTaint.get("Var2");

        // Assert

        Assert.assertTrue(Var2.getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Assignment statement result tracks where it gets its taint from. ")
    public void AssignmentStatement_NoPhi_TracksTaintedFrom_NoTaint() {
        // Arrange
        AssignmentStatement statement = new AssignmentStatement("Var2", "Var1");

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);
        Variable Var2 = inputTaint.get("Var2");

        // Assert

        Assert.assertFalse(Var2.getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Assignment statement result tracks where it gets its taint from. ")
    public void AssignmentStatement_Phi_TracksTaintedFrom() {
        // Arrange
        AssignmentStatement statement = new AssignmentStatement("Var2", "Phi(Var1, Var3, Var4)");

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);
        Variable Var3 = new Variable("Var3");
        Var3.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);
        inputTaint.put(Var3);

        // Act

        statement.computeTaintFromInput(inputTaint);
        Variable Var2 = inputTaint.get("Var2");

        // Assert

        Assert.assertTrue(Var2.getTaintedFrom().contains(Var1) && Var2.getTaintedFrom().contains(Var3));
    }

    @Test
    @DisplayName("Check that the Assignment statement result tracks where it gets its taint from. ")
    public void AssignmentStatement_Phi_TracksTaintedFrom_NoTaint() {
        // Arrange
        AssignmentStatement statement = new AssignmentStatement("Var2", "Phi(Var1, Var3, Var4)");

        Variable Var1 = new Variable("Var1");
        Variable Var3 = new Variable("Var3");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);
        inputTaint.put(Var3);

        // Act

        statement.computeTaintFromInput(inputTaint);
        Variable Var2 = inputTaint.get("Var2");

        // Assert

        Assert.assertTrue(!Var2.getTaintedFrom().contains(Var1) && !Var2.getTaintedFrom().contains(Var3));
    }

    @Test
    @DisplayName("Check that the Terminal statement result tracks which variable it gets its taint from. ")
    public void TerminalStatement_TracksTaintedBy() {
        // Arrange
        TerminalStatement statement = new TerminalStatement("Terminal_Echo", new String[] {"expr: Var1"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(statement.TaintedBy().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Terminal statement result does not track where it gets its taint from if the expression is untainted. ")
    public void TerminalStatement_DoesNotTrackTaintedBy() {
        // Arrange
        TerminalStatement statement = new TerminalStatement("Terminal_Echo", new String[] {"expr: Var1"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertFalse(statement.TaintedBy().contains(Var1));
    }

    @Test
    @DisplayName("Check that the ExprArrayDimFetch statement result tracks where variables get tainted from. ")
    public void ExprArrayDimFetch_Tracks_TaintedFrom() {
        // Arrange
        Expr_ArrayDimFetch statement = new Expr_ArrayDimFetch("Expr_ArrayDimFetch", new String[] {"var: var#1<$_GET>", "dim: 2", "result: Var2"});

        Variable array = new Variable("var#1<$_GET>");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(array);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var2").getTaintedFrom().contains(array));
    }

    @Test
    @DisplayName("Check that the ExprArrayDimFetch statement result does not track where it gets its taint from if the expression is untainted. ")
    public void ExprArrayDimFetch_DoesNotTrack_TaintedFrom_NoTaint() {
        // Arrange
        Expr_ArrayDimFetch statement = new Expr_ArrayDimFetch("Expr_ArrayDimFetch", new String[] {"var: Var1", "dim: 2", "result: Var2"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertFalse(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the ExprAssign statement result tracks where variables are tainted from. ")
    public void ExprAssign_TracksTaintedFrom() {
        // Arrange
        Expr_Assign statement = new Expr_Assign("Expr_Assign", new String[] {"var: Var2", "expr: Var1", "result: Var3"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var2").getTaintedFrom().contains(Var1)
                && inputTaint.get("Var3").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the ExprAssign statement result does not track where untainted variables are tainted from. ")
    public void ExprAssign_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_Assign statement = new Expr_Assign("Expr_Assign", new String[] {"var: Var2", "expr: Var1", "result: Var3"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(!inputTaint.get("Var2").getTaintedFrom().contains(Var1)
                && !inputTaint.get("Var3").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_BinaryOp statement result tracks where variables are tainted from. ")
    public void Expr_BinaryOp_TracksTaintedFrom() {
        // Arrange
        Expr_BinaryOp_Concat statement = new Expr_BinaryOp_Concat("Expr_BinaryOp_Concat", new String[] {"left: Var1", "right: Var2", "result: Var3"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);
        Variable Var2 = new Variable("Var2");
        Var2.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);
        inputTaint.put(Var2);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var3").getTaintedFrom().contains(Var1)
                && inputTaint.get("Var3").getTaintedFrom().contains(Var2));
    }

    @Test
    @DisplayName("Check that the Expr_BinaryOp statement result does not track where untainted variables are tainted from. ")
    public void Expr_BinaryOp_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_BinaryOp_Concat statement = new Expr_BinaryOp_Concat("Expr_BinaryOp_Concat", new String[] {"var: Var2", "expr: Var1", "result: Var3"});

        Variable Var1 = new Variable("Var1");
        Variable Var2 = new Variable("Var2");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);
        inputTaint.put(Var2);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(!inputTaint.get("Var3").getTaintedFrom().contains(Var1)
                && !inputTaint.get("Var3").getTaintedFrom().contains(Var2));
    }

    @Test
    @DisplayName("Check that the Expr_ConcatList statement result tracks where variables are tainted from. ")
    public void Expr_ConcatList_TracksTaintedFrom() {
        // Arrange
        Expr_ConcatList statement = new Expr_ConcatList("Expr_ConcatList", new String[] {"list[0]: Var1", "list[1]: Var2", "result: Var3"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);
        Variable Var2 = new Variable("Var2");
        Var2.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);
        inputTaint.put(Var2);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var3").getTaintedFrom().contains(Var1)
                && inputTaint.get("Var3").getTaintedFrom().contains(Var2));
    }

    @Test
    @DisplayName("Check that the Expr_ConcatList statement result does not track where untainted variables are tainted from. ")
    public void Expr_ConcatList_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_ConcatList statement = new Expr_ConcatList("Expr_ConcatList", new String[] {"list[0]: Var1", "list[1]: Var2", "result: Var3"});

        Variable Var1 = new Variable("Var1");
        Variable Var2 = new Variable("Var2");


        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);
        inputTaint.put(Var2);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(!inputTaint.get("Var3").getTaintedFrom().contains(Var1)
                && !inputTaint.get("Var3").getTaintedFrom().contains(Var2));
    }

    @Test
    @DisplayName("Check that the Expr_FuncCall statement result tracks where variables are tainted from. ")
    public void Expr_FuncCall_TracksTaintedFrom() {
        // Arrange
        Expr_FuncCall statement = new Expr_FuncCall("Expr_FuncCall", new String[] {"name: LITERAL('shell_exec')", "args[0]: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_FuncCall statement result does not track where untainted variables are tainted from. ")
    public void Expr_FuncCall_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_FuncCall statement = new Expr_FuncCall("Expr_FuncCall", new String[] {"name: LITERAL('shell_exec')", "args[0]: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertFalse(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_FuncCall statement result tracks the variable the sink was tainted By. ")
    public void Expr_FuncCall_TracksTaintedBy() {
        // Arrange
        Expr_FuncCall statement = new Expr_FuncCall("Expr_FuncCall", new String[] {"name: LITERAL('shell_exec')", "args[0]: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.INJECTION);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(statement.TaintedBy().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_MethodCall statement result tracks where variables are tainted from. ")
    public void Expr_MethodCall_TracksTaintedFrom() {
        // Arrange
        Expr_MethodCall statement = new Expr_MethodCall("Expr_MethodCall", new String[] {"var: object", "name: LITERAL('shell_exec')", "args[0]: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);
        // Assert

        Assert.assertTrue(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_MethodCall statement result does not track where untainted variables are tainted from. ")
    public void Expr_MethodCall_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_MethodCall statement = new Expr_MethodCall("Expr_MethodCall", new String[] {"var: object", "name: LITERAL('shell_exec')", "args[0]: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertFalse(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_MethodCall statement result tracks the variable the sink was tainted By. ")
    public void Expr_MethodCall_TracksTaintedBy() {
        // Arrange
        Expr_MethodCall statement = new Expr_MethodCall("Expr_MethodCall", new String[] {"var: object", "name: LITERAL('shell_exec')", "args[0]: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.INJECTION);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(statement.TaintedBy().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_Print statement result tracks where variables are tainted from. ")
    public void Expr_Print_TracksTaintedFrom() {
        // Arrange
        Expr_Print statement = new Expr_Print("Expr_Print", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_Include statement result tracks where variables are tainted from. ")
    public void Expr_Include_TracksTaintedFrom() {
        // Arrange
        Expr_Include statement = new Expr_Include("Expr_Include", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.DIRECTORY);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_Eval statement result tracks where variables are tainted from. ")
    public void Expr_Eval_TracksTaintedFrom() {
        // Arrange
        Expr_Eval statement = new Expr_Eval("Expr_Eval", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.INJECTION);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_Print statement result does not track where untainted variables are tainted from. ")
    public void Expr_Print_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_Print statement = new Expr_Print("Expr_Print", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertFalse(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }


    @Test
    @DisplayName("Check that the Expr_Print statement result tracks the variable the sink was tainted By. ")
    public void Expr_Print_TracksTaintedBy() {
        // Arrange
        Expr_Print statement = new Expr_Print("Expr_Print", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.XSS);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(statement.TaintedBy().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_Include statement result does not track where untainted variables are tainted from. ")
    public void Expr_Include_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_Include statement = new Expr_Include("Expr_Include", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertFalse(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }


    @Test
    @DisplayName("Check that the Expr_Include statement result tracks the variable the sink was tainted By. ")
    public void Expr_Include_TracksTaintedBy() {
        // Arrange
        Expr_Include statement = new Expr_Include("Expr_Include", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.DIRECTORY);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(statement.TaintedBy().contains(Var1));
    }

    @Test
    @DisplayName("Check that the Expr_Eval statement result does not track where untainted variables are tainted from. ")
    public void Expr_Eval_DoesNotTrack_TaintedFrom() {
        // Arrange
        Expr_Eval statement = new Expr_Eval("Expr_Eval", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertFalse(inputTaint.get("Var2").getTaintedFrom().contains(Var1));
    }


    @Test
    @DisplayName("Check that the Expr_Eval statement result tracks the variable the sink was tainted By. ")
    public void Expr_Eval_TracksTaintedBy() {
        // Arrange
        Expr_Eval statement = new Expr_Eval("Expr_Eval", new String[] {"expr: Var1", "result: Var2"});

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.INJECTION);

        TaintMap inputTaint = new TaintMap();
        inputTaint.put(Var1);

        // Act

        statement.computeTaintFromInput(inputTaint);

        // Assert

        Assert.assertTrue(statement.TaintedBy().contains(Var1));
    }

    @Test
    @DisplayName("Check that FuncCall Source functions taint correctly. ")
    public void Expr_FuncCall_SourceFunctionWorks() {
        // Arrange
        String[] Arguments = new String[] { "name: LITERAL('apache_request_headers')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();



        Expr_FuncCall FuncCall = new Expr_FuncCall("Expr_FuncCall", Arguments);

        // Act
        FuncCall.computeTaintFromInput(TaintMap);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that FuncCall non-Source functions taint correctly. ")
    public void Expr_FuncCall_NotSourceFunctionWorks() {
        // Arrange
        String[] Arguments = new String[] { "name: LITERAL('safe')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();



        Expr_FuncCall FuncCall = new Expr_FuncCall("Expr_FuncCall", Arguments);

        // Act
        FuncCall.computeTaintFromInput(TaintMap);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that MethodCall Source functions taint correctly. ")
    public void Expr_MethodCall_SourceFunctionWorks() {
        // Arrange
        String[] Arguments = new String[] { "var: testVariable", "name: LITERAL('apache_request_headers')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();



        Expr_MethodCall MethodCall = new Expr_MethodCall("Expr_MethodCall", Arguments);

        // Act
        MethodCall.computeTaintFromInput(TaintMap);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that MethodCall non-Source functions taint correctly. ")
    public void Expr_MethodCall_NotSourceFunctionWorks() {
        // Arrange
        String[] Arguments = new String[] { "var: testVariable","name: LITERAL('safe')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();



        Expr_MethodCall MethodCall = new Expr_MethodCall("Expr_MethodCall", Arguments);

        // Act
        MethodCall.computeTaintFromInput(TaintMap);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

}
