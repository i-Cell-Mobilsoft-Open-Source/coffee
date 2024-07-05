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

import java.text.MessageFormat;
import java.util.logging.Logger;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import hu.icellmobilsoft.coffee.cdi.logger.LogContainer.LogLevel;
import hu.icellmobilsoft.coffee.se.logging.JulLevel;

/**
 * <p>
 * AppLoggerImpl class.
 * </p>
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Named
@Dependent
@DefaultAppLogger
public class AppLoggerImpl implements AppLogger {

    private static final long serialVersionUID = 1L;

    /**
     * The wrapped logger instance
     */
    private Logger logger;

    /**
     * Log message container
     */
    @Inject
    private LogContainer logContainer;

    /**
     * Default constructor, constructs a new object.
     */
    public AppLoggerImpl() {
        super();
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private Logger logger() {
        if (getLogger() == null) {
            // if the logger were null (not set by anyone when needed)
            // Logger log = CommonLoggerFactory.getLogger(getClass());
            // Logger log = Logger.getLogger(getClass());
            Logger log = Logger.getLogger(getClass().getName());
            log.log(JulLevel.WARN, "Logger not SET! Applogger create temporary logger!");
            return log;
        } else {
            return logger;
        }
    }

    @Override
    public void trace(String msg) {
        logger().log(JulLevel.TRACE, msg);
        logContainer.trace(msg);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logger().log(JulLevel.TRACE, () -> MessageFormat.format(format, arguments));
        logContainer.trace(format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logger().log(JulLevel.TRACE, msg, t);
        logContainer.trace(msg, t);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger().isLoggable(JulLevel.TRACE);
    }

    @Override
    public void debug(String msg) {
        logger().log(JulLevel.DEBUG, msg);
        logContainer.debug(msg);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logger().log(JulLevel.DEBUG, () -> MessageFormat.format(format, arguments));
        logContainer.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logger().log(JulLevel.DEBUG, msg, t);
        logContainer.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger().isLoggable(JulLevel.DEBUG);
    }

    @Override
    public void info(String msg) {
        logger().log(JulLevel.INFO, msg);
        logContainer.info(msg);
    }

    @Override
    public void info(String format, Object... arguments) {
        logger().log(JulLevel.INFO, () -> MessageFormat.format(format, arguments));
        logContainer.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logger().log(JulLevel.INFO, msg, t);
        logContainer.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger().isLoggable(JulLevel.INFO);
    }

    @Override
    public void warn(String msg) {
        logger().log(JulLevel.WARN, msg);
        logContainer.warn(msg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logger().log(JulLevel.WARN, () -> MessageFormat.format(format, arguments));
        logContainer.warn(format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logger().log(JulLevel.WARN, msg, t);
        logContainer.warn(msg, t);
    }

    @Override
    public void error(String msg) {
        logger().log(JulLevel.ERROR, msg);
        logContainer.error(msg);
    }

    @Override
    public void error(String format, Object... arguments) {
        logger().log(JulLevel.ERROR, () -> MessageFormat.format(format, arguments));
        logContainer.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger().log(JulLevel.ERROR, msg, t);
        logContainer.error(msg, t);
    }

    /**
     * {@inheritDoc}
     *
     * Accessing a variable stored at the request level
     */
    public Object getValue(String key) {
        return logContainer.getValue(key);
    }

    /**
     * {@inheritDoc}
     *
     * Saving a variable stored at the request level
     */
    public void setValue(String key, Object value) {
        logContainer.setValue(key, value);
    }

    /**
     * {@inheritDoc}
     *
     * Deleting a variable stored at the request level
     */
    public void removeValue(String key) {
        logContainer.removeValue(key);
    }

    /**
     * <p>
     * writeLogToInfo.
     * </p>
     */
    public void writeLogToInfo() {
        logger().log(JulLevel.INFO, toString());
    }

    /**
     * <p>
     * writeLogToError.
     * </p>
     */
    public void writeLogToError() {
        logger().log(JulLevel.ERROR, toString());
    }

    @Override
    public void writeLog() {
        LogLevel logLevel = logContainer.getHighestLogLevel();
        switch (logLevel) {
        case CUSTOM:
        case TRACE:
            logger().log(JulLevel.TRACE, toString());
            break;
        case DEBUG:
            logger().log(JulLevel.DEBUG, toString());
            break;
        case INFO:
            logger().log(JulLevel.INFO, toString());
            break;
        case WARN:
            logger().log(JulLevel.WARN, toString());
            break;
        case ERROR:
            logger().log(JulLevel.ERROR, toString());
            break;
        default:
            logger().log(JulLevel.ERROR, toString());
            break;
        }
    }

    @Override
    public String toString() {
        String proxy = logContainer.getClass() + "@" + logContainer.hashCode() + "@@" + System.identityHashCode(logContainer);
        String clazz = logContainer.getClass().getSuperclass() + "@" + logContainer.getClass().getSuperclass().hashCode();
        return proxy + " -> " + clazz + ":\n" + logContainer.toString();
    }

    @Override
    public boolean isThereAnyError() {
        return (logContainer.getHighestLogLevel() == LogLevel.ERROR);
    }

    @Override
    public boolean isThereAnyWarning() {
        return (logContainer.getHighestLogLevel() == LogLevel.WARN || logContainer.getHighestLogLevel() == LogLevel.ERROR);
    }

}
