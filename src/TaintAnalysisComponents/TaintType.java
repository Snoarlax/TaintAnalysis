package TaintAnalysisComponents;

import java.util.Arrays;

public enum TaintType {
    SQLI("SQL Injection", "Attackers can write arbitrary queries to the DBMS  ", "CRITICAL"), // can do " or '
    XSS("Cross site scripting", "Attackers can cause clients to run arbitrary javascript ", "medium"), // can do <, >
    INJECTION("Code injection", "Attackers can run shell commands on the server ", "CRITICAL"), // can do ;, ||, &&
    DIRECTORY("Directory Traversal", "Attackers can read files stored on the server ", "high"); // can do .. , /


    private final String Message, Description, Priority;

    TaintType(String message, String description, String priority) {
        Message = message;
        Description = description;
        Priority = priority;
    }

    public String getMessage() {
        return Message;
    }

    public String getDescription() { return  Description; }

    public String getPriority() {
        return Priority;
    }

    public static Integer getMaxMessageLength() {
        return Arrays.stream(TaintType.values()).mapToInt(x -> x.getMessage().length()).max().getAsInt();
    }

    public static Integer getMaxDescriptionLength() {
        return Arrays.stream(TaintType.values()).mapToInt(x -> x.getDescription().length()).max().getAsInt();
    }
}
