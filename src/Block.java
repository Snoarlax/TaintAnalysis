import java.util.*;

public class Block {

    private final String BlockName;
    private Block[] Pred;
    private Block[] Succ;
    private boolean ret = false;
    private final String[] Statements; // TODO: change to Statements type, with statement that gets new tainted variables given input tainted
    private final HashMap<String, String[]> Arguments; // Maps from Property to Argument

    private final HashSet<Variable> Tainted;
    // Used when resolving dataflow equations to test if equations have reached a stable point (TaintedChanged == False)
    private boolean TaintedChanged = true;

    // Block has Statements, which each have Arguments
    public Block(String rawBlock) {
        Arguments = new HashMap<>();
        Tainted = new HashSet<>();

        BlockName  = rawBlock.split("\n", 2)[0];

        // Statements un-separated from their Arguments
        String[] StatementsCombined = rawBlock
                                        .split("\n( {4})(?=[^\s])", 2)[1]
                                            .split("\n( {4})(?=[^\s])"); // 4 character indent + non-whitespace character delimits Statements

        Statements = new String[StatementsCombined.length];

        for (int i = 0; i < Statements.length; i++) {
            Statements[i] = StatementsCombined[i].split("\n( {8})(?=[^\s])", 2)[0];

            if (StatementsCombined[i].split("\n( {8})(?=[^\s])", 2).length > 1) { // If there are arguments, then parse them
                // Array of Arguments
                String[] ArgumentsArray = StatementsCombined[i]
                        .split("\n( {8})(?=[^\s])", 2)[1]
                        .split("\n( {8})(?=[^\s])"); // 8 character indent + non-whitespace character delimits arguments

                for (int j = 0; j < ArgumentsArray.length; j++)
                    Arguments.put(Statements[i], ArgumentsArray);
            }
        }

        // Go through Statements, add the necessary ones
        for (String property : Statements) {
            // TODO: implement the parsing of important Statements

            if (property.equals("Terminal_Return"))
                ret = true;
        }
    }

    public void updateTaintedVariables(){
        // TODO: implement the dataflow equations s.t the "tainted variables" = "Out" hold for the "in"
        HashSet<Variable> in = new HashSet<>();

        for (Block block : Pred)
            in.addAll(block.getTainted());

        HashSet<Variable> newTainted = new HashSet<>();

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

    public boolean isEnd() {
        return ret;
    }

    public String[] getStatements() {
        return Statements;
    }

    public HashMap<String, String[]> getArguments() {
        return Arguments;
    }

    public HashSet<Variable> getTainted() {
        return Tainted;
    }

    public boolean hasTaintedChanged() {
        return TaintedChanged;
    }

    public void setTaintedChanged(boolean taintedChanged) {
        TaintedChanged = taintedChanged;
    }
}