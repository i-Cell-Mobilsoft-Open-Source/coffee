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

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * HTTP response object data.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class ResponseData {

    private Type responseType;

    private MultivaluedMap<String, Object> headers;

    private MediaType mediaType;

    private int status;

    private byte[] body;

    /**
     * Default constructor, constructs a new object.
     */
    public ResponseData() {
        super();
    }

    /**
     * Getter for the field <code>responseType</code>.
     *
     * @return responseType
     */
    public Type getResponseType() {
        return responseType;
    }

    /**
     * Setter for the field <code>responseType</code>.
     *
     * @param responseType
     *            response type
     */
    public void setResponseType(Type responseType) {
        this.responseType = responseType;
    }

    /**
     * Getter for the field <code>headers</code>.
     *
     * @return headers
     */
    public MultivaluedMap<String, Object> getHeaders() {
        return headers;
    }

    /**
     * Setter for the field <code>headers</code>.
     *
     * @param headers
     *            headers
     */
    public void setHeaders(MultivaluedMap<String, Object> headers) {
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
     * Getter for the field <code>status</code>.
     *
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Setter for the field <code>status</code>.
     *
     * @param status
     *            status
     */
    public void setStatus(int status) {
        this.status = status;
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
        sb.append("-- Type: ").append(System.lineSeparator());
        if (responseType != null) {
            sb.append(responseType).append(System.lineSeparator());
        }
        sb.append("-- Header parameters:").append(System.lineSeparator());
        if (headers != null) {
            for (Map.Entry<String, List<Object>> param : headers.entrySet()) {
                sb.append(param.getKey()).append(": ").append(param.getValue()).append(System.lineSeparator());
            }
        }
        sb.append("-- Media type:").append(System.lineSeparator());
        if (mediaType != null) {
            sb.append(mediaType).append(System.lineSeparator());
        }
        sb.append("-- Body:").append(System.lineSeparator());
        if (body != null) {
            sb.append(new String(body, StandardCharsets.UTF_8)).append(System.lineSeparator());
        }
        sb.append("]");
        return sb.toString();
    }
}
