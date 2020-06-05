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
package hu.icellmobilsoft.coffee.rest.utils;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.WriterInterceptorContext;

import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifiers;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;

/**
 * Rest Logger util
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
public class RestLoggerUtil {

    /**
     * Returns max target entity log size for requestContext
     *
     * @param requestContext
     * @param target
     */
    public static int getMaxEntityLogSize(ContainerRequestContext requestContext, LogSpecifierTarget target) {
        if (requestContext == null) {
            return LogSpecifier.UNLIMIT;
        }
        LogSpecifiers logSpecifiers = RequestUtil.getAnnotation(requestContext, LogSpecifiers.class);
        if (logSpecifiers != null) {
            return getMaxEntityLogSize(target, logSpecifiers.value());
        } else {
            LogSpecifier logSpecifier = RequestUtil.getAnnotation(requestContext, LogSpecifier.class);
            return getMaxEntityLogSize(target, logSpecifier);
        }
    }

    /**
     * Returns max target entity log size for requestContext
     *
     * @param requestContext
     * @param target
     */
    public static int getMaxEntityLogSize(ClientRequestContext requestContext, LogSpecifierTarget target) {
        if (requestContext == null) {
            return LogSpecifier.UNLIMIT;
        }
        LogSpecifiers logSpecifiers = RequestUtil.getAnnotation(requestContext, LogSpecifiers.class);
        if (logSpecifiers != null) {
            return getMaxEntityLogSize(target, logSpecifiers.value());
        } else {
            LogSpecifier logSpecifier = RequestUtil.getAnnotation(requestContext, LogSpecifier.class);
            return getMaxEntityLogSize(target, logSpecifier);
        }
    }

    /**
     * Returns max target entity log size for writerInterceptorContext
     *
     * @param writerInterceptorContext
     * @param target
     */
    public static int getMaxEntityLogSize(WriterInterceptorContext writerInterceptorContext, LogSpecifierTarget target) {
        if (writerInterceptorContext == null) {
            return LogSpecifier.UNLIMIT;
        }
        Annotation[] annotations = writerInterceptorContext.getAnnotations();
        if (annotations != null) {
            for (Annotation a : annotations) {
                if (a instanceof LogSpecifiers) {
                    return getMaxEntityLogSize(target, ((LogSpecifiers) a).value());
                } else if (a instanceof LogSpecifier) {
                    return getMaxEntityLogSize(target, ((LogSpecifier) a));
                }
            }
        }
        return LogSpecifier.UNLIMIT;
    }

    /**
     * Returns the targets max entity log size
     *
     * @param target
     * @param logSpecifiers
     */
    public static int getMaxEntityLogSize(LogSpecifierTarget target, LogSpecifier... logSpecifiers) {
        if (logSpecifiers != null) {
            for (LogSpecifier logSpecifier : logSpecifiers) {
                if (logSpecifier != null && Arrays.asList(logSpecifier.target()).contains(target)) {
                    return logSpecifier.maxEntityLogSize();
                }
            }
        }
        return LogSpecifier.UNLIMIT;
    }

    /**
     * Checks if {@link LogSpecifier} annotation is present and {@link LogSpecifier#noLog()} ()} is NoLogTarget.ALL or target.
     *
     * @param requestContext
     * @param target
     */
    public static boolean logDisabled(ContainerRequestContext requestContext, LogSpecifierTarget target) {
        if (requestContext == null) {
            return false;
        }
        LogSpecifiers logSpecifiers = RequestUtil.getAnnotation(requestContext, LogSpecifiers.class);
        if (logSpecifiers != null) {
            return logDisabled(target, logSpecifiers.value());
        } else {
            LogSpecifier logSpecifier = RequestUtil.getAnnotation(requestContext, LogSpecifier.class);
            return logDisabled(target, logSpecifier);
        }
    }

    /**
     * Checks if {@link LogSpecifier} annotation is present and {@link LogSpecifier#noLog()} ()} is NoLogTarget.ALL or target.
     *
     * @param requestContext
     * @param target
     */
    public static boolean logDisabled(ClientRequestContext requestContext, LogSpecifierTarget target) {
        if (requestContext == null) {
            return false;
        }
        LogSpecifiers logSpecifiers = RequestUtil.getAnnotation(requestContext, LogSpecifiers.class);
        if (logSpecifiers != null) {
            return logDisabled(target, logSpecifiers.value());
        } else {
            LogSpecifier logSpecifier = RequestUtil.getAnnotation(requestContext, LogSpecifier.class);
            return logDisabled(target, logSpecifier);
        }
    }

    /**
     * Checks if {@link LogSpecifier} annotation is present and {@link LogSpecifier#noLog()} ()} is NoLogTarget.ALL or target.
     *
     * @param writerInterceptorContext
     * @param target
     */
    public static boolean logDisabled(WriterInterceptorContext writerInterceptorContext, LogSpecifierTarget target) {
        if (writerInterceptorContext == null) {
            return false;
        }
        Annotation[] annotations = writerInterceptorContext.getAnnotations();
        if (annotations == null) {
            return false;
        }
        for (Annotation a : annotations) {
            if (a instanceof LogSpecifiers) {
                return logDisabled(target, ((LogSpecifiers) a).value());
            } else if (a instanceof LogSpecifier) {
                return logDisabled(target, ((LogSpecifier) a));
            }
        }
        return false;
    }

    /**
     * Returns if log is disabled for the target
     *
     * @param target
     * @param logSpecifiers
     */
    public static boolean logDisabled(LogSpecifierTarget target, LogSpecifier... logSpecifiers) {
        if (logSpecifiers != null) {
            for (LogSpecifier logSpecifier : logSpecifiers) {
                if (logSpecifier != null && Arrays.asList(logSpecifier.target()).contains(target)) {
                    return logSpecifier.noLog();
                }
            }
        }
        return false;
    }

}
