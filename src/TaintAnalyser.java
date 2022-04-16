import Statement.Expression.Sinks;
import Statement.Statement;
import Statement.Variable;
import Statement.TaintType;

import javax.swing.plaf.nimbus.State;
import java.util.*;

public class TaintAnalyser {
    // mark successors as changed, so they recompute the tainted set
    // todo: change the valid .dat file to mention it has to be procedurally generated
    // todo: implement "unsure"
    // todo: compare postorder iteration to round robin
    // todo: remove SSA names if var name is there, add verbose mode to keep it

    public static void main(String[] args) throws InvalidFileException {
        boolean Verbose = false;
        String Delimiter = " --> ";
        String Header = "A" + Delimiter + "B: A Taints B";
        String FailMessage = "The Analyser could not find any injection vulnerabilities. ";

        boolean tainted = false;
        HashSet<TaintType> taintTypeList = new HashSet<>();
        HashSet<Statement> TaintedSinks = new HashSet<>();

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
                // Write output for that block
                // Block: X Is Tainted!
                // [var1] --> [var2] ... --> [Sink]
                for (Statement statement : block.getStatements())
                    if (statement.isTaintedSink())
                        TaintedSinks.add(statement);
            }

        if (!tainted)
            System.out.println(FailMessage);

        else {
            System.out.println(Header);

            for (Statement statement : TaintedSinks) {
                // Write summary of vulnerabilities for the program.
                HashSet<Variable> TaintedFrom = statement.TaintedBy();
                ArrayList<String> TaintChain = new ArrayList<>();

                Sinks sinkType = statement.getSinkType();
                taintTypeList.add(sinkType.getVulnerableTaint());


                // constructs each link in the chain of variables which caused the taint.
                TaintChain.add(String.format("[ %s ]", sinkType.name()));
                while (!TaintedFrom.isEmpty()) {
                    StringBuilder CurrentVariableLink = new StringBuilder("[");
                    for (Variable variable : TaintedFrom) {
                        if (Verbose)
                            CurrentVariableLink.append(String.format(" %s", variable.getVariableName()));
                        else if (variable.isRealVariable()){
                            CurrentVariableLink.append(String.format(" %s", variable.getRealVariableName()));
                        }
                    }
                    CurrentVariableLink.append(" ]");

                    if (!CurrentVariableLink.toString().equals("[ ]"))
                        TaintChain.add(CurrentVariableLink.toString());

                    // finds the union of the variables that tainted these variables
                    HashSet<Variable> NewTaintedFrom = new HashSet<>();
                    for (Variable variable : TaintedFrom) {
                        NewTaintedFrom.addAll(variable.getTaintedFrom());
                    }
                    TaintedFrom = NewTaintedFrom;
                }

                Collections.reverse(TaintChain);

                // print the Taint Chain
                System.out.println("        " + "TAINT CHAIN:");
                System.out.println("                " + String.format("%s : ", sinkType.getVulnerableTaint()) + String.join(Delimiter, TaintChain));
                System.out.println();
            }
            System.out.println();
            int spacing = -Integer.max(25, TaintType.getMaxMessageLength());
            System.out.println("VULNERABILITY TABLE:");
            System.out.printf(String.join("", Collections.nCopies(5, "%"+spacing+"s")) + "%n",
                    "Vulnerability", "Confidentiality", "Integrity", "Availability", "Priority");
            System.out.println();

            for (TaintType taintType : taintTypeList) {
                System.out.printf(String.join("", Collections.nCopies(5, "%" + spacing + "s")) + "%n",
                        taintType.getMessage(), taintType.getConfidentiality(), taintType.getIntegrity(), taintType.getAvailability(), taintType.getPriority());
                System.out.println();
            }
        }
    }
}
