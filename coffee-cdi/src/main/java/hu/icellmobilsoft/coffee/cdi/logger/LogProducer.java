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
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.api.provider.DependentProvider;

import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;

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
        return getStaticAppLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    /**
     * <p>
     * createDefaultJBossLogger.
     * </p>
     *
     * @param injectionPoint
     *            the injection point
     * @return {@link org.jboss.logging.Logger} instance
     */
    @Produces
    @Deprecated(since = "1.1.0", forRemoval = true)
    public org.jboss.logging.Logger createDefaultJbossLogger(InjectionPoint injectionPoint) {
        return getStaticLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    /**
     * AppLogger bean letrehozasa. Hasznalhato a statikus metodusokban es ott, ahol nem lehet a CDI Inject-et hasznalni. <b>Hasznalat utan
     * ".destroy()" kell!!</b>
     *
     * @param clazz
     *            logger class rakotese
     * @return {@code DependentProvider<AppLogger>} instance bean
     */
    public static DependentProvider<AppLogger> getAppLogger(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class should not be empty!");
        }
        // ez ugyan az ami itt van injectalva "private AppLogger appLogger;"
        DependentProvider<AppLogger> dpAppLogger = BeanProvider.getDependent(AppLogger.class, new DefaultAppLoggerQualifier());
        java.util.logging.Logger log = java.util.logging.Logger.getLogger(clazz.getName());
        dpAppLogger.get().setLogger(log);
        return dpAppLogger;
    }

    /**
     * Bizonyos esetekben hasznaljuk, peldaul ott ahol nincs CDI es static-kent bekotjuk osztaly valtozoba
     *
     * @param clazz
     *            osztaly
     * @return slf4j logger
     * @deprecated use {@link #getStaticAppLogger(Class)} instead
     */
    @Deprecated(forRemoval = true, since = "1.1.0")
    public static org.jboss.logging.Logger getStaticLogger(Class<?> clazz) {
        return org.jboss.logging.Logger.getLogger(clazz);
    }

    /**
     * Bizonyos esetekben hasznaljuk, peldaul ott ahol nincs CDI es static-kent bekotjuk osztaly valtozoba
     *
     * @param className
     *            osztaly neve
     * @return slf4j logger
     * @deprecated use {@link #getStaticAppLogger(String)} instead
     */
    @Deprecated(forRemoval = true, since = "1.1.0")
    public static org.jboss.logging.Logger getStaticLogger(String className) {
        return org.jboss.logging.Logger.getLogger(className);
    }

    /**
     * Bizonyos esetekben hasznaljuk, peldaul ott ahol nincs CDI es static-kent bekotjuk osztaly valtozoba
     *
     * @param clazz
     *            osztaly
     * @return slf4j logger
     */
    public static hu.icellmobilsoft.coffee.se.logging.Logger getStaticAppLogger(Class<?> clazz) {
        return DefaultLogger.getLogger(clazz);
    }

    /**
     * Bizonyos esetekben hasznaljuk, peldaul ott ahol nincs CDI es static-kent bekotjuk osztaly valtozoba
     *
     * @param className
     *            osztaly neve
     * @return slf4j logger
     */
    public static hu.icellmobilsoft.coffee.se.logging.Logger getStaticAppLogger(String className) {
        return DefaultLogger.getLogger(className);
    }
}
