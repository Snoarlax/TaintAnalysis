package Statement;

public enum TaintType {
    // todo: replace with constants
    Default("For Debugging purposes. ", "", "", ""),
    SQLI("SQL Injection", "HIGH", "HIGH", "HIGH"), // can do " or '
    XSS("Cross site scripting", "low", "low", "low"), // can do <, >
    INJECTION("Code injection", "HIGH", "HIGH", "HIGH"), // can do ;, ||, &&
    DIRECTORY("Directory Traversal", "HIGH", "HIGH", "low"); // can do .. , /


    private final String Message, Confidentiality, Integrity, Availability;

    TaintType(String message, String confidentiality, String integrity, String availability) {
        Message = message;
        Confidentiality = confidentiality;
        Integrity = integrity;
        Availability = availability;
    }


    public String generateMessage() {
        int spacing = Integer.max(15, Message.length());
        String header = String.format("%"+spacing+"s" + "%"+spacing+"s" + "%"+spacing+"s" + "%"+spacing+"s", "Vulnerability", "Confidentiality", "Integrity", "Availability");
        String content = String.format("%"+spacing+"s" + "%"+spacing+"s" + "%"+spacing+"s" + "%"+spacing+"s", Message,Confidentiality,Integrity,Availability);

        return String.format("%s\n%s",header,content);
    }

}
