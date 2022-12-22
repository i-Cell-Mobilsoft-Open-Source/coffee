/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.exception.enums;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * HttpStatus codes
 *
 * @author gabor.balazs
 * @since 1.13.0
 */
public enum HttpStatus implements StatusType {

    /**
     * 422 Unprocessable Entity.
     */
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),

    /**
     * 518 Optimistic Lock.
     */
    OPTIMISTIC_LOCK(518, "Optimistic Lock");

    private final int code;
    private final String reason;
    private final Family family;

    HttpStatus(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
        this.family = Family.familyOf(statusCode);
    }

    /**
     * Create a new ResponseBuilder with the supplied httpStatus.
     *
     * @param httpStatus
     *            the response httpStatus.
     * @return a new response builder.
     * @throws IllegalArgumentException
     *             if httpStatus is {@code null}.
     */
    public static Response.ResponseBuilder status(HttpStatus httpStatus) {
        return Response.status(httpStatus);
    }

    /**
     * Get the class of status code.
     *
     * @return the class of status code.
     */
    @Override
    public Family getFamily() {
        return family;
    }

    /**
     * Get the associated status code.
     *
     * @return the status code.
     */
    @Override
    public int getStatusCode() {
        return code;
    }

    /**
     * Get the reason phrase.
     *
     * @return the reason phrase.
     */
    @Override
    public String getReasonPhrase() {
        return toString();
    }

    /**
     * Get the reason phrase.
     *
     * @return the reason phrase.
     */
    @Override
    public String toString() {
        return reason;
    }
}
