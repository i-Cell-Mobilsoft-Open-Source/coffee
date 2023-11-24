/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.log.optimized;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;
import hu.icellmobilsoft.coffee.rest.utils.RestLoggerUtil;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import hu.icellmobilsoft.coffee.tool.utils.marshalling.MarshallingUtil;
import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;

/**
 * Request - Response logger class
 *
 * @author imre.scheffer
 * @author mate.biro
 * @since 2.4.0
 */
@Dependent
@Named("optimized_RequestResponseLogger")
public class RequestResponseLogger {

    /** Constant <code>NOTIFICATION_PREFIX="* "</code> */
    public static final String NOTIFICATION_PREFIX = "* ";
    /** Constant <code>REQUEST_PREFIX="&gt; "</code> */
    public static final String REQUEST_PREFIX = "> ";
    /** Constant <code>RESPONSE_PREFIX="&lt; "</code> */
    public static final String RESPONSE_PREFIX = "< ";

    /** Constant <code>ENTITY_MAX_LOG=5000</code> */
    public static final int ENTITY_MAX_LOG = 5000;

    /** Constant <code>SKIP_MEDIATYPE_SUBTYPE_PDF="pdf"</code> */
    public static final String SKIP_MEDIATYPE_SUBTYPE_PDF = "pdf";
    /** Constant <code>SKIP_MEDIATYPE_SUBTYPE_CSV="csv"</code> */
    public static final String SKIP_MEDIATYPE_SUBTYPE_CSV = "csv";
    /** Constant <code>SKIP_MEDIATYPE_SUBTYPE_SHEET="sheet"</code> */
    public static final String SKIP_MEDIATYPE_SUBTYPE_SHEET = "sheet";
    /** Constant <code>SKIP_PATH_POSTFIX_WADL=".wadl"</code> */
    public static final String SKIP_PATH_POSTFIX_WADL = ".wadl";
    /** Constant <code>SKIP_PATH_POSTFIX_XSD=".xsd"</code> */
    public static final String SKIP_PATH_POSTFIX_XSD = ".xsd";

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Default constructor, constructs a new object.
     */
    public RequestResponseLogger() {
        super();
    }

    /**
     * Prints request headers to {@link String}. Masks password.
     *
     * @param headerValues
     *            http header key and list of values
     * @return HTTP request header or null if invalid parameter
     */
    protected String printRequestHeaders(Map<String, List<String>> headerValues) {
        StringBuffer sb = new StringBuffer();
        sb.append(REQUEST_PREFIX).append("-- Header parameters:").append('\n');
        if (headerValues == null) {
            return sb.toString();
        }
        for (Map.Entry<String, List<String>> param : headerValues.entrySet()) {
            for (String value : param.getValue()) {
                String key = param.getKey();
                sb.append(REQUEST_PREFIX).append(key).append(": ").append(StringHelper.maskPropertyValue(key, value)).append('\n');
            }
        }
        sb.append(REQUEST_PREFIX).append('\n');
        return sb.toString();
    }

    /**
     * Prints http headers info from {@link ContainerRequestContext}.
     *
     * @param requestContext
     *            context
     * @return HTTP headers info or null if invalid parameter
     */
    public String printRequestHeaders(ContainerRequestContext requestContext) {
        if (requestContext == null) {
            return null;
        }
        return printRequestHeaders(requestContext.getHeaders());
    }

    /**
     * Prints http headers info from {@link HttpServletRequest}.
     *
     * @param servletRequest
     *            request
     * @return HTTP headers info or null if invalid parameter
     */
    public String printRequestHeaders(HttpServletRequest servletRequest) {
        if (servletRequest == null) {
            return null;
        }
        Map<String, List<String>> map = new HashMap<>();

        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            map.put(key, Collections.list(servletRequest.getHeaders(key)));
        }
        return printRequestHeaders(map);
    }

    /**
     * Prints http request url line.
     *
     * @param method
     *            POST, GET, PUT, ...
     * @param fullPath
     *            full url path
     * @param pathParameters
     *            path parameters
     * @param queryParameters
     *            query parameters
     * @return HTTP request URL line
     */
    protected String printRequestLine(String method, String fullPath, Map<String, List<String>> pathParameters,
            Map<String, List<String>> queryParameters) {
        StringBuffer sb = new StringBuffer();
        sb.append(NOTIFICATION_PREFIX).append("Server in-bound request").append('\n');
        sb.append(REQUEST_PREFIX).append(method).append(" ").append(fullPath).append('\n');
        sb.append(REQUEST_PREFIX).append("-- Path parameters:").append('\n');
        if (pathParameters != null) {
            for (Map.Entry<String, List<String>> param : pathParameters.entrySet()) {
                sb.append(REQUEST_PREFIX).append(param.getKey()).append(": ").append(param.getValue()).append('\n');
            }
        }
        sb.append(REQUEST_PREFIX).append("-- Query parameters:").append('\n');
        if (queryParameters != null) {
            for (Map.Entry<String, List<String>> param : queryParameters.entrySet()) {
                sb.append(REQUEST_PREFIX).append(param.getKey()).append(": ").append(param.getValue()).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Print http path info from {@link HttpServletRequest}.
     *
     * @param servletRequest
     *            context
     * @return HTTP path info or null if invalid parameter
     */
    public String printRequestLine(HttpServletRequest servletRequest) {
        if (servletRequest == null) {
            return null;
        }
        Map<String, List<String>> queryParameters = new HashMap<>();
        for (Map.Entry<String, String[]> param : servletRequest.getParameterMap().entrySet()) {
            queryParameters.put(param.getKey(), Arrays.asList(param.getValue()));
        }
        // HttpServletRequest nem tudja a path parametereket kiolvasni, ezert emptyMap-ot adunk be
        return printRequestLine(servletRequest.getMethod(), servletRequest.getRequestURL().toString(), Collections.emptyMap(), queryParameters);
    }

    /**
     * Prints http path info from {@link ContainerRequestContext}.
     *
     * @param requestContext
     *            context
     * @return HTTP path info or null if invalid parameter
     */
    public String printRequestLine(ContainerRequestContext requestContext) {
        if (requestContext == null) {
            return null;
        }
        UriInfo uriInfo = requestContext.getUriInfo();
        return printRequestLine(
                requestContext.getMethod(),
                uriInfo.getAbsolutePath().toASCIIString(),
                uriInfo.getPathParameters(),
                uriInfo.getQueryParameters());
    }

    /**
     * Prints request entity to {@link String}. Masks password.
     *
     * @param entity
     *            entity
     * @param maxLogSize
     *            max size for log
     * @throws IOException
     *             if cannot be read
     * @return request entity
     * @see #printEntity(byte[], Integer, String)
     */
    public String printRequestEntity(byte[] entity, Integer maxLogSize) throws IOException {
        return printEntity(entity, maxLogSize, REQUEST_PREFIX);
    }

    /**
     * Prints request entity to {@link String}. Masks password.
     *
     * @param entity
     *            entity
     * @param maxLogSize
     *            max size for log
     * @param prefix
     *            prefix for log
     * @return entity {@code String}
     * @throws IOException
     *             if cannot be read
     */
    public String printEntity(byte[] entity, Integer maxLogSize, String prefix) throws IOException {
        String requestText = entityToString(entity, maxLogSize);
        String maskedText = StringHelper.maskValueInXmlJson(requestText);
        return prefix + "entity: [" + maskedText + "]\n";
    }

    private String entityToString(byte[] entity, Integer maxLogSize) {
        if (entity == null) {
            return null;
        }
        if (entity.length == 0) {
            return "";
        }
        // input parameter szerint korlatozzuk a logot
        byte[] requestEntityPart = entity;
        if (maxLogSize != null && maxLogSize >= LogSpecifier.NO_LOG && entity.length > maxLogSize.intValue()) {
            requestEntityPart = Arrays.copyOf(entity, maxLogSize);
        }

        return new String(requestEntityPart, StandardCharsets.UTF_8);
    }

    /**
     * Returns the maximum request entity log size
     *
     * @param requestContext
     *            context
     *
     * @return the maximum log size of the entity
     */
    protected int getMaxRequestEntityLogSize(ContainerRequestContext requestContext) {
        // ha octet-stream vagy multipart és nincs LogSpecifier annotáció, akkor korlátozunk
        if (RestLoggerUtil.isLogSizeLimited(requestContext, MediaType.APPLICATION_OCTET_STREAM_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE)
                && !RestLoggerUtil.isLogSpecifierPresent(requestContext)) {
            return RequestResponseLogger.ENTITY_MAX_LOG;
        }

        // különben annotációban meghatározott maxEntityLogSize (ha nincs annotáció, akkor unlimit)
        return RestLoggerUtil.getMaxEntityLogSize(requestContext, LogSpecifierTarget.REQUEST);
    }

    /**
     * Returns the maximum response entity log size
     *
     * @param context
     *            {@link WriterInterceptorContext}
     *
     * @return the maximum log size of the entity
     */
    protected int getMaxResponseEntityLogSize(WriterInterceptorContext context) {
        // ha octet-stream vagy multipart és nincs LogSpecifier annotáció, akkor korlátozunk
        if (RestLoggerUtil.isLogSizeLimited(context, MediaType.APPLICATION_OCTET_STREAM_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE)
                && !RestLoggerUtil.isLogSpecifierPresent(context)) {
            return RequestResponseLogger.ENTITY_MAX_LOG;
        }

        // különben annotációban meghatározott maxEntityLogSize (ha nincs annotáció, akkor unlimit)
        return RestLoggerUtil.getMaxEntityLogSize(context, LogSpecifierTarget.RESPONSE);
    }

    /**
     * Prints response URL line.
     *
     * @param fullPath
     *            full url path from request
     * @param httpStatus
     *            400, 401, 500, ...
     * @param statusInfo
     *            OK, ERROR
     * @param mediaType
     *            application/json, text/xml, ...
     * @return response URL line
     */
    protected String printResponseLine(String fullPath, int httpStatus, String statusInfo, String mediaType) {
        StringBuffer sb = new StringBuffer();
        sb.append(RESPONSE_PREFIX).append("Server response from [").append(fullPath).append("]:\n");
        sb.append(RESPONSE_PREFIX).append("Status: [").append(httpStatus).append("], [").append(statusInfo).append("]\n");
        sb.append(RESPONSE_PREFIX).append("Media type: [").append(mediaType).append("]\n");
        return sb.toString();
    }

    /**
     * Prints response URL line.
     *
     * @param requestContext
     *            request context
     * @param responseContext
     *            response context
     * @return response URL line
     * @see #printResponseLine(String, int, String, String)
     */
    public String printResponseLine(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (requestContext == null || responseContext == null) {
            return null;
        }
        return printResponseLine(
                requestContext.getUriInfo().getAbsolutePath().toASCIIString(),
                responseContext.getStatus(),
                String.valueOf(responseContext.getStatusInfo()),
                String.valueOf(responseContext.getMediaType()));
    }

    /**
     * Prints response header values.
     *
     * @param headerValues
     *            {@link Map} of header values
     * @return header parameter values
     */
    public String printResponseHeaders(Map<String, List<Object>> headerValues) {
        StringBuffer sb = new StringBuffer();
        sb.append(RESPONSE_PREFIX).append("-- Header parameters:").append('\n');
        if (headerValues == null) {
            return sb.toString();
        }
        for (Map.Entry<String, List<Object>> param : headerValues.entrySet()) {
            sb.append(RESPONSE_PREFIX).append(param.getKey()).append(": ").append(param.getValue()).append('\n');
        }
        return sb.toString();
    }

    /**
     * Prints response entity object. Tries to print as String or json object.
     *
     * @param entity
     *            entity to log
     * @param mediaType
     *            media type of entity if relevant
     * @return entity in string
     */
    protected String printResponseEntity(Object entity, MediaType mediaType) {
        return printEntity(entity, RequestResponseLogger.RESPONSE_PREFIX, false, mediaType);
    }

    /**
     * Prints entity object. Tries to print as String or json object.
     *
     * @param entity
     *            entity to log
     * @param prefix
     *            log prefix
     * @param maskingNeeded
     *            is masking sensitive data needed
     * @param mediaType
     *            media type of entity if relevant
     * @return entity in string
     */
    public String printEntity(Object entity, String prefix, boolean maskingNeeded, MediaType mediaType) {
        if (entity == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        String entityText;
        if (entity instanceof String) {
            entityText = (String) entity;
        } else if (mediaType != null && MediaType.APPLICATION_JSON_TYPE.getSubtype().equals(mediaType.getSubtype())) {
            entityText = JsonUtil.toJson(entity);
        } else if (mediaType != null && (MediaType.APPLICATION_XML_TYPE.getSubtype().equals(mediaType.getSubtype()) //
                || MediaType.APPLICATION_ATOM_XML_TYPE.getSubtype().equals(mediaType.getSubtype()))) {
            entityText = MarshallingUtil.marshall(entity);
        } else {
            entityText = entity.toString();
        }

        if (maskingNeeded) {
            entityText = StringHelper.maskValueInXmlJson(entityText);
        }

        sb.append(prefix).append("entity: [").append(entityText).append("]\n");
        return sb.toString();
    }

    /**
     * Skipping default specified url and mediaType for entity logging.
     *
     * @param fullPath
     *            URL path to skip
     * @param mediaType
     *            media type to skip
     * @return if entity logging is skipped
     */
    protected boolean skipLoggingForPathOrMediaType(String fullPath, MediaType mediaType) {
        return mediaType != null && (StringUtils.containsIgnoreCase(mediaType.getSubtype(), SKIP_MEDIATYPE_SUBTYPE_PDF)
                || StringUtils.containsIgnoreCase(mediaType.getSubtype(), SKIP_MEDIATYPE_SUBTYPE_CSV)
                || StringUtils.containsIgnoreCase(mediaType.getSubtype(), SKIP_MEDIATYPE_SUBTYPE_SHEET)
                || StringUtils.containsIgnoreCase(fullPath, SKIP_PATH_POSTFIX_WADL)
                || StringUtils.containsIgnoreCase(fullPath, SKIP_PATH_POSTFIX_XSD));
    }

    /**
     * Print response from {@link WriterInterceptorContext}. Printing is disabled in some stream situation.
     *
     * @param fullPath
     *            URL path
     * @param writerInterceptorContext
     *            context
     * @param entityCopy
     *            entity
     * @return response
     */
    public String printResponseEntity(String fullPath, WriterInterceptorContext writerInterceptorContext, byte[] entityCopy) {
        StringBuffer sb = new StringBuffer();
        if (writerInterceptorContext == null) {
            return sb.toString();
        }
        MediaType mediaType = writerInterceptorContext.getMediaType();
        if (skipLoggingForPathOrMediaType(fullPath, mediaType)) {
            sb.append(RequestResponseLogger.RESPONSE_PREFIX)
                    .append("Response outputstream logging disabled, because MediaType: [" + mediaType + "]\n");
        } else {
            String responseText = new String(entityCopy, StandardCharsets.UTF_8);
            sb.append(printResponseEntity(responseText, mediaType));
        }
        return sb.toString();
    }
}
