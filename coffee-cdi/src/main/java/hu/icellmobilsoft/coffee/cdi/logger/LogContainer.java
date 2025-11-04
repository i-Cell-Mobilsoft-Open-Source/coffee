/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.coffee.cdi.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import jakarta.enterprise.inject.Model;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Container for logging.
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Model
public class LogContainer {

    /**
     * Log maximum size configuration key
     */
    private static final String LOG_MAX_SIZE_CONFIG_KEY = "coffee.logger.logContainer.maxSize";

    /**
     * Default log maximum size
     */
    private static final int DEFAULT_LOG_MAX_SIZE = 1000;

    /**
     * Log maximum size
     */
    private final int logMaxSize = ConfigProvider.getConfig().getOptionalValue(LOG_MAX_SIZE_CONFIG_KEY, Integer.class).orElse(DEFAULT_LOG_MAX_SIZE);

    private final Queue<Log> logs = new ArrayDeque<>();
    private final Map<String, Object> customParam = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public LogContainer() {
        super();
    }

    /**
     * Logs trace log with message.
     *
     * @param msg
     *            log message
     */
    public void trace(String msg) {
        addLog(new Log(LogLevel.TRACE, msg));
    }

    /**
     * Logs trace log with format and one or more arguments.
     *
     * @param format
     *            format of error message
     * @param arguments
     *            one or more arguments
     */
    public void trace(String format, Object... arguments) {
        String message = format(format, arguments);
        addLog(new Log(LogLevel.TRACE, message));
    }

    /**
     * Logs trace log with message and Throwable.
     *
     * @param msg
     *            message
     * @param t
     *            throwable error
     */
    public void trace(String msg, Throwable t) {
        String message = getFullStackTrace(msg, t);
        addLog(new Log(LogLevel.TRACE, message));
    }

    /**
     * Logs debug log with message.
     *
     * @param msg
     *            log message
     */
    public void debug(String msg) {
        addLog(new Log(LogLevel.DEBUG, msg));
    }

    /**
     * Logs debug log with format and one or more arguments.
     *
     * @param format
     *            format of error message
     * @param arguments
     *            one or more arguments
     */
    public void debug(String format, Object... arguments) {
        String message = format(format, arguments);
        addLog(new Log(LogLevel.DEBUG, message));
    }

    /**
     * Logs debug log with message and Throwable.
     *
     * @param msg
     *            message
     * @param t
     *            throwable error
     */
    public void debug(String msg, Throwable t) {
        String message = getFullStackTrace(msg, t);
        addLog(new Log(LogLevel.DEBUG, message));
    }

    /**
     * Logs info log with message.
     *
     * @param msg
     *            log message
     */
    public void info(String msg) {
        addLog(new Log(LogLevel.INFO, msg));
    }

    /**
     * Logs info log with format and one or more arguments.
     *
     * @param format
     *            format of error message
     * @param arguments
     *            one or more arguments
     */
    public void info(String format, Object... arguments) {
        String message = format(format, arguments);
        addLog(new Log(LogLevel.INFO, message));
    }

    /**
     * Logs info log with message and Throwable.
     *
     * @param msg
     *            message
     * @param t
     *            throwable error
     */
    public void info(String msg, Throwable t) {
        String message = getFullStackTrace(msg, t);
        addLog(new Log(LogLevel.INFO, message));
    }

    /**
     * Logs warn log with message.
     *
     * @param msg
     *            log message
     */
    public void warn(String msg) {
        addLog(new Log(LogLevel.WARN, msg));
    }

    /**
     * Logs warn log with format and one or more arguments.
     *
     * @param format
     *            format of error message
     * @param arguments
     *            one or more arguments
     */
    public void warn(String format, Object... arguments) {
        String message = format(format, arguments);
        addLog(new Log(LogLevel.WARN, message));
    }

    /**
     * Logs warn log with message and Throwable.
     *
     * @param msg
     *            message
     * @param t
     *            throwable error
     */
    public void warn(String msg, Throwable t) {
        String message = getFullStackTrace(msg, t);
        addLog(new Log(LogLevel.WARN, message));
    }

    /**
     * Logs error log with message.
     *
     * @param msg
     *            log message
     */
    public void error(String msg) {
        addLog(new Log(LogLevel.ERROR, msg));
    }

    /**
     * Logs error log with format and onr or more arguments.
     *
     * @param format
     *            format of error message
     * @param arguments
     *            one or more arguments
     */
    public void error(String format, Object... arguments) {
        String message = format(format, arguments);
        addLog(new Log(LogLevel.ERROR, message));
    }

    /**
     * Logs error log with message and Throwable.
     *
     * @param msg
     *            message
     * @param t
     *            throwable error
     */
    public void error(String msg, Throwable t) {
        String message = getFullStackTrace(msg, t);
        addLog(new Log(LogLevel.ERROR, message));
    }

    /**
     * Returns value for given key in custom params.
     *
     * @param key
     *            searched key
     * @return value for key
     */
    public Object getValue(String key) {
        return customParam.get(key);
    }

    /**
     * Sets value for given key in custom params.
     *
     * @param key
     *            the key from key-value pair
     * @param value
     *            the value from key-value pair
     */
    public void setValue(String key, Object value) {
        String msg = (customParam.containsKey(key) ? "Replaced" : "Added") + " key: [" + key + "], value: [" + value + "]";
        addLog(new Log(LogLevel.CUSTOM, msg));
        customParam.put(key, value);
    }

    /**
     * Removes given key from custom params.
     *
     * @param key
     *            the key to remove
     */
    public void removeValue(String key) {
        addLog(new Log(LogLevel.CUSTOM, "Remove key: [" + key + "]"));
        customParam.remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuffer msg = new StringBuffer();
        for (Log log : logs) {
            if (msg.length() > 0) {
                msg.append("\n");
            }
            msg.append(log.toString());
        }
        return msg.toString();
    }

    private String format(String format, Object... arguments) {
        return MessageFormat.format(format, arguments);
    }

    private class Log {
        private LocalDateTime logDateTime;
        private LogLevel level;
        private String message;

        private Log(LogLevel level, String message) {
            this.logDateTime = LocalDateTime.now();
            this.level = level;
            this.message = message;
        }

        @Override
        public String toString() {
            return MessageFormat.format("[{0}] {1}: {2}", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(logDateTime), level, message);
        }
    }

    /**
     * Returns highest {@link LogLevel} amongst {@code logs} list.
     *
     * @return highest log level in {@code logs}
     */
    public LogLevel getHighestLogLevel() {
        LogLevel highestLogLevel = LogLevel.CUSTOM;
        for (Log log : logs) {
            if (log.level.ordinal() > highestLogLevel.ordinal()) {
                highestLogLevel = log.level;
            }
        }
        return highestLogLevel;
    }

    private static String getFullStackTrace(String msg, Throwable t) {
        StringWriter sw = new StringWriter();
        sw.append(msg);
        sw.append(" > stacktrace: \n");
        sw.append("[");
        sw.append(getFullStackTrace(t));
        sw.append("]");
        return sw.toString();
    }

    /**
     * Returns full stack trace of given {@link Throwable}.
     *
     * @param t
     *            {@code Throwable} to convert into string
     * @return parameter converted into string
     */
    public static String getFullStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Log levels definition enum.
     */
    public enum LogLevel {
        /**
         * shows custom param changes
         */
        CUSTOM,
        /**
         * providing fine grade trace information
         */
        TRACE,
        /**
         * providing detailed information for debugging
         */
        DEBUG,
        /**
         * providing informational massages
         */
        INFO,
        /**
         * indicating potential problems of misconfiguration
         */
        WARN,
        /**
         * indicating some errors
         */
        ERROR,
    }

    private void addLog(LogContainer.Log log) {
        if (logs.size() >= logMaxSize) {
            logs.remove();
        }
        logs.add(log);
    }
}
