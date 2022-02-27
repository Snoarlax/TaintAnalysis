package Statement.Expression;
// ToDo: mark each Sink with the related TaintType
public enum Sinks {
    // Echo is detected in the TerminalStatement class, as it is labelled differently in the .dat files. The same goes for print and Expr_Print

    // Injection
        shell_exec,
        exec,
        eval,
        system,
        proc_open,
        passthru,
        popen,
    // Traversal
        include,
        include_once,
        require,
        require_once,
        file_put_contents,
        fopen,
        opendir,
        file,

    // XSS
        // echo
        // print
        printf,
    // SQLI
        mysql_query,
        msqli_query,
        // MySQLi::query Cant write as enum ToDO
        sqlite_query,
        // SqliteDataBase::query cant write as enum ToDO
        sqlite_single_query,
        // SqliteDataBase::singleQuery cant write as enum ToDO
        oci_parse



}
