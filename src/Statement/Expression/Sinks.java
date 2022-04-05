package Statement.Expression;

import Statement.TaintType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public enum Sinks {
    // Echo is detected in the TerminalStatement class, as it is labelled differently in the .dat files. The same goes for print and Expr_Print

    // Injection
        shell_exec(TaintType.INJECTION),
        exec(TaintType.INJECTION),
        eval(TaintType.INJECTION),
        system(TaintType.INJECTION),
        proc_open(TaintType.INJECTION),
        passthru(TaintType.INJECTION),
        popen(TaintType.INJECTION),
    // Traversal
        include(TaintType.DIRECTORY),
        include_once(TaintType.DIRECTORY),
        require(TaintType.DIRECTORY),
        require_once(TaintType.DIRECTORY),
        file_put_contents(TaintType.DIRECTORY),
        fopen(TaintType.DIRECTORY),
        opendir(TaintType.DIRECTORY),
        file(TaintType.DIRECTORY),
        readfile(TaintType.DIRECTORY),

    // XSS
        echo(TaintType.XSS),
        print(TaintType.XSS),
        printf(TaintType.XSS),
    // SQLI
        mysql_query(TaintType.SQLI),
        mysqli_query(TaintType.SQLI),
        sqlite_query(TaintType.SQLI),
        sqlite_single_query(TaintType.SQLI),
        single_query(TaintType.SQLI),
        oci_parse(TaintType.SQLI),
        query(TaintType.SQLI),
        prepare(TaintType.SQLI);

    private final TaintType SinkType;

    Sinks(TaintType taintType) {
            this.SinkType = taintType;
    }

    public TaintType getVulnerableTaint() {
        return SinkType;
    }
}
