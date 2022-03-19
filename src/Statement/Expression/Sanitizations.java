package Statement.Expression;

import Statement.TaintType;

import java.util.HashSet;
import java.util.List;

public enum Sanitizations {
    // todo: add flag which means unsure for some
    // Injection
    escapeshellcmd(new TaintType[] {TaintType.Default, TaintType.INJECTION}),

    // Traversal
    realpath(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}), //unsure
    // XSS
    htmlspecialchars(new TaintType[] {TaintType.Default, TaintType.XSS}),
    htmlentities(new TaintType[] {TaintType.Default, TaintType.XSS}),

    // SQLI
    addcslashes(new TaintType[] {TaintType.Default, TaintType.SQLI}), //unsure
    addslashes(new TaintType[] {TaintType.Default, TaintType.SQLI}), //unsure,
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


    Sanitizations(TaintType[] TaintTypesToRemove) {
        this.TaintTypeSanitizations = new HashSet<>(List.of(TaintTypesToRemove));
    }

    public HashSet<TaintType> getTaintTypeSanitizations() {
        return new HashSet<>(TaintTypeSanitizations);
    }
}
