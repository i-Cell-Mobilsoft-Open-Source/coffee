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

import org.apache.commons.lang3.StringUtils;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.WriterInterceptorContext;

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
     * Default constructor, constructs a new object.
     */
    public RestLoggerUtil() {
        super();
    }

    /**
     * Returns max target entity log size for requestContext.
     *
     * @param requestContext
     *            context
     * @param target
     *            target to check
     * @return max entity log size
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
     * Returns max target entity log size for requestContext.
     *
     * @param requestContext
     *            context
     * @param target
     *            target to check
     * @return max entity log size
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
     * Returns max target entity log size for writerInterceptorContext.
     *
     * @param writerInterceptorContext
     *            context
     * @param target
     *            target to check
     * @return max entity log size
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
     *            target to check
     * @param logSpecifiers
     *            {@link LogSpecifier}s to check
     * @return max entity log size
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
     * Checks if {@link LogSpecifier} annotation is present and {@link LogSpecifier#noLog()} is NoLogTarget.ALL or target.
     *
     * @param requestContext
     *            context
     * @param target
     *            target to check
     * @return if {@code LogSpecifier} annotation is present and log is disabled
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
     * Checks if {@link LogSpecifier} annotation is present and {@link LogSpecifier#noLog()} is NoLogTarget.ALL or target.
     *
     * @param requestContext
     *            context
     * @param target
     *            target to check
     * @return if {@code LogSpecifier} annotation is present and log is disabled
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
     * Checks if {@link LogSpecifier} annotation is present and {@link LogSpecifier#noLog()} is NoLogTarget.ALL or target.
     *
     * @param writerInterceptorContext
     *            context
     * @param target
     *            target to check
     * @return if {@code LogSpecifier} annotation is present and log is disabled
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
     *            target to check
     * @param logSpecifiers
     *            {@link LogSpecifier}s to check
     * @return if log is disabled
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

    /**
     * Returns true if the {@link MediaType} of the request context matches one of the given ones
     *
     * @param requestContext
     *            context
     * @param mediaTypes
     *            {@link MediaType}s to compare
     * @return true if there is a matching {@code MediaType}
     */
    public static boolean isLogSizeLimited(ContainerRequestContext requestContext, MediaType... mediaTypes) {
        if (requestContext == null || mediaTypes == null) {
            return false;
        }
        for (MediaType mediaType : mediaTypes) {
            if (RestLoggerUtil.isSameMediaTypeWithoutCharset(requestContext.getMediaType(), mediaType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if {@link MediaType}s are the same irrespective of charset
     *
     * @param mediaType1
     *            {@link MediaType} to compare
     * @param mediaType2
     *            {@link MediaType} to compare
     * @return true if {@code MediaType}s are the same
     */
    private static boolean isSameMediaTypeWithoutCharset(MediaType mediaType1, MediaType mediaType2) {
        if (mediaType1 == null || mediaType2 == null) {
            return false;
        }

        String type1 = mediaType1.getType();
        String subtype1 = mediaType1.getSubtype();
        String type2 = mediaType2.getType();
        String subtype2 = mediaType2.getSubtype();

        return (StringUtils.equals(type1, type2) && StringUtils.equals(subtype1, subtype2));
    }
}
