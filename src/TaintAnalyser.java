import java.util.Arrays;

public class TaintAnalyser {
    public static void main(String[] args) throws InvalidFileException {
        // todo: make this more efficient using a work set?
        CFGParser parser = new CFGParser(args[0]);
        // Stream used to check if any of the blocks have had their tainted changed updated, should be true on initialisation as entry point is marked as changed by default.
        boolean changes = Arrays.stream(parser.getBlocks()).anyMatch(Block::hasTaintedChanged);
        while (changes) {
            for (Block block : parser.getBlocks())
                if (block.hasTaintedChanged())
                    block.updateTaintedVariables();
            changes = Arrays.stream(parser.getBlocks()).anyMatch(Block::hasTaintedChanged);
        }

        for (Block block : parser.getBlocks())
            if (block.isTaintedSink())
                System.out.println("Block: " + block.getBlockName() + " Is Tainted! ");
    }
}
