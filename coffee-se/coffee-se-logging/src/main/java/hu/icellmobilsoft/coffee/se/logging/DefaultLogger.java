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
package hu.icellmobilsoft.coffee.se.logging;

import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * <p>
 * Logger default implementation without cdi
 * </p>
 *
 * @author mark.petrenyi
 * @since 1.1.0
 */
public class DefaultLogger implements hu.icellmobilsoft.coffee.se.logging.Logger {

    private static final long serialVersionUID = 1L;

    private Logger logger;

    public static hu.icellmobilsoft.coffee.se.logging.Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static hu.icellmobilsoft.coffee.se.logging.Logger getLogger(String name) {
        DefaultLogger appLogger = new DefaultLogger();
        appLogger.setLogger(Logger.getLogger(name));
        return appLogger;
    }

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
            Logger log = Logger.getLogger(getClass().getName());
            log.log(JulLevel.WARN, "Logger not SET! Applogger create temporary logger!");
            return log;
        } else {
            return logger;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String msg) {
        logger().log(JulLevel.TRACE, msg);
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String format, Object... arguments) {
        logger().log(JulLevel.TRACE, () -> MessageFormat.format(format, arguments));
    }

    /** {@inheritDoc} */
    @Override
    public void trace(String msg, Throwable t) {
        logger().log(JulLevel.TRACE, msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTraceEnabled() {
        return logger().isLoggable(JulLevel.TRACE);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String msg) {
        logger().log(JulLevel.DEBUG, msg);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String format, Object... arguments) {
        logger().log(JulLevel.DEBUG, () -> MessageFormat.format(format, arguments));
    }

    /** {@inheritDoc} */
    @Override
    public void debug(String msg, Throwable t) {
        logger().log(JulLevel.DEBUG, msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDebugEnabled() {
        return logger().isLoggable(JulLevel.DEBUG);
    }

    /** {@inheritDoc} */
    @Override
    public void info(String msg) {
        logger().log(JulLevel.INFO, msg);
    }

    /** {@inheritDoc} */
    @Override
    public void info(String format, Object... arguments) {
        logger().log(JulLevel.INFO, () -> MessageFormat.format(format, arguments));
    }

    /** {@inheritDoc} */
    @Override
    public void info(String msg, Throwable t) {
        logger().log(JulLevel.INFO, msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isInfoEnabled() {
        return logger().isLoggable(JulLevel.INFO);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String msg) {
        logger().log(JulLevel.WARN, msg);
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String format, Object... arguments) {
        logger().log(JulLevel.WARN, () -> MessageFormat.format(format, arguments));
    }

    /** {@inheritDoc} */
    @Override
    public void warn(String msg, Throwable t) {
        logger().log(JulLevel.WARN, msg, t);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String msg) {
        logger().log(JulLevel.ERROR, msg);
    }

    /** {@inheritDoc} */
    @Override
    public void error(String format, Object... arguments) {
        logger().log(JulLevel.ERROR, () -> MessageFormat.format(format, arguments));
    }

    /** {@inheritDoc} */
    @Override
    public void error(String msg, Throwable t) {
        logger().log(JulLevel.ERROR, msg, t);
    }
}
