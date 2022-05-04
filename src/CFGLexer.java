import Statement.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import Statement.*;

public class CFGLexer {
    // Parses the output of the PHP-CFG programme into Block Objects I can analyse.

    private Block[] Blocks = null;
    private String Data = "";

    public File getGraph() {
        return graph;
    }

    private final File graph;

    CFGLexer(String FileLocation) throws InvalidFileException{
        graph = new File(FileLocation);
        ReadFile();
        ParseFile(Data);

    }

    private void ReadFile() throws InvalidFileException{
        try {
            Scanner reader = new Scanner(graph);
            StringBuilder builder = new StringBuilder();

            while (reader.hasNextLine()) builder.append(reader.nextLine()).append('\n');
            Data = builder.toString();

            reader.close();
        }

        catch (FileNotFoundException e){
            throw new InvalidFileException("File: " + graph.getName() + " cannot be found. ");
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

    private void ParseFile(String Data) throws InvalidFileException{
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
            // Go through Statements and find Parents (Predecessors) of blocks
            for (Statement statement : block.getStatements()) {
                if (statement.getStatementType() == StatementType.PROPERTY && ((PropertyStatement)statement).getPropertyName().equals("Parent"))
                    ParentsList.add(getBlock(((PropertyStatement)statement).getPropertyValue()));
            }

            block.setPred(ParentsList.toArray(Block[]::new));

            for (Block Predecessor : block.getPred())
                Successors.get(Predecessor).add(block);
        }

        // add the successors found to each block
        Arrays.stream(Blocks).forEach(b -> b.setSucc(Successors.get(b).toArray(Block[]::new)));
    }

}
