package com.github.ddth.recipes.apiservice.logging;

import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.recipes.apiservice.IApiLogger;

import java.io.PrintStream;
import java.util.Map;

/**
 * This {@link IApiLogger} writes API performance logs to a {@link PrintStream} in JSON format.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v1.1.0
 */
public class PrintStreamPerfApiLogger extends AbstractPerfApiLogger {
    /**
     * This logger writes logs to stdout.
     */
    public static PrintStreamPerfApiLogger STDOUT_LOGGER = new PrintStreamPerfApiLogger().setPrintStream(System.out);

    /**
     * This logger writes logs to stderr.
     */
    public static PrintStreamPerfApiLogger STDERR_LOGGER = new PrintStreamPerfApiLogger().setPrintStream(System.err);

    private PrintStream printStream = System.out;

    public PrintStream getPrintStream() {
        return printStream;
    }

    public PrintStreamPerfApiLogger setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
        return this;
    }

    /**
     * {@@inheritDoc}
     */
    @Override
    protected void writeLog(Map<String, Object> data) {
        if (printStream != null) {
            printStream.println(SerializationUtils.toJsonString(data));
        }
    }
}
