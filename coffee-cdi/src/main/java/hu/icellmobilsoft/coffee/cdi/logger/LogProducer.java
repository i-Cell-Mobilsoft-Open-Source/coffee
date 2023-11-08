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

import java.util.function.Consumer;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * <p>
 * LogProducer class.
 * </p>
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Named
@Dependent
public class LogProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public LogProducer() {
        super();
    }

    @Inject
    @DefaultAppLogger
    private AppLogger appLogger;

    /**
     * Create request logger app logger.
     *
     * @param injectionPoint
     *            the injection point
     * @return the app logger
     */
    @Produces
    @ThisLogger
    public AppLogger createRequestLogger(InjectionPoint injectionPoint) {
        appLogger.setLogger(java.util.logging.Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName()));
        return appLogger;
    }

    /**
     * <p>
     * createDefaultLogger.
     * </p>
     *
     * @param injectionPoint
     *            the injection point
     * @return {@link hu.icellmobilsoft.coffee.se.logging.Logger} instance
     */
    @Produces
    public hu.icellmobilsoft.coffee.se.logging.Logger createDefaultLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    /**
     * AppLogger bean kezelése. Hasznalhato a statikus metodusokban es ott, ahol nem lehet a CDI Inject-et hasznalni
     * 
     * @param function
     *            the function doing the logging
     * @param clazz
     *            class for logging
     */
    public static void logToAppLogger(Consumer<AppLogger> function, Class<?> clazz) {
        if (function == null || clazz == null) {
            throw new IllegalArgumentException("function or class is missing!");
        }
        Instance<AppLogger> logInstance = CDI.current().select(AppLogger.class, new DefaultAppLoggerQualifier());
        AppLogger logger = logInstance.get();
        try {
            logger.setLogger(java.util.logging.Logger.getLogger(clazz.getName()));
            function.accept(logger);
        } finally {
            logInstance.destroy(logger);
        }
    }
}
