import Statement.Expression.Sinks;
import Statement.Statement;
import Statement.Variable;
import Statement.TaintType;

import java.util.*;

public class TaintAnalyser {
    // mark successors as changed, so they recompute the tainted set
    // todo: use constants for the output
    public static void main(String[] args) throws InvalidFileException {
        boolean tainted = false;

        System.out.println("--> : Tainted from");

        CFGParser parser = new CFGParser(args[0]);
        // Use a FiFo queue to manage which blocks need to be analysed.
        LinkedList<Block> workSet = new LinkedList<>();
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
            if (block.isTaintedSink()) {
                tainted = true;
                System.out.println("Block: " + block.getBlockName() + " Is Tainted! ");
                System.out.println();
                System.out.println("TAINT CHAIN:");
                for (Statement statement : block.getStatements())
                    if (statement.isTaintedSink()) {
                        HashSet<Variable> TaintedFrom = statement.TaintedBy();
                        ArrayList<String> TaintChain = new ArrayList<>();

                        Sinks sinkType = statement.getSinkType();
                        TaintChain.add(String.format("[ %s ]", sinkType.name()));
                        while (!TaintedFrom.isEmpty()) {
                            // constructs each link in the chain of variables which caused the taint.
                            StringBuilder CurrentVariableLink = new StringBuilder("[");
                            for (Variable variable : TaintedFrom)
                                CurrentVariableLink.append(String.format(" %s", variable.getVariableName()));
                            CurrentVariableLink.append(" ]");
                            TaintChain.add(CurrentVariableLink.toString());

                            // finds the union of the variables that tainted these variables
                            HashSet<Variable> NewTaintedFrom = new HashSet<>();
                            for (Variable variable : TaintedFrom) {
                                NewTaintedFrom.addAll(variable.getTaintedFrom());
                            }
                            TaintedFrom = NewTaintedFrom;
                        }

                        System.out.println("        " + String.join(" --> ",  TaintChain));
                        System.out.println();

                        TaintType taintType = sinkType.getVulnerableTaint();
                        int spacing = -Integer.max(25, taintType.getMessage().length());
                        System.out.println("VULNERABILITY TABLE:");
                        System.out.printf("        " + String.join("", Collections.nCopies(5, "%"+spacing+"s")) + "%n",
                                "Vulnerability", "Confidentiality", "Integrity", "Availability", "Priority");
                        System.out.println();
                        System.out.printf("        " + String.join("", Collections.nCopies(5, "%"+spacing+"s")) + "%n",
                                taintType.getMessage(),taintType.getConfidentiality(), taintType.getIntegrity(),taintType.getAvailability(), taintType.getPriority());
                    }
            }

        if (!tainted)
            System.out.println("The Analyser could not find any injection vulnerabilities. ");
    }
}
