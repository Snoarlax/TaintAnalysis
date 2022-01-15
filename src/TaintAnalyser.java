import Statement.Variable;

import java.util.*;

public class TaintAnalyser {
    // mark successors as changed, so they recompute the tainted set
    public static void main(String[] args) throws InvalidFileException {
        CFGParser parser = new CFGParser(args[0]);
        // Use a FiFo queue to manage which blocks need to be analysed.
        LinkedList<Block> workSet = new LinkedList<>();
        // todo: think about whether this is correct
        workSet.add(parser.getBlocks()[0]);
        while (!workSet.isEmpty()) {
            Block block = workSet.pop();
            HashSet<Variable> oldTainted = new HashSet<>(block.getTainted().keySet());
            block.updateTaintedVariables();
            // If the tainted values change, make sure to repeat this on the successors of the block!
            if (!oldTainted.equals(block.getTainted().keySet()))
                Collections.addAll(workSet, block.getSucc());
        }

        for (Block block : parser.getBlocks())
            if (block.isTaintedSink())
                System.out.println("Block: " + block.getBlockName() + " Is Tainted! ");
    }
}
