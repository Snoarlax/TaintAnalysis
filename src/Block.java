import java.util.*;

public class Block {

    private final String BlockName;
    private Block[] Pred;
    private Block[] Succ;
    private boolean ret = false;
    private final String[] Properties;
    private final HashMap<String, String[]> Arguments; // Maps from Property to Argument

    private final HashSet<Variable> Tainted;
    // Used when resolving dataflow equations to test if equations have reached a stable point (TaintedChanged == False)
    private boolean TaintedChanged = true;

    // Block has Properties, which each have Arguments
    public Block(String rawBlock) {
        Arguments = new HashMap<>();
        Tainted = new HashSet<>();

        BlockName  = rawBlock.split("\n", 2)[0];

        // Properties un-separated from their Arguments
        String[] PropertiesCombined = rawBlock
                                        .split("\n( {4})(?=[^\s])", 2)[1]
                                            .split("\n( {4})(?=[^\s])"); // 4 character indent + non-whitespace character delimits properties

        Properties = new String[PropertiesCombined.length];

        for (int i = 0; i < Properties.length; i++) {
            Properties[i] = PropertiesCombined[i].split("\n( {8})(?=[^\s])", 2)[0];

            if (PropertiesCombined[i].split("\n( {8})(?=[^\s])", 2).length > 1) { // If there are arguments, then parse them
                // Array of Arguments
                String[] ArgumentsArray = PropertiesCombined[i]
                        .split("\n( {8})(?=[^\s])", 2)[1]
                        .split("\n( {8})(?=[^\s])"); // 8 character indent + non-whitespace character delimits arguments

                for (int j = 0; j < ArgumentsArray.length; j++)
                    Arguments.put(Properties[i], ArgumentsArray);
            }
        }

        // Go through properties, add the necessary ones
        for (String property : Properties) {
            // TODO: implement the parsing of important properties

            if (property.equals("Terminal_Return"))
                ret = true;
        }
    }

    public void updateTaintedVariables(){
        // TODO: implement the dataflow equations s.t the "tainted variables" = "Out" hold for the "in"
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

    public String[] getProperties() {
        return Properties;
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