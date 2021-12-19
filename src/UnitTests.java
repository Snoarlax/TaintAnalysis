import Statement.Statement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import Statement.*;

import java.util.HashMap;
import java.util.HashSet;

public class UnitTests {
    @Test
    @DisplayName("Tests that CFGParser returns a block for each block in the .dat file")
    public void CFGParser_TestParsing() throws InvalidFileException {
        // Arrange
        CFGParser parser = new CFGParser("graph.dat");
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
            CFGParser parser = new CFGParser("InvalidFile.dat");
        });
        String actualMessage = exception.getMessage();

        // Assert
        assertEquals(actualMessage, expectedMessage);

    }

    @Test
    @DisplayName("Test getBlock from CFGParser")
    public void CFGParser_TestGetBlock() throws InvalidFileException {
        // Arrange
        CFGParser parser = new CFGParser("graph.dat");
        // Act
        Block Block2 = parser.getBlock("Block#2");
        // Assert
        assertEquals(Block2.getBlockName(), "Block#2");
    }

    @Test
    @DisplayName("Test getSucc from CFGParser")
    public void CFGParser_TestGetSucc() throws InvalidFileException {
        // Arrange
        CFGParser parser = new CFGParser("graph.dat");
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
        CFGParser parser = new CFGParser("graph.dat");
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
        CFGParser parser = new CFGParser("graph.dat");

        // Act
        Block Block4 = parser.getBlock("Block#3");
        Statement[] statements = Block4.getStatements();
        StatementType[] types = new StatementType[statements.length];
        for (int i = 0; i < statements.length; i++)
            types[i] = statements[i].getStatementType();

        // Assert
        assertTrue(types[0] == StatementType.PROPERTY && types[1] == StatementType.ASSIGNMENT && types[2] == StatementType.EXPRESSION && types[3] == StatementType.TERMINAL && types[4] == StatementType.STATEMENT);
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment without a Phi function and tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_noPhi_WithTaint(){
        // Arrange
        AssignmentStatement StatementWithTaint = new AssignmentStatement("Var1", "Var2");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());
        Var2.setTainted(TaintType.Default);

        TaintMap.put(Var2, Var2);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, new String[0]);

        // Assert
        assertTrue(TaintMap.get(Var1).isTainted() && TaintMap.get(Var1).isTainted());
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment without a Phi function and no tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_noPhi_NoTaint(){
        // Arrange
        AssignmentStatement StatementWithNoTaint = new AssignmentStatement("Var1", "Var2");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        TaintMap.put(Var2, Var2);


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, new String[0]);

        // Assert
        assertFalse(TaintMap.containsKey(Var1));
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment with a Phi function and tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_Phi_WithTaint(){
        // Arrange
        AssignmentStatement StatementWithTaint = new AssignmentStatement("Var1", "Phi(Var2,Var3,Var4,Var5)");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());
        Var2.setTainted(TaintType.Default);

        TaintMap.put(Var2, Var2);


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, new String[0]);

        // Assert
        assertTrue(TaintMap.get(Var1).isTainted() && TaintMap.get(Var1).isTainted());
    }

    @Test
    @DisplayName("Test computeTaintFromInput for an AssignmentStatement for an assignment with a Phi function and no tainted assignments")
    public void AssignmentStatement_computeTaintFromInput_Phi_NoTaint(){
        // Arrange
        AssignmentStatement StatementWithNoTaint = new AssignmentStatement("Var1", "Phi(Var2,Var3,Var4,Var5)");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        TaintMap.put(Var2, Var2);


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, new String[0]);

        // Assert
        assertFalse(TaintMap.containsKey(Var1));
    }

    @Test
    @DisplayName("Found a bug when including new Lines in Literals, this checks a file with the bug to check for correct parsing. ")
    public void Test_NewLineInLiteral_BugFix() throws InvalidFileException {
        // Upon Experimenting with the bug, it seems you cannot inject an injection that looks like a normally parsed program (See ExprTest.dat for my attempt,
        // Created by parsing [ echo $a."fake')\nFakeArgument : LITERAL('FakeValue"; ]

        // Arrange
        CFGParser parser = new CFGParser("ExprTest.dat");
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
    public void ExpressionStatement_ExprBinaryOpConcat_ComputeTaintFromInput_WithTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithTaint = new ExpressionStatement("Expr_BinaryOp_Concat");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        String[] Arguments = new String[]{"left: Var1","right: LITERAL('')", "result: Var2"};


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(TaintMap.containsKey(Var2) && TaintMap.get(Var2).isTainted());
    }

    @Test
    @DisplayName("Check that Expr_BinaryOp_Concat passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprBinaryOpConcat_ComputeTaintFromInput_NoTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new ExpressionStatement("Expr_BinaryOp_Concat");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        String[] Arguments = new String[]{"left: Var1","right: LITERAL('')", "result: Var2"};


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(TaintMap.containsKey(Var2));
    }

    @Test
    @DisplayName("Check that Expr_ConcatList passes on taint correctly when given tainted arguments")
    public void ExpressionStatement_ExprConcatList_ComputeTaintFromInput_WithTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithTaint = new ExpressionStatement("Expr_ConcatList");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var3 = new Variable("Var3", new HashSet<>());
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        String[] Arguments = new String[]{"list[0]: Var1","list[1]: LITERAL('')","list[0]: Var2", "result: Var3"};


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(TaintMap.containsKey(Var3) && TaintMap.get(Var3).isTainted());
    }

    @Test
    @DisplayName("Check that Expr_ConcatList passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprConcatList_ComputeTaintFromInput_NoTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new ExpressionStatement("Expr_ConcatList");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var3 = new Variable("Var3", new HashSet<>());
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        String[] Arguments = new String[]{"list[0]: Var1","list[1]: LITERAL('')","list[0]: Var2", "result: Var3"};


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(TaintMap.containsKey(Var3));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given tainted arguments")
    public void ExpressionStatement_ExprAssign_ComputeTaintFromInput_WithTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithTaint = new ExpressionStatement("Expr_Assign");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var3 = new Variable("Var3", new HashSet<>());
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        Var1.setTainted(TaintType.Default);
        TaintMap.put(Var1, Var1);

        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};


        // Act
        StatementWithTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertTrue(TaintMap.containsKey(Var2) && TaintMap.get(Var2).isTainted() &&
                TaintMap.containsKey(Var3) && TaintMap.get(Var3).isTainted());
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given untainted arguments")
    public void ExpressionStatement_ExprAssign_ComputeTaintFromInput_NoTaintedArguments() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new ExpressionStatement("Expr_ConcatList");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var3 = new Variable("Var3", new HashSet<>());
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};


        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(TaintMap.containsKey(Var2) && TaintMap.containsKey(Var3));
    }

    @Test
    @DisplayName("Check that Expr_Assign passes on taint correctly when given untainted arguments and already tainted variables")
    public void ExpressionStatement_ExprAssign_ComputeTaintFromInput_TaintedResults() {
        // Arrange
        ExpressionStatement StatementWithNoTaint = new ExpressionStatement("Expr_ConcatList");

        HashMap<Variable, Variable> TaintMap = new HashMap<>();
        Variable Var3 = new Variable("Var3", new HashSet<>());
        Variable Var2 = new Variable("Var2", new HashSet<>());
        Variable Var1 = new Variable("Var1", new HashSet<>());

        String[] Arguments = new String[]{"var: Var2", "expr: Var1", "result: Var3"};

        Var2.setTainted(TaintType.Default);
        TaintMap.put(Var2, Var2);

        // Act
        StatementWithNoTaint.computeTaintFromInput(TaintMap, Arguments);

        // Assert
        assertFalse(!TaintMap.containsKey(Var2) && TaintMap.containsKey(Var3) && TaintMap.containsKey(Var1));
    }

// TODO: Tests for terminal statements
}
