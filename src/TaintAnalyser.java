import TaintAnalysisComponents.Sink;
import Statement.Statement;
import TaintAnalysisComponents.TaintMap;
import TaintAnalysisComponents.Variable;
import TaintAnalysisComponents.TaintType;

import java.util.*;

public class TaintAnalyser {
    public static void main(String[] args) {
        // Verbose output prints everything, non-verbose output only prints variables with real names
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
            System.err.println(e.getMessage());
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
            System.err.println(FailMessage);

        else {
            System.out.println("File: " + parser.getGraph().getName() + " vulnerable. \n");
            System.out.println(Header);
            System.out.println("        " + "TAINT CHAIN[S]:");
            for (Statement statement : TaintedSinks) {
                // Write summary of vulnerabilities for the program.
                LinkedHashSet<Variable> TaintedFrom = new LinkedHashSet<>(statement.TaintedBy());
                ArrayList<String> TaintChain = new ArrayList<>();

                Sink sinkType = statement.getSinkType();
                taintTypeList.add(sinkType.getVulnerableTaint());


                // constructs each link in the chain of variables which caused the taint.

                TaintChain.add(String.format("[ %s ]", sinkType.name()));
                while (!TaintedFrom.isEmpty()) {
                    StringBuilder CurrentVariableLink = new StringBuilder("[");
                    // if verbose, add all variables in tainted from to the current link
                    if (Verbose)
                        TaintedFrom.forEach(x -> CurrentVariableLink.append(String.format(" %s", x.getVariableName())));

                    // otherwise only add the first real variable to the current link
                    else
                        TaintedFrom.stream().filter(Variable::isRealVariable).findFirst()
                            .ifPresent(variable -> CurrentVariableLink.append(String.format(" %s", variable.getRealVariableName())));


                    CurrentVariableLink.append(" ]");

                    if (!CurrentVariableLink.toString().equals("[ ]"))
                        TaintChain.add(CurrentVariableLink.toString());

                    // finds the union of the variables that tainted these variables
                    LinkedHashSet<Variable> NewTaintedFrom = new LinkedHashSet<>();
                    if (Verbose)
                        for (Variable variable : TaintedFrom)
                            NewTaintedFrom.addAll(variable.getTaintedFrom());

                    else {
                        Optional<Variable> RealVariable = TaintedFrom.stream().filter(Variable::isRealVariable).findFirst();
                        // Display the taint coming form the first real variable, or the first variable if none are real variables
                        if (RealVariable.isPresent())
                            NewTaintedFrom.addAll(RealVariable.get().getTaintedFrom());
                        else
                            NewTaintedFrom.addAll(TaintedFrom.iterator().next().getTaintedFrom());
                    }

                    TaintedFrom = NewTaintedFrom;
                }

                // reverse the taint chain since it is more intuitive (From source to sink instead of vice versa)
                Collections.reverse(TaintChain);

                // combine adjacent duplicates in non verbose mode; they are pointless otherwise
                if (!Verbose) {
                    ArrayList<String> DeduplicatedTaintChain = new ArrayList<>();
                    for (int i = 0; i < TaintChain.size()-1; i++) {
                        if (!TaintChain.get(i).equals(TaintChain.get(i+1)))
                            DeduplicatedTaintChain.add(TaintChain.get(i));
                    }
                    // add the last element ( the sink )
                    DeduplicatedTaintChain.add(TaintChain.get(TaintChain.size()-1));
                    TaintChain = new ArrayList<>(DeduplicatedTaintChain);
                }
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
