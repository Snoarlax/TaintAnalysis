package Statement;

public enum TaintType {
    // todo: replace with constants
    Default("For Debugging purposes. ", "", "", ""),
    SQLI("SQL Injection", "HIGH", "HIGH", "HIGH"), // can do " or '
    XSS("Cross site scripting", "low", "low", "low"), // can do <, >
    INJECTION("Code injection", "HIGH", "HIGH", "HIGH"), // can do ;, ||, &&
    DIRECTORY("Directory Traversal", "HIGH", "low", "low"); // can do .. , /


    private final String Message, Confidentiality, Integrity, Availability;

    TaintType(String message, String confidentiality, String integrity, String availability) {
        Message = message;
        Confidentiality = confidentiality;
        Integrity = integrity;
        Availability = availability;
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
}
