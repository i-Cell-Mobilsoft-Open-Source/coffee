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

import jakarta.enterprise.inject.Vetoed;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.resteasy.spi.CorsHeaders;

import com.google.common.net.HttpHeaders;

/**
 * REST Response utils
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class ResponseUtil {

    /** Constant <code>MEDIATYPE_APPLICATION_ZIP="application/zip"</code> */
    public final static String MEDIATYPE_APPLICATION_ZIP = "application/zip";

    /**
     * <p>Constructor for ResponseUtil.</p>
     */
    protected ResponseUtil() {
    }

    /**
     * Returns {@link Response} with given status and entity.
     * 
     * @param status
     *            HTTP status code
     * @param entity
     *            response entity
     * @return {@code Response}
     */
    public static Response getResponse(int status, Object entity) {
        ResponseBuilder rb = Response.status(status);
        if (entity != null) {
            rb.entity(entity);
        }
        return rb.build();
    }

    /**
     * Returns {@link Response} with given status and entity.
     *
     * @param status
     *            HTTP status code
     * @param entity
     *            response entity
     * @return {@code Response}
     */
    public static Response getResponse(Status status, Object entity) {
        if (status == null) {
            return null;
        }
        return getResponse(status.getStatusCode(), entity);
    }

    /**
     * Returns {@link Response} with {@link Response.Status#OK} (200) status and given entity.
     *
     * @param entity
     *            response entity
     * @return {@code Response}
     */
    public static Response getOk(Object entity) {
        return getResponse(Status.OK, entity);
    }

    /**
     * Returns {@link Response} with {@link Response.Status#INTERNAL_SERVER_ERROR} (500) status and given entity.
     *
     * @param entity
     *            response entity
     * @return {@code Response}
     */
    public static Response getInternalServerError(Object entity) {
        return getResponse(Status.INTERNAL_SERVER_ERROR, entity);
    }

    /**
     * Returns {@link Response} with given entity, file and content type.
     *
     * @param entity
     *            response entity
     * @param fileName
     *            name of the file
     * @param contentType
     *            content type
     * @return {@code Response}
     */
    public static Response getFileResponse(Object entity, String fileName, String contentType) {
        return Response.ok(entity).header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(CorsHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).build();
    }

    /**
     * Returns XML {@link Response} with given entity and file.
     *
     * @param entity
     *            response entity
     * @param fileName
     *            name of the file
     * @return {@code Response} with {@link MediaType#APPLICATION_XML} content type
     */
    public static Response getXMLResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, MediaType.APPLICATION_XML);
    }

    /**
     * Returns PDF {@link Response} with given entity and file.
     *
     * @param entity
     *            response entity
     * @param fileName
     *            name of the file
     * @return {@code Response} with PDF content type
     */
    public static Response getPDFResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, "application/pdf");
    }

    /**
     * Returns XLSX {@link Response} with given entity and file.
     *
     * @param entity
     *            response entity
     * @param fileName
     *            name of the file
     * @return {@code Response} with XLSX content type
     */
    public static Response getXLSXResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    /**
     * Returns CSV {@link Response} with given entity and file.
     *
     * @param entity
     *            response entity
     * @param fileName
     *            name of the file
     * @return {@code Response} with UTF-8 CSV content type
     */
    public static Response getCSVResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, "text/csv;charset=UTF-8");
    }

    /**
     * Returns ZIP {@link Response} with given entity and file.
     *
     * @param entity
     *            response entity
     * @param fileName
     *            name of the file
     * @return {@code Response} with {@link #MEDIATYPE_APPLICATION_ZIP} content type
     */
    public static Response getZipResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, MEDIATYPE_APPLICATION_ZIP);
    }

    /**
     * Returns if status of the given {@link Response} is equal to the given {@code status}.
     * 
     * @param response
     *            {@code Response} to check
     * @param status
     *            status to check
     * @return if {@code Response}'s status code is equal to {@code status}.
     */
    public static boolean isInStatus(Response response, int status) {
        return response != null && response.getStatus() == status;
    }

    /**
     * Returns if status of the given {@link Response} is equal to the given {@code status}.
     *
     * @param response
     *            {@code Response} to check
     * @param status
     *            status to check
     * @return if {@code Response}'s status code is equal to {@code status}.
     */
    public static boolean isInStatus(Response response, Status status) {
        return status != null && isInStatus(response, status.getStatusCode());
    }

    /**
     * Returns if status of the given {@link Response} is {@link Status#OK} (200).
     *
     * @param response
     *            {@code Response} to check
     * @return if {@code Response}'s status code is OK (200).
     */
    public static boolean isOk(Response response) {
        return isInStatus(response, Status.OK);
    }
}
