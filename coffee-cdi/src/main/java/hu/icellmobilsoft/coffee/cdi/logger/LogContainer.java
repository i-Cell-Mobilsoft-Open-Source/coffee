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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Model;


/**
 * Logglasra szolgalo container
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Model
public class LogContainer {

    private List<LogContainer.Log> logs = new ArrayList<LogContainer.Log>();
    private Map<String, Object> customParam = new HashMap<String, Object>();

    /**
     * <p>trace.</p>
     */
    public void trace(String msg) {
        logs.add(new Log(LogLevel.TRACE, msg));
    }

    /**
     * <p>trace.</p>
     */
    public void trace(String format, Object arg) {
        String message = format(format, arg);
        logs.add(new Log(LogLevel.TRACE, message));
    }

    /**
     * <p>trace.</p>
     */
    public void trace(String format, Object... arguments) {
        String message = format(format, arguments);
        logs.add(new Log(LogLevel.TRACE, message));
    }

    /**
     * <p>trace.</p>
     */
    public void trace(String msg, Throwable t) {
        String message = getFullStackTrace(t);
        logs.add(new Log(LogLevel.TRACE, message));
    }

    /**
     * <p>trace.</p>
     */
    public void trace(String format, Object arg1, Object arg2) {
        String message = format(format, arg1, arg2);
        logs.add(new Log(LogLevel.TRACE, message));
    }

    /**
     * <p>debug.</p>
     */
    public void debug(String msg) {
        logs.add(new Log(LogLevel.DEBUG, msg));
    }

    /**
     * <p>debug.</p>
     */
    public void debug(String format, Object arg) {
        String message = format(format, arg);
        logs.add(new Log(LogLevel.DEBUG, message));
    }

    /**
     * <p>debug.</p>
     */
    public void debug(String format, Object... arguments) {
        String message = format(format, arguments);
        logs.add(new Log(LogLevel.DEBUG, message));
    }

    /**
     * <p>debug.</p>
     */
    public void debug(String msg, Throwable t) {
        String message = getFullStackTrace(t);
        logs.add(new Log(LogLevel.DEBUG, message));
    }

    /**
     * <p>debug.</p>
     */
    public void debug(String format, Object arg1, Object arg2) {
        String message = format(format, arg1, arg2);
        logs.add(new Log(LogLevel.DEBUG, message));
    }

    /**
     * <p>info.</p>
     */
    public void info(String msg) {
        logs.add(new Log(LogLevel.INFO, msg));
    }

    /**
     * <p>info.</p>
     */
    public void info(String format, Object arg) {
        String message = format(format, arg);
        logs.add(new Log(LogLevel.INFO, message));
    }

    /**
     * <p>info.</p>
     */
    public void info(String format, Object... arguments) {
        String message = format(format, arguments);
        logs.add(new Log(LogLevel.INFO, message));
    }

    /**
     * <p>info.</p>
     */
    public void info(String msg, Throwable t) {
        String message = getFullStackTrace(t);
        logs.add(new Log(LogLevel.INFO, message));
    }

    /**
     * <p>info.</p>
     */
    public void info(String format, Object arg1, Object arg2) {
        String message = format(format, arg1, arg2);
        logs.add(new Log(LogLevel.INFO, message));
    }

    /**
     * <p>warn.</p>
     */
    public void warn(String msg) {
        logs.add(new Log(LogLevel.WARN, msg));
    }

    /**
     * <p>warn.</p>
     */
    public void warn(String format, Object arg) {
        String message = format(format, arg);
        logs.add(new Log(LogLevel.WARN, message));
    }

    /**
     * <p>warn.</p>
     */
    public void warn(String format, Object... arguments) {
        String message = format(format, arguments);
        logs.add(new Log(LogLevel.WARN, message));
    }

    /**
     * <p>warn.</p>
     */
    public void warn(String msg, Throwable t) {
        String message = getFullStackTrace(t);
        logs.add(new Log(LogLevel.WARN, message));
    }

    /**
     * <p>warn.</p>
     */
    public void warn(String format, Object arg1, Object arg2) {
        String message = format(format, arg1, arg2);
        logs.add(new Log(LogLevel.WARN, message));
    }

    /**
     * <p>error.</p>
     */
    public void error(String msg) {
        logs.add(new Log(LogLevel.ERROR, msg));
    }

    /**
     * <p>error.</p>
     */
    public void error(String format, Object arg) {
        String message = format(format, arg);
        logs.add(new Log(LogLevel.ERROR, message));
    }

    /**
     * <p>error.</p>
     */
    public void error(String format, Object... arguments) {
        String message = format(format, arguments);
        logs.add(new Log(LogLevel.ERROR, message));
    }

    /**
     * <p>error.</p>
     */
    public void error(String msg, Throwable t) {
        String message = getFullStackTrace(t);
        logs.add(new Log(LogLevel.ERROR, message));
    }

    /**
     * <p>error.</p>
     */
    public void error(String format, Object arg1, Object arg2) {
        String message = format(format, arg1, arg2);
        logs.add(new Log(LogLevel.ERROR, message));
    }

    /**
     * <p>getValue.</p>
     */
    public Object getValue(String key) {
        return customParam.get(key);
    }

    /**
     * <p>setValue.</p>
     */
    public void setValue(String key, Object value) {
        String msg = (customParam.containsKey(key) ? "Replaced" : "Added") + " key: [" + key + "], value: [" + value + "]";
        logs.add(new Log(LogLevel.CUSTOM, msg));
        customParam.put(key, value);
    }

    /**
     * <p>removeValue.</p>
     */
    public void removeValue(String key) {
        logs.add(new Log(LogLevel.CUSTOM, "Remove key: [" + key + "]"));
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
        private LogLevel level;
        private String message;

        private Log(LogLevel level, String message) {
            this.level = level;
            this.message = message;
        }

        @Override
        public String toString() {
            return level + ":" + message;
        }
    }

    /**
     * <p>getHighestLogLevel.</p>
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

    /**
     * <p>getFullStackTrace.</p>
     */
    public static String getFullStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public enum LogLevel {
        CUSTOM, TRACE, DEBUG, INFO, WARN, ERROR;
    }
}
