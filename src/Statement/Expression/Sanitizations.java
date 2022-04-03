package Statement.Expression;

import Statement.TaintType;

import java.util.HashSet;
import java.util.List;

public enum Sanitizations {
    // Injection
    escapeshellcmd(new TaintType[] {TaintType.Default, TaintType.INJECTION}),

    // Traversal
    realpath(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}, true), //unsure, needs to be checked manually
    // XSS
    htmlspecialchars(new TaintType[] {TaintType.Default, TaintType.XSS}),
    htmlentities(new TaintType[] {TaintType.Default, TaintType.XSS}),

    // SQLI
    addcslashes(new TaintType[] {TaintType.Default, TaintType.SQLI}, true), //unsure, needs to be checked manually
    addslashes(new TaintType[] {TaintType.Default, TaintType.SQLI}, true), //unsure, needs to be checked manually
    mysql_escape_string(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    mysql_real_escape_string(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    mysqli_escape_string(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    escape_string(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    mysqli_real_escape_string(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    real_escape_string(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    sqlite_escape_string(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    escapeString(new TaintType[] {TaintType.Default, TaintType.SQLI}),
    quote(new TaintType[] {TaintType.Default, TaintType.SQLI});


    private final HashSet<TaintType> TaintTypeSanitizations;

    private final boolean NeedsManualVerification;


    Sanitizations(TaintType[] TaintTypesToRemove) {
        this.TaintTypeSanitizations = new HashSet<>(List.of(TaintTypesToRemove));
        this.NeedsManualVerification = false;
    }

    Sanitizations(TaintType[] TaintTypesToRemove, boolean ManualCheckingFlag) {
        this.TaintTypeSanitizations = new HashSet<>(List.of(TaintTypesToRemove));
        this.NeedsManualVerification = ManualCheckingFlag;
    }

    public HashSet<TaintType> getTaintTypeSanitizations() {
        return new HashSet<>(TaintTypeSanitizations);
    }

    public boolean NeedsManualVerification() {
        return NeedsManualVerification;
    }

}
