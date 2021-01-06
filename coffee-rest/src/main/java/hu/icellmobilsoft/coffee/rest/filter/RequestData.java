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
package hu.icellmobilsoft.coffee.rest.filter;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * HTTP request object data.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class RequestData {

    private Map<String, Cookie> cookies;

    private Date messageDate;

    private MultivaluedMap<String, String> headers;

    private MediaType mediaType;

    private String method;

    private String fullPath;

    private Map<String, List<String>> pathParameters;

    private Map<String, List<String>> queryParameters;

    private byte[] body;

    /**
     * Getter for the field <code>cookies</code>.
     *
     * @return cookies
     */
    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    /**
     * Setter for the field <code>cookies</code>.
     *
     * @param cookies
     *            cookies
     */
    public void setCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * Getter for the field <code>messageDate</code>.
     *
     * @return messageDate
     */
    public Date getMessageDate() {
        return messageDate;
    }

    /**
     * Setter for the field <code>messageDate</code>.
     *
     * @param messageDate
     *            message date
     */
    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    /**
     * Getter for the field <code>headers</code>.
     *
     * @return headers
     */
    public MultivaluedMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Setter for the field <code>headers</code>.
     *
     * @param headers
     *            headers
     */
    public void setHeaders(MultivaluedMap<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Getter for the field <code>mediaType</code>.
     *
     * @return mediaType
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Setter for the field <code>mediaType</code>.
     *
     * @param mediaType
     *            media type
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * Getter for the field <code>method</code>.
     *
     * @return method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Setter for the field <code>method</code>.
     *
     * @param method
     *            method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Getter for the field <code>fullPath</code>.
     *
     * @return full path
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * Setter for the field <code>fullPath</code>.
     *
     * @param fullPath
     *            full path
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * Getter for the field <code>pathParameters</code>.
     *
     * @return pathParameters
     */
    public Map<String, List<String>> getPathParameters() {
        return pathParameters;
    }

    /**
     * Setter for the field <code>pathParameters</code>.
     *
     * @param pathParameters
     *            path parameters
     */
    public void setPathParameters(Map<String, List<String>> pathParameters) {
        this.pathParameters = pathParameters;
    }

    /**
     * Getter for the field <code>queryParameters</code>.
     *
     * @return queryParameters
     */
    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    /**
     * Setter for the field <code>queryParameters</code>.
     *
     * @param queryParameters
     *            query parameters
     */
    public void setQueryParameters(Map<String, List<String>> queryParameters) {
        this.queryParameters = queryParameters;
    }

    /**
     * Getter for the field <code>body</code>.
     *
     * @return body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Setter for the field <code>body</code>.
     * 
     * @param body
     *            body
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append(" [");
        sb.append(System.lineSeparator());
        sb.append("-- URL: ").append(System.lineSeparator());
        if (method != null || fullPath != null) {
            sb.append(method).append(" ").append(fullPath).append(System.lineSeparator());
        }
        sb.append("-- Path parameters:").append(System.lineSeparator());
        if (pathParameters != null) {
            for (Map.Entry<String, List<String>> param : pathParameters.entrySet()) {
                sb.append(param.getKey()).append(": ").append(param.getValue()).append(System.lineSeparator());
            }
        }
        sb.append("-- Query parameters:").append(System.lineSeparator());
        if (queryParameters != null) {
            for (Map.Entry<String, List<String>> param : queryParameters.entrySet()) {
                sb.append(param.getKey()).append(": ").append(param.getValue()).append(System.lineSeparator());
            }
        }
        sb.append("-- Media type:").append(System.lineSeparator());
        if (mediaType != null) {
            sb.append(mediaType).append(System.lineSeparator());
        }
        sb.append("-- Header parameters:").append(System.lineSeparator());
        if (headers != null) {
            for (Map.Entry<String, List<String>> param : headers.entrySet()) {
                sb.append(param.getKey()).append(": ").append(param.getValue()).append(System.lineSeparator());
            }
        }
        sb.append("-- Cookies:").append(System.lineSeparator());
        if (cookies != null) {
            for (Map.Entry<String, Cookie> param : cookies.entrySet()) {
                sb.append(param.getKey()).append(": ").append(param.getValue()).append(System.lineSeparator());
            }
        }
        sb.append("-- Message Date:").append(System.lineSeparator());
        if (messageDate != null) {
            sb.append(messageDate).append(System.lineSeparator());
        }
        sb.append("-- Body:").append(System.lineSeparator());
        if (body != null) {
            sb.append(new String(body, StandardCharsets.UTF_8)).append(System.lineSeparator());
        }
        sb.append("]");
        return sb.toString();
    }
}
