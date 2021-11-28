import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CFGParser {
    // Parses the output of the PHP-CFG programme into Block Objects I can analyse.

    private Block[] Blocks = null;
    private String Data = "";

    CFGParser(String FileLocation){
        ReadFile(FileLocation);
        ParseFile(Data);
    }

    private void ReadFile(String FileLocation){
        try {
            File graph = new File(FileLocation);
            Scanner reader = new Scanner(graph);
            StringBuilder builder = new StringBuilder();

            while (reader.hasNextLine()) builder.append(reader.nextLine()).append('\n');
            Data = builder.toString();

            reader.close();
        }

        catch (FileNotFoundException e){
            System.out.println("File: " + FileLocation + " cannot be found. ");
            e.printStackTrace();
        }
    }

    public Block[] getBlocks() {
        return Blocks;
    }

    public Block getBlock(String BlockName) {
        for (Block Block: Blocks) {
            if (Block.getBlockName().equals(BlockName))
                return Block;
        }
        return null;
    }

    private void ParseFile(String Data){
        String[] RawBlocks = Data.split("\n\n");
        Blocks = new Block[RawBlocks.length];

        for (int i = 0; i < RawBlocks.length; i++)
            Blocks[i] = new Block(RawBlocks[i].strip());

        // Finish parsing the blocks by allocating their predecessors and successors

        // Initialise data structure to keep track of successors
        HashMap<Block, HashSet<Block>> Successors = new HashMap<>();
        Arrays.stream(Blocks).forEach(b -> Successors.put(b, new HashSet<>()));

        for (Block block : Blocks) {
            // Add Predecessors to each Block object

            ArrayList<Block> ParentsList = new ArrayList<>();
            // Go through properties and find Parents (Predecessors) of blocks
            for (String property : block.getStatements()) {
                if (property.split(": ")[0].equals("Parent"))
                    ParentsList.add(getBlock(property.split(": ")[1]));
            }

            block.setPred(ParentsList.toArray(Block[]::new));

            for (Block Predecessor : block.getPred())
                Successors.get(Predecessor).add(block);
        }

        // add the successors found to each block
        Arrays.stream(Blocks).forEach(b -> b.setSucc(Successors.get(b).toArray(Block[]::new)));
    }

}
