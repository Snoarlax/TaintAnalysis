import TaintAnalysisComponents.Sink;
import Statement.Statement;
import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;
import TaintAnalysisComponents.TaintType;

import java.util.*;

public class TaintAnalyser {

    public static void main(String[] args) {
        boolean Verbose = false;
        String Delimiter = " --> ";
        String Header = "A" + Delimiter + "B: A Taints B";
        String FailMessage = "The Analyser could not find any injection vulnerabilities. ";
        String UsageMessage = """
                TaintAnalyser [FILE] [OPTION]
                Uses Taint analyses to analyse FILE in CFG form for injection vulnerabilities.
                
                OPTIONS
                -v Print with Verbosity
                """;
        if (args.length != 1 && args.length != 2) {
            System.out.println(UsageMessage);
            return;
        }

        if (args.length == 2) {
            if (args[1].equals("-v"))
                Verbose = true;
            else {
                System.out.println(UsageMessage);
                return;
            }
        }

        boolean tainted = false;
        HashSet<TaintType> taintTypeList = new HashSet<>();
        HashSet<Statement> TaintedSinks = new HashSet<>();
        CFGLexer parser;
        try {
            parser = new CFGLexer(args[0]);
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        // Use a FiFo queue to manage which blocks need to be analysed.
        LinkedList<Block> workSet = new LinkedList<>();
        workSet.add(parser.getBlocks()[0]);

        while (!workSet.isEmpty()) {
            Block block = workSet.pop();
            TaintMap oldTainted = new TaintMap(block.getTainted());
            block.updateTaintedVariables();
            // If the tainted values change, make sure to repeat this on the successors of the block!
            if (!oldTainted.equals(block.getTainted()))
                Collections.addAll(workSet, block.getSucc());
        }

        // Find Tainted Sinks

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
            System.out.println("        " + "TAINT CHAIN[S]:");
            for (Statement statement : TaintedSinks) {
                // Write summary of vulnerabilities for the program.
                HashSet<Variable> TaintedFrom = statement.TaintedBy();
                ArrayList<String> TaintChain = new ArrayList<>();

                Sink sinkType = statement.getSinkType();
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

                // reverse the taint chain since it is more intuitive (From source to sink instead of vice versa)
                Collections.reverse(TaintChain);

                // print the Taint Chain
                System.out.println("                " + String.format("%s : ", sinkType.getVulnerableTaint()) + String.join(Delimiter, TaintChain));
                System.out.println();
            }
            System.out.println();
            int spacing = -Integer.max(25, TaintType.getMaxMessageLength());
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
