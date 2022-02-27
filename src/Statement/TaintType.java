package Statement;

public enum TaintType {
    Default,
    SQLI, // can do " or '
    XSS, // can do <, >
    INJECTION, // can do ;, ||, &&
    DIRECTORY // can do .. , /
}
