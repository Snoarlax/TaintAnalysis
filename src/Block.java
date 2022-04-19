import Statement.*;
import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;

import java.util.*;
import java.util.regex.Pattern;

public class Block {
    private final String BlockName;
    private Block[] Pred;
    private Block[] Succ;
    private final Statement[] Statements;
    private final HashMap<Statement, String[]> Arguments; // Maps from Property to Argument

    private final TaintMap Tainted;
    private boolean TaintedSink = false;

    // Block has Statements, which each have Arguments
    public Block(String rawBlock) throws InvalidFileException {
        Arguments = new HashMap<>();
        Tainted = new TaintMap();

        if (!Pattern.compile("\n( {4})(?=[^\s])").matcher(rawBlock).find()) // check for regex matches for the Statement Delimiter
            throw new InvalidFileException("The file is not a valid CFG .dat file. ");
        BlockName  = rawBlock.split("\n", 2)[0];

        // Statements un-separated from their Arguments
        String[] StatementsCombined = rawBlock
                                        .split("\n( {4})(?=[^\s])", 2)[1]
                                            .split("\n( {4})(?=[^\s])"); // 4 character indent + non-whitespace character delimits Statements

        // Construct the Statements by finding their arguments and then the type of statement.
        Statements = new Statement[StatementsCombined.length];
        for (int i = 0; i < Statements.length; i++) {
            String[] ArgumentsArray = new String[0];
            if (StatementsCombined[i].split("\n( {8})(?=[^\s])", 2).length > 1) { // If there are arguments, then parse them
                // Array of Arguments
                String[] ArgumentsArray_Unchecked = StatementsCombined[i]
                        .split("\n( {8})(?=[^\s])", 2)[1]
                        .split("\n( {8})(?=[^\s])"); // 8 character indent + non-whitespace character delimits arguments

                // Check if the bug (look in tests for more detail) caused the Arguments to be parsed incorrectly, and if so fix it
                ArrayList<String> ArgumentList = new ArrayList<>();
                for (int j = 0; j < ArgumentsArray_Unchecked.length; j++) {
                    // Fix bug by checking if bug has happened, by checking for LITERAL(' at the start and no ') at the end
                    // Then iteratively combining with the next argument until the bug is fixed.
                    String arg = ArgumentsArray_Unchecked[j].split(": ", 2)[0] + ": ";
                    StringBuilder value = new StringBuilder(ArgumentsArray_Unchecked[j].split(": ", 2)[1]);
                    if (value.toString().startsWith("LITERAL('"))
                        while (!(value.toString().endsWith("')") && !value.toString().endsWith("\\')")))
                            value.append(ArgumentsArray_Unchecked[++j]);
                    ArgumentList.add(arg + value);
                }

                ArgumentsArray = ArgumentList.toArray(String[]::new);
            }
                // Construct the statement using the type and arguments if necessary.
                String rawStatement = StatementsCombined[i].split("\n( {8})(?=[^\s])", 2)[0];

                StatementType statementType = StatementType.ParseStatementType(rawStatement);
                if (statementType == StatementType.PROPERTY)
                    Statements[i] = StatementType.ConstructPropertyStatement(rawStatement);
                else if (statementType == StatementType.ASSIGNMENT)
                    Statements[i] = StatementType.ConstructAssignmentStatement(rawStatement);
                else if (statementType == StatementType.Expr)
                    Statements[i] = StatementType.ConstructExpressionStatement(rawStatement, ArgumentsArray);
                else if (statementType == StatementType.Stmt)
                    Statements[i] = StatementType.ConstructStatementStatement(rawStatement);
                else if (statementType == StatementType.Terminal)
                    Statements[i] = StatementType.ConstructTerminalStatement(rawStatement);
                else
                    Statements[i] = StatementType.ConstructDefaultStatement(rawStatement);
                Arguments.put(Statements[i], ArgumentsArray);
        }

    }

    public void updateTaintedVariables() {
        for (Block block : Pred)
            Tainted.putAll(block.getTainted());

        // The taint function of the block should be equal to the application of all sequential taint functions of the statements that make up the block
        for (Statement statement : Statements) {
            statement.computeTaintFromInput(Tainted, Arguments.get(statement));
            // if the statement is a sink which gets tainted, mark the block as containing a tainted sink
            if (statement.isTaintedSink())
                TaintedSink = true;

        }
    }

    public String getBlockName() {
        return BlockName;
    }

    public void setPred(Block[] Predecessors) {
        Pred = Arrays.copyOf(Predecessors, Predecessors.length);
    }

    public Block[] getPred() {
        return Pred;
    }

    public void setSucc(Block[] Successors) {
        Succ = Arrays.copyOf(Successors, Successors.length);
    }

    public Block[] getSucc() {
        return Succ;
    }

    public Statement[] getStatements() {
        return Statements;
    }

    public HashMap<Statement, String[]> getArguments() {
        return Arguments;
    }

    public HashMap<Variable, Variable> getTainted() {
        // It is important the result is a copy of the tainted Values, so the taint application is a serializable flow.
        return new HashMap<>(Tainted);
    }

    public boolean isTaintedSink() {
        return TaintedSink;
    }

}