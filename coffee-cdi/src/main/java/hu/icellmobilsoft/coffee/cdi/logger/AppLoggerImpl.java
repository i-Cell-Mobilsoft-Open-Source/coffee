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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.cdi.logger.LogContainer.LogLevel;

/**
 * <p>AppLoggerImpl class.</p>
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Named
@Dependent
@DefaultAppLogger
public class AppLoggerImpl implements AppLogger {

    private static final long serialVersionUID = 1L;

    private Logger logger;

    @Inject
    private LogContainer logContainer;

    /** {@inheritDoc} */
    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /** {@inheritDoc} */
    @Override
    public Logger getLogger() {
        return logger;
    }

    private Logger logger() {
        if (getLogger() == null) {
            // hogyha null lenne a logger (nem allitja be valaki amikor kell)
            // Logger log = CommonLoggerFactory.getLogger(getClass());
            Logger log = Logger.getLogger(getClass());
            log.warn("Logger not SET! Applogger create temporary logger!");
            return log;
        } else {
            return logger;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String msg) {
        logger().trace(msg);
        logContainer.trace(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String format, Object... arguments) {
        logger().tracev(format, arguments);
        logContainer.trace(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String msg, Throwable t) {
        logger().trace(msg, t);
        logContainer.trace(msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTraceEnabled() {
        return logger().isTraceEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String msg) {
        logger().debug(msg);
        logContainer.debug(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String format, Object... arguments) {
        logger().debugv(format, arguments);
        logContainer.debug(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String msg, Throwable t) {
        logger().debug(msg, t);
        logContainer.debug(msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDebugEnabled() {
        return logger().isDebugEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public void info(String msg) {
        logger().info(msg);
        logContainer.info(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void info(String format, Object... arguments) {
        logger().infov(format, arguments);
        logContainer.info(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void info(String msg, Throwable t) {
        logger().info(msg, t);
        logContainer.info(msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInfoEnabled() {
        return logger().isInfoEnabled();
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String msg) {
        logger().warn(msg);
        logContainer.warn(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String format, Object... arguments) {
        logger().warnv(format, arguments);
        logContainer.warn(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String msg, Throwable t) {
        logger().warn(msg, t);
        logContainer.warn(msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String msg) {
        logger().error(msg);
        logContainer.error(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String format, Object... arguments) {
        logger().errorv(format, arguments);
        logContainer.error(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String msg, Throwable t) {
        logger().error(msg, t);
        logContainer.error(msg, t);
    }

    /**
     * {@inheritDoc}
     *
     * request szinten eltarolt valtozo elkerese
     */
    public Object getValue(String key) {
        return logContainer.getValue(key);
    }

    /**
     * {@inheritDoc}
     *
     * request szinten eltarolt valtozo elmentese
     */
    public void setValue(String key, Object value) {
        logContainer.setValue(key, value);
    }

    /**
     * {@inheritDoc}
     *
     * request szinten eltarolt valtozo torlese
     */
    public void removeValue(String key) {
        logContainer.removeValue(key);
    }

    /**
     * <p>writeLogToInfo.</p>
     */
    public void writeLogToInfo() {
        logger().info(toString());
    }

    /**
     * <p>writeLogToError.</p>
     */
    public void writeLogToError() {
        logger().error(toString());
    }

    /** {@inheritDoc} */
    @Override
    public void writeLog() {
        LogLevel logLevel = logContainer.getHighestLogLevel();
        switch (logLevel) {
        case CUSTOM:
        case TRACE:
            logger().trace(toString());
            break;
        case DEBUG:
            logger().debug(toString());
            break;
        case INFO:
            logger().info(toString());
            break;
        case WARN:
            logger().warn(toString());
            break;
        case ERROR:
            logger().error(toString());
            break;
        default:
            logger().error(toString());
            break;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        String proxy = logContainer.getClass() + "@" + logContainer.hashCode() + "@@" + System.identityHashCode(logContainer);
        String clazz = logContainer.getClass().getSuperclass() + "@" + logContainer.getClass().getSuperclass().hashCode();
        return proxy + " -> " + clazz + ":\n" + logContainer.toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isThereAnyError() {
        return (logContainer.getHighestLogLevel() == LogLevel.ERROR);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isThereAnyWarning() {
        return (logContainer.getHighestLogLevel() == LogLevel.WARN || logContainer.getHighestLogLevel() == LogLevel.ERROR);
    }

}
