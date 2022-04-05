package Statement.Expression;

import Statement.TaintType;

import java.util.HashSet;
import java.util.List;

public enum Sanitizations {
    // Injection
    escapeshellcmd(new TaintType[] {TaintType.INJECTION}),

    // Traversal
    realpath(new TaintType[] {TaintType.DIRECTORY}), //unsure, needs to be checked manually
    // XSS
    htmlspecialchars(new TaintType[] {TaintType.XSS}),
    htmlentities(new TaintType[] {TaintType.XSS}),

    // SQLI
    addcslashes(new TaintType[] {TaintType.SQLI}), //unsure, needs to be checked manually
    addslashes(new TaintType[] {TaintType.SQLI}), //unsure, needs to be checked manually
    mysql_escape_string(new TaintType[] {TaintType.SQLI}),
    mysql_real_escape_string(new TaintType[] {TaintType.SQLI}),
    mysqli_escape_string(new TaintType[] {TaintType.SQLI}),
    escape_string(new TaintType[] {TaintType.SQLI}),
    mysqli_real_escape_string(new TaintType[] {TaintType.SQLI}),
    real_escape_string(new TaintType[] {TaintType.SQLI}),
    sqlite_escape_string(new TaintType[] {TaintType.SQLI}),
    escapeString(new TaintType[] {TaintType.SQLI}),
    quote(new TaintType[] {TaintType.SQLI});


    private final HashSet<TaintType> TaintTypeSanitizations;

    private final boolean NeedsManualVerification;


    Sanitizations(TaintType[] TaintTypesToRemove) {
        this.TaintTypeSanitizations = new HashSet<>(List.of(TaintTypesToRemove));
        this.NeedsManualVerification = false;
    }

    public HashSet<TaintType> getTaintTypeSanitizations() {
        return new HashSet<>(TaintTypeSanitizations);
    }

    public boolean NeedsManualVerification() {
        return NeedsManualVerification;
    }

}
