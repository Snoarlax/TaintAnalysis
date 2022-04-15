package Statement;

import java.util.Arrays;

public enum TaintType {
    SQLI("SQL Injection", "HIGH", "HIGH", "HIGH", "CRITICAL"), // can do " or '
    XSS("Cross site scripting", "low", "low", "low", "medium"), // can do <, >
    INJECTION("Code injection", "HIGH", "HIGH", "HIGH", "CRITICAL"), // can do ;, ||, &&
    DIRECTORY("Directory Traversal", "HIGH", "low", "low", "medium"); // can do .. , /


    private final String Message, Confidentiality, Integrity, Availability, Priority;

    TaintType(String message, String confidentiality, String integrity, String availability, String priority) {
        Message = message;
        Confidentiality = confidentiality;
        Integrity = integrity;
        Availability = availability;
        Priority = priority;
    }

    public String getMessage() {
        return Message;
    }

    public String getConfidentiality() {
        return Confidentiality;
    }

    public String getIntegrity() {
        return Integrity;
    }

    public String getAvailability() {
        return Availability;
    }

    public String getPriority() {
        return Priority;
    }

    public static Integer getMaxMessageLength() {
        return Arrays.stream(TaintType.values()).mapToInt(x -> x.getMessage().length()).max().getAsInt();
    }
}
