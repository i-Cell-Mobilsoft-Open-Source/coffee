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

import javax.enterprise.inject.Vetoed;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.google.common.net.HttpHeaders;
import org.jboss.resteasy.spi.CorsHeaders;

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
     * <p>getResponse.</p>
     */
    public static Response getResponse(int status, Object entity) {
        ResponseBuilder rb = Response.status(status);
        if (entity != null) {
            rb.entity(entity);
        }
        return rb.build();
    }

    /**
     * <p>getResponse.</p>
     */
    public static Response getResponse(Status status, Object entity) {
        if (status == null) {
            return null;
        }
        return getResponse(status.getStatusCode(), entity);
    }

    /**
     * <p>getOk.</p>
     */
    public static Response getOk(Object entity) {
        return getResponse(Status.OK, entity);
    }

    /**
     * <p>getInternalServerError.</p>
     */
    public static Response getInternalServerError(Object entity) {
        return getResponse(Status.INTERNAL_SERVER_ERROR, entity);
    }

    /**
     * <p>getFileResponse.</p>
     */
    public static Response getFileResponse(Object entity, String fileName, String contentType) {
        return Response.ok(entity).header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(CorsHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).build();
    }

    /**
     * <p>getXMLResponse.</p>
     */
    public static Response getXMLResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, MediaType.APPLICATION_XML);
    }

    /**
     * <p>getPDFResponse.</p>
     */
    public static Response getPDFResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, "application/pdf");
    }

    /**
     * <p>getXLSXResponse.</p>
     */
    public static Response getXLSXResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    /**
     * <p>getCSVResponse.</p>
     */
    public static Response getCSVResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, "text/csv;charset=UTF-8");
    }

    /**
     * {@value #MEDIATYPE_APPLICATION_ZIP} típusú csatolmányt ad vissza válaszként.
     *
     * @param entity
     * @param fileName
     */
    public static Response getZipResponse(Object entity, String fileName) {
        return getFileResponse(entity, fileName, MEDIATYPE_APPLICATION_ZIP);
    }

    /**
     * <p>isInStatus.</p>
     */
    public static boolean isInStatus(Response response, int status) {
        return response != null && response.getStatus() == status;
    }

    /**
     * <p>isInStatus.</p>
     */
    public static boolean isInStatus(Response response, Status status) {
        return status != null && isInStatus(response, status.getStatusCode());
    }

    /**
     * <p>isOk.</p>
     */
    public static boolean isOk(Response response) {
        return isInStatus(response, Status.OK);
    }
}
