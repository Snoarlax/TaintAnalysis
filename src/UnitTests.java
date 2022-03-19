import Statement.Expression.*;
import Statement.Statement;
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
        CFGParser parser = new CFGParser("tests/test1.dat");
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
            CFGParser parser = new CFGParser("tests/InvalidFile.dat");
        });
        String actualMessage = exception.getMessage();

        // Assert
        assertEquals(actualMessage, expectedMessage);

    }

    @Test
    @DisplayName("Test getBlock from CFGParser")
    public void CFGParser_TestGetBlock() throws InvalidFileException {
        // Arrange
        CFGParser parser = new CFGParser("tests/test1.dat");
        // Act
        Block Block2 = parser.getBlock("Block#2");
        // Assert
        assertEquals(Block2.getBlockName(), "Block#2");
    }

    @Test
    @DisplayName("Test getSucc from CFGParser")
    public void CFGParser_TestGetSucc() throws InvalidFileException {
        // Arrange
        CFGParser parser = new CFGParser("tests/test1.dat");
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
        CFGParser parser = new CFGParser("tests/test1.dat");
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
        CFGParser parser = new CFGParser("tests/test1.dat");

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
        CFGParser parser = new CFGParser("tests/test2.dat");

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
        Var2.setTainted(TaintType.Default);

        TaintMap.put(Var2, Var2);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, new String[0]);

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
        StatementWithNoTaint.computeTaintFromInput(TaintMap, new String[0]);

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

        Var2.setTainted(TaintType.Default);
        TaintMap.put(Var2, Var2);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, new String[0]);

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
        StatementWithNoTaint.computeTaintFromInput(TaintMap, new String[0]);

        // Assert
        assertFalse(TaintMap.isTainted("Var1"));
    }

    @Test
    @DisplayName("Found a bug when including new Lines in Literals, this checks a file with the bug to check for correct parsing. ")
    public void Test_NewLineInLiteral_BugFix() throws InvalidFileException {
        // Upon Experimenting with the bug, it seems you cannot inject an injection for this bug that results in a normally parsed program (See ExprTest.dat for my attempt,
        // Created by parsing [ echo $a."fake')\nFakeArgument : LITERAL('FakeValue"; ]

        // Arrange
        CFGParser parser = new CFGParser("tests/ExprTest.dat");
        // Act + Assert
        for (Block block : parser.getBlocks()){
            for (String[] arguments : block.getArguments().values()){
                for (String argument : arguments){
                    // Check each argument's value, if it starts with LITERAL(' it must end with ') (where the last ' is unescaped)
                    String value = argument.split(": ",2)[1];
                    if (value.startsWith("LITERAL('")){
                        if (!(value.endsWith("')") && !value.endsWith("\\')")))
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
        ExpressionStatement StatementWithTaint = new Expr_BinaryOp_Concat("Expr_BinaryOp_Concat");

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1");

        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        String[] Arguments = new String[]{"left: Var1","right: LITERAL('')", "result: Var2"};


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_BinaryOp_Concat passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprBinaryOpConcat_computeTaintFromInput_NoTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new Expr_BinaryOp_Concat("Expr_BinaryOp_Concat");
        TaintMap TaintMap = new TaintMap();
        Variable Var2 = new Variable("Var2");
        String[] Arguments = new String[]{"left: Var1","right: LITERAL('')", "result: Var2"};


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(TaintMap.isTainted(Var2));
    }

    @Test
    @DisplayName("Check that Expr_ConcatList passes on taint correctly when given tainted arguments")
    public void ExpressionStatement_ExprConcatList_computeTaintFromInput_WithTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithTaint = new Expr_ConcatList("Expr_ConcatList");

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1");

        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        String[] Arguments = new String[]{"list[0]: Var1","list[1]: LITERAL('')","list[0]: Var2", "result: Var3"};


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_ConcatList passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprConcatList_computeTaintFromInput_NoTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new Expr_ConcatList("Expr_ConcatList");
        TaintMap TaintMap = new TaintMap();
        String[] Arguments = new String[]{"list[0]: Var1","list[1]: LITERAL('')","list[0]: Var2", "result: Var3"};

        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given tainted arguments")
    public void ExpressionStatement_ExprAssign_computeTaintFromInput_WithTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithTaint = new Expr_Assign("Expr_Assign");

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1", new HashSet<>());

        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(TaintMap.isTainted("Var2") &&
                TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprAssign_computeTaintFromInput_NoTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new Expr_ConcatList("Expr_ConcatList");

        TaintMap TaintMap = new TaintMap();

        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(TaintMap.isTainted("Var2")
               || TaintMap.isTainted("Var3"));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given untainted arguments and already tainted variables")
    public void ExpressionStatement_ExprAssign_computeTaintFromInput_TaintedResults() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new Expr_Assign("Expr_Assign");

        TaintMap TaintMap = new TaintMap();
        Variable Var2 = new Variable("Var2");


        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};

        Var2.setTainted(TaintType.Default);
        TaintMap.put(Var2, Var2);

        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(TaintMap.isTainted("Var2") && !TaintMap.isTainted("Var3") && !TaintMap.isTainted("Var1"));
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as tainted if given a tainted argument. ")
    public void TerminalStatement_computeTaintFromInput_CorrectlyRegistersTaintedArguments() {
        // Arrange
        TerminalStatement StatementWithTaint = new TerminalStatement("Terminal_Echo");

        TaintMap TaintMap = new TaintMap();
        Variable Var1 = new Variable("Var1", new HashSet<>());

        String[] Arguments = new String[]{"expr: Var1"};

        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(StatementWithTaint.isTainted());
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as untainted if given an untainted argument. ")
    public void TerminalStatement_computeTaintFromInput_CorrectlyRegistersUntaintedArguments() {
        // Arrange
        TerminalStatement StatementWithTaint = new TerminalStatement("Terminal_Echo");

        TaintMap TaintMap = new TaintMap();
        String[] Arguments = new String[]{"expr: Var1"};

        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(StatementWithTaint.isTainted());
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as a sink if it is Terminal_Echo. ")
    public void TerminalStatement_TerminalEcho_MarkedAs_Sink() {
        // Arrange + Act
        TerminalStatement Statement = new TerminalStatement("Terminal_Echo");

        // Assert
        assertTrue(Statement.isSink());
    }

    @Test
    @DisplayName("Check that Terminal statements correctly mark themselves as a not a sink if it is not Terminal_Echo. ")
    public void TerminalStatement_NotTerminalEcho_NotMarkedAs_Sink() {
        // Arrange + Act
        TerminalStatement Statement = new TerminalStatement("Terminal_NotEcho");

        // Assert
        assertFalse(Statement.isSink());
    }

    @Test
    @DisplayName("Check that Expr_ArrayDimFetch correctly assigns sources. ")
    public void ExprArrayDimFetch_MarksOnSource() {
        // Arrange
        String[] Arguments = new String[] {"var: Var#1<$_GET>","dim: LITERAL('str')","result: Var#2"};
        TaintMap TaintMap = new TaintMap();
        Expr_ArrayDimFetch SourceStatement = new Expr_ArrayDimFetch("Expr_ArrayDimFetch");

        // Act
        SourceStatement.computeTaintFromInput(TaintMap,Arguments);
        // Assert
        assertTrue(TaintMap.isTainted("Var#2"));
    }

    @Test
    @DisplayName("Check that Expr_ArrayDimFetch correctly assigns non sources. ")
    public void ExprArrayDimFetch_NoMarkOnNonSource() {
        // Arrange
        String[] Arguments = new String[] {"var: Var#1<SAFE>","dim: LITERAL('str')","result: Var#2"};
        TaintMap TaintMap = new TaintMap();
        Expr_ArrayDimFetch SourceStatement = new Expr_ArrayDimFetch("Expr_ArrayDimFetch");

        // Act
        SourceStatement.computeTaintFromInput(TaintMap,Arguments);
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
        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        Expr_Print PrintStatement = new Expr_Print("Expr_Print");

        // Act
        PrintStatement.computeTaintFromInput(TaintMap, Arguments);

        // Assert

        assertTrue(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_Print computeTaintFromInput works correctly on a untainted argument. ")
    public void ExprPrint_computeTaintFromInput_noTaint() {
        // Arrange
        String[] Arguments = new String[] { "expr: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();
        Expr_Print PrintStatement = new Expr_Print("Expr_Print");

        // Act
        PrintStatement.computeTaintFromInput(TaintMap, Arguments);

        // Assert

        assertFalse(TaintMap.isTainted("Var2"));
    }

    @Test
    @DisplayName("Check that Expr_Print computeTaintFromInput works correctly on a tainted argument. ")
    public void FuncCall_computeTaintFromInput_withTaint() {
        // Arrange
        String[] Arguments = new String[] { "name: LITERAL('function')", "args[0]: Var1", "result: Var2"};
        TaintMap TaintMap = new TaintMap();

        Variable Var1 = new Variable("Var1");
        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        Expr_FuncCall PrintStatement = new Expr_FuncCall("Expr_FuncCall", Arguments);

        // Act
        PrintStatement.computeTaintFromInput(TaintMap, Arguments);

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
        FuncCallStatement.computeTaintFromInput(TaintMap, Arguments);

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
    @DisplayName("Check that getTaintTypes() works for different sinks. ")
    public void getTaintTypes_Sinks(){
        // Arrange
        Sinks[] SinksToTest = new Sinks[] {Sinks.shell_exec, Sinks.include, Sinks.echo, Sinks.mysql_query};
        List<HashSet<TaintType>> TaintTypesToCheck = List.of(
                new HashSet<TaintType>(Arrays.asList(TaintType.Default, TaintType.INJECTION)),
                new HashSet<TaintType>(Arrays.asList(TaintType.Default, TaintType.DIRECTORY)),
                new HashSet<TaintType>(Arrays.asList(TaintType.Default, TaintType.XSS)),
                new HashSet<TaintType>(Arrays.asList(TaintType.Default, TaintType.SQLI))
        );

        // ACT + ASSERT

        for (int i = 0; i < SinksToTest.length; i++)
            assertEquals(SinksToTest[i].getSinkType(), TaintTypesToCheck.get(i));
    }

}
