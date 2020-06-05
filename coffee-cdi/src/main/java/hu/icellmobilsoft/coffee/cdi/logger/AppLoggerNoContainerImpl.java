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

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import javax.interceptor.Interceptor;

import org.jboss.logging.Logger;

/**
 * <p>AppLoggerNoContainerImpl class.</p>
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Dependent
@Named
@Alternative
@DefaultAppLogger
@Priority(Interceptor.Priority.APPLICATION + 10)
public class AppLoggerNoContainerImpl implements AppLogger {

    private static final long serialVersionUID = 1L;

    private Logger logger;

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
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String format, Object... arguments) {
        logger().tracev(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String msg, Throwable t) {
        logger().trace(msg, t);
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
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String format, Object... arguments) {
        logger().debugv(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String msg, Throwable t) {
        logger().debug(msg, t);
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
    }

    /** {@inheritDoc} */
    @Override
    public void info(String format, Object... arguments) {
        logger().infov(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void info(String msg, Throwable t) {
        logger().info(msg, t);
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
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String format, Object... arguments) {
        logger().warnv(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String msg, Throwable t) {
        logger().warn(msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String msg) {
        logger().error(msg);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String format, Object... arguments) {
        logger().errorv(format, arguments);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String msg, Throwable t) {
        logger().error(msg, t);
    }

    /**
     * {@inheritDoc}
     *
     * request szinten eltarolt valtozo elkerese
     */
    public Object getValue(String key) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * request szinten eltarolt valtozo elmentese
     */
    public void setValue(String key, Object value) {
    }

    /**
     * {@inheritDoc}
     *
     * request szinten eltarolt valtozo torlese
     */
    public void removeValue(String key) {
    }

    /** {@inheritDoc} */
    @Override
    public void writeLogToInfo() {
        logger().info(toString());
    }

    /** {@inheritDoc} */
    @Override
    public void writeLogToError() {
        logger().error(toString());
    }

    /** {@inheritDoc} */
    @Override
    public void writeLog() {
        logger().info(toString());
    }

    /** {@inheritDoc} */
    @Override
    public boolean isThereAnyError() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isThereAnyWarning() {
        return false;
    }
}
