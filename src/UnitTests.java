import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class UnitTests {
    @Test
    @DisplayName("Tests that CFGParser returns a block for each block in the .dat file")
    public void CFGParser_TestParsing(){
        // Arrange
        CFGParser parser = new CFGParser("/home/snoarlax/Documents/ProjectPartII/Scripts/CFGParser/graph.dat");
        // Act
        int NoBlocks = parser.getBlocks().length;
        // Assert
        assertEquals(NoBlocks, 4);
    }

    @Test
    @DisplayName("Test getBlock from CFGParser")
    public void CFGParser_TestGetBlock(){
        // Arrange
        CFGParser parser = new CFGParser("/home/snoarlax/Documents/ProjectPartII/Scripts/CFGParser/graph.dat");
        // Act
        Block Block2 = parser.getBlock("Block#2");
        // Assert
        assertEquals(Block2.getBlockName(), "Block#2");
    }

    @Test
    @DisplayName("Test getSucc from CFGParser")
    public void CFGParser_TestGetSucc(){
        // Arrange
        CFGParser parser = new CFGParser("/home/snoarlax/Documents/ProjectPartII/Scripts/CFGParser/graph.dat");
        // Act
        Block Block1 = parser.getBlock("Block#1");
        Block[] Successors = Block1.getSucc();

        // Assert
        assertTrue(Successors[0].getBlockName().equals("Block#2") && Successors[1].getBlockName().equals("Block#3") || Successors[0].getBlockName().equals("Block#3") && Successors[1].getBlockName().equals("Block#2"));
    }

    @Test
    @DisplayName("Test getPred from CFGParser")
    public void CFGParser_TestGetPred(){
        // Arrange
        CFGParser parser = new CFGParser("/home/snoarlax/Documents/ProjectPartII/Scripts/CFGParser/graph.dat");
        // Act
        Block Block4 = parser.getBlock("Block#4");
        Block[] Predecessors = Block4.getPred();

        // Assert
        assertTrue(Predecessors[0].getBlockName().equals("Block#2") && Predecessors[1].getBlockName().equals("Block#3") || Predecessors[0].getBlockName().equals("Block#3") && Predecessors[1].getBlockName().equals("Block#2"));
    }
}
