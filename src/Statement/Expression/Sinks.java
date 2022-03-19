package Statement.Expression;

import Statement.TaintType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public enum Sinks {
    // todo: Check if the fact that some methods have common names becomes a problem; if it is implement flag to change output on error detection.
    // Echo is detected in the TerminalStatement class, as it is labelled differently in the .dat files. The same goes for print and Expr_Print

    // Injection
        shell_exec(new TaintType[] {TaintType.Default, TaintType.INJECTION}),
        exec(new TaintType[] {TaintType.Default, TaintType.INJECTION}),
        eval(new TaintType[] {TaintType.Default, TaintType.INJECTION}),
        system(new TaintType[] {TaintType.Default, TaintType.INJECTION}),
        proc_open(new TaintType[] {TaintType.Default, TaintType.INJECTION}),
        passthru(new TaintType[] {TaintType.Default, TaintType.INJECTION}),
        popen(new TaintType[] {TaintType.Default, TaintType.INJECTION}),
    // Traversal
        include(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),
        include_once(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),
        require(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),
        require_once(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),
        file_put_contents(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),
        fopen(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),
        opendir(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),
        file(new TaintType[] {TaintType.Default, TaintType.DIRECTORY}),

    // XSS
        echo(new TaintType[] {TaintType.Default, TaintType.XSS}),
        print(new TaintType[] {TaintType.Default, TaintType.XSS}),
        printf(new TaintType[] {TaintType.Default, TaintType.XSS}),
    // SQLI
        mysql_query(new TaintType[] {TaintType.Default, TaintType.SQLI}),
        mysqli_query(new TaintType[] {TaintType.Default, TaintType.SQLI}),
        sqlite_query(new TaintType[] {TaintType.Default, TaintType.SQLI}),
        sqlite_single_query(new TaintType[] {TaintType.Default, TaintType.SQLI}),
        single_query(new TaintType[] {TaintType.Default, TaintType.SQLI}),
        oci_parse(new TaintType[] {TaintType.Default, TaintType.SQLI}),
        query(new TaintType[] {TaintType.Default, TaintType.SQLI}),
        prepare(new TaintType[] {TaintType.Default, TaintType.SQLI});

    private final HashSet<TaintType> SinkType;

    private Sinks(TaintType[] TaintTypes) {
            this.SinkType = new HashSet<>(List.of(TaintTypes));
    }

    public HashSet<TaintType> getVulnerableTaints() {
        return new HashSet<>(SinkType);
    }
}
