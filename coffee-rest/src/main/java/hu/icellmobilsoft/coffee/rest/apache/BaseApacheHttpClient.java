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
package hu.icellmobilsoft.coffee.rest.apache;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;

/**
 * BaseApacheHttpClient class.
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Dependent
public class BaseApacheHttpClient {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(BaseApacheHttpClient.class);

    /** constant connection time-out: 1 minute response time */
    public static final int CONNECTION_TIMEOUT_MILLIS = (int) TimeUnit.MINUTES.toMillis(1);

    /** Constant <code>CONTENT_TYPE_TEXT_PLAIN_UTF8</code> */
    public static final ContentType CONTENT_TYPE_TEXT_PLAIN_UTF8 = ContentType.create("text/plain", Consts.UTF_8);

    /** Constant <code>ABBREVIATE_MAX_WIDTH</code> for the maximum width setting for abbreviation in []byte logs **/
    public static final int ABBREVIATE_MAX_WIDTH = 80;

    /** "application/xml" */
    public final static String APPLICATION_XML = "application/xml";
    /** "text/xml" */
    public final static String TEXT_XML = "text/xml";
    /** "application/json" */
    public final static String APPLICATION_JSON = "application/json";

    /**
     * Method called before everything else.
     * 
     * @param request
     *            http request
     * @throws BaseException
     *             if any exception occurs
     */
    protected void beforeAll(HttpRequestBase request) throws BaseException {
    }

    /**
     * Method called before http GET.
     *
     * @param get
     *            http get object
     * @throws BaseException
     *             if any exception occurs
     */
    protected void beforeGet(HttpGet get) throws BaseException {
        beforeAll(get);
    }

    /**
     * Builder method for {@link RequestConfig}.
     * 
     * @return {@code RequestConfig.Builder}
     */
    protected RequestConfig.Builder createRequestConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(getTimeOut()).setConnectTimeout(getTimeOut()).setSocketTimeout(getTimeOut());
    }

    /**
     * Creates {@link HttpClientBuilder} based on given {@link RequestConfig}.
     * 
     * @param requestConfig
     *            request config
     * @return {@code HttpClientBuilder}
     * @throws BaseException
     *             if any exception occurs
     */
    protected HttpClientBuilder createHttpClientBuilder(RequestConfig requestConfig) throws BaseException {
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
    }

    /**
     * Send http GET request.
     *
     * @param url
     *            URL
     * @return {@link HttpResponse}
     * @throws BaseException
     *             if any exception occurs
     */
    public HttpResponse sendClientBaseGet(String url) throws BaseException {
        HttpGet get = new HttpGet(url);
        // HttpClient client = new ContentEncodingHttpClient();

        RequestConfig config = createRequestConfig().build();
        CloseableHttpClient client = createHttpClientBuilder(config).build();

        // SSL lekezeles
        handleSSL(client, get.getURI());

        try {
            // modositasi lehetoseg
            beforeGet(get);
            // kiloggoljuk a requestet
            logRequest(get);

            // kuldjuk
            return client.execute(get);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Method called before http POST.
     *
     * @param post
     *            http post object
     * @throws BaseException
     *             if any exception occurs
     */
    protected void beforePost(HttpPost post) throws BaseException {
        beforeAll(post);
    }

    /**
     * Send http POST request.
     *
     * @param url
     *            URL
     * @param contentType
     *            content type
     * @param entityObject
     *            entity to POST
     * @return {@link HttpResponse}
     * @throws BaseException
     *             if any exception occurs
     */
    public HttpResponse sendClientBasePost(String url, ContentType contentType, Object entityObject) throws BaseException {

        HttpPost post = new HttpPost(url);
        // HttpClient client = new ContentEncodingHttpClient();

        RequestConfig config = createRequestConfig().build();
        CloseableHttpClient client = createHttpClientBuilder(config).build();

        // SSL lekezeles
        handleSSL(client, post.getURI());

        // add header
        post.setHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType() + ";charset=" + contentType.getCharset());

        try {
            String entityString = toJsonString(entityObject);

            StringEntity stringEntity = new StringEntity(entityString, contentType);
            post.setEntity(stringEntity);
            // modositasi lehetoseg
            beforePost(post);
            // kiloggoljuk a requestet
            logRequest(post, entityString);
            // kuldjuk
            return client.execute(post);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Send http POST request.
     *
     * @param url
     *            URL
     * @param contentType
     *            content type
     * @param request
     *            http request
     * @return {@link HttpResponse}
     * @throws BaseException
     *             if any exception occurs
     */
    public HttpResponse sendClientBasePost(String url, ContentType contentType, byte[] request) throws BaseException {

        HttpPost post = new HttpPost(url);
        // HttpClient client = new ContentEncodingHttpClient();

        RequestConfig config = createRequestConfig().build();
        CloseableHttpClient client = createHttpClientBuilder(config).build();

        // SSL lekezeles
        handleSSL(client, post.getURI());

        // add header
        post.setHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType());

        try {
            ByteArrayEntity byteEntityRequest = new ByteArrayEntity(request);
            post.setEntity(byteEntityRequest);
            // modositasi lehetoseg
            beforePost(post);
            // kiloggoljuk a requestet
            logRequestAbbreviated(post, request);
            // kuldjuk
            return client.execute(post);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Method called before http PUT.
     *
     * @param put
     *            http put object
     * @throws BaseException
     *             if any exception occurs
     */
    protected void beforePut(HttpPut put) throws BaseException {
        beforeAll(put);
    }

    /**
     * Send http PUT request.
     *
     * @param url
     *            URL
     * @param contentType
     *            content type
     * @param entityObject
     *            entity to PUT
     * @return {@link HttpResponse}
     * @throws BaseException
     *             if any exception occurs
     */
    public HttpResponse sendClientBasePut(String url, ContentType contentType, Object entityObject) throws BaseException {

        HttpPut put = new HttpPut(url);
        // HttpClient client = new ContentEncodingHttpClient();

        RequestConfig config = createRequestConfig().build();
        CloseableHttpClient client = createHttpClientBuilder(config).build();

        // SSL lekezeles
        handleSSL(client, put.getURI());

        // add header
        put.setHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType() + ";charset=" + contentType.getCharset());

        try {
            String entityString = toJsonString(entityObject);

            StringEntity stringEntity = new StringEntity(entityString, contentType);
            put.setEntity(stringEntity);
            // modositasi lehetoseg
            beforePut(put);
            // kiloggoljuk a requestet
            logRequest(put, entityString);
            // kuldjuk
            return client.execute(put);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Send http PUT request.
     *
     * @param url
     *            URL
     * @param contentType
     *            content type
     * @param request
     *            http request
     * @return {@link HttpResponse}
     * @throws BaseException
     *             if any exception occurs
     */
    public HttpResponse sendClientBasePut(String url, ContentType contentType, byte[] request) throws BaseException {

        HttpPut put = new HttpPut(url);
        // HttpClient client = new ContentEncodingHttpClient();

        RequestConfig config = createRequestConfig().build();
        CloseableHttpClient client = createHttpClientBuilder(config).build();

        // SSL lekezeles
        handleSSL(client, put.getURI());

        // add header
        put.setHeader(HttpHeaders.CONTENT_TYPE, contentType.getMimeType());

        try {
            ByteArrayEntity byteEntityRequest = new ByteArrayEntity(request);
            put.setEntity(byteEntityRequest);
            // modositasi lehetoseg
            beforePut(put);
            // kiloggoljuk a requestet
            logRequestAbbreviated(put, request);
            // kuldjuk
            return client.execute(put);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Method called before http DELETE.
     *
     * @param delete
     *            http delete object
     * @throws BaseException
     *             if any exception occurs
     */
    protected void beforeDelete(HttpDelete delete) throws BaseException {
        beforeAll(delete);
    }

    /**
     * Send http DELETE request.
     * 
     * @param url
     *            URL
     * @return {@link HttpResponse}
     * @throws BaseException
     *             if any exception occurs
     */
    public HttpResponse sendClientBaseDelete(String url) throws BaseException {

        HttpDelete delete = new HttpDelete(url);
        // HttpClient client = new ContentEncodingHttpClient();

        RequestConfig config = createRequestConfig().build();
        CloseableHttpClient client = createHttpClientBuilder(config).build();

        // SSL lekezeles
        handleSSL(client, delete.getURI());

        try {
            // modositasi lehetoseg
            beforeDelete(delete);
            // kiloggoljuk a requestet
            logRequest(delete);
            // kuldjuk
            return client.execute(delete);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Adds param with given key and value to given map.
     * 
     * @param map
     *            {@code Map} to add param to
     * @param key
     *            param key
     * @param value
     *            param value
     */
    public void addParam(Map<String, Object> map, String key, Object value) {
        if (map != null && StringUtils.isNotBlank(key) && value != null) {
            map.put(key, value);
        }
    }

    /**
     * URL encodes given text.
     * 
     * @param s
     *            text to encode
     * @return URL encoded {@code String} or null if encoding error
     */
    public String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // NOTE TK: Impossible case
            LOGGER.error("Unsupported encoding: ", e);
            return null;
        }
    }

    /**
     * URL encodes the elements of given map.
     *
     * @param map
     *            map to encode
     * @return URL encoded {@code String}s appended
     */
    public String urlEncodeUTF8(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey().toString()), urlEncodeUTF8(entry.getValue().toString())));
        }
        return sb.toString();
    }

    /**
     * Logs http request.
     *
     * @param request
     *            {@link HttpRequestBase}, could be extended to its parent object
     * @param entity
     *            instead of using input stream
     */
    protected void logRequest(HttpRequestBase request, String entity) {
        StringBuffer msg = createLogRequest(request);
        msg.append("> entity: [").append(entity).append("]\n");
        LOGGER.info(msg.toString());
    }

    /**
     * Logs http request.
     * 
     * @param request
     *            http request
     */
    protected void logRequest(HttpRequestBase request) {
        LOGGER.info(createLogRequest(request).toString());
    }

    /**
     * Logs http request. As a byte[] can be too long for log purposes at the {@link hu.icellmobilsoft.coffee.se.logging.JulLevel#INFO} level, it is
     * enough to log only the first {@link BaseApacheHttpClient#ABBREVIATE_MAX_WIDTH} bytes. At
     * {@link hu.icellmobilsoft.coffee.se.logging.JulLevel#DEBUG} and {@link hu.icellmobilsoft.coffee.se.logging.JulLevel#TRACE} instead of the
     * abbreviated string, the entire byte[] should be printed
     *
     * @param request
     *            http request
     * @param entityAsByteArray
     *            instead of using input stream
     */
    protected void logRequestAbbreviated(HttpRequestBase request, byte[] entityAsByteArray) {

        if (LOGGER.isTraceEnabled() || LOGGER.isDebugEnabled()) {
            String entity = new String(entityAsByteArray);
            StringBuffer msg = createLogRequest(request);
            msg.append("> entity: [").append(entity).append("]\n");
            LOGGER.debug(msg.toString());
        } else {
            String entity = org.apache.commons.lang3.StringUtils.abbreviate(new String(entityAsByteArray), ABBREVIATE_MAX_WIDTH);
            StringBuffer msg = createLogRequest(request);
            msg.append("> entity: [").append(entity).append("]\n");
            LOGGER.info(msg.toString());
        }
    }
    
    /**
     * Creates log request.
     *
     * @param request
     *            {@link HttpRequestBase}, could be extended to its parent object
     * @return log request {@link StringBuffer}
     */
    protected StringBuffer createLogRequest(HttpRequestBase request) {
        StringBuffer msg = new StringBuffer();
        msg.append(">> ApacheHttpClient.request ->\n");
        msg.append("> url: [").append(request.getMethod()).append(" ").append(request.getURI()).append("]\n");
        msg.append("> headers: [");
        for (Header header : request.getAllHeaders()) {
            msg.append("\n>    ").append(header.getName()).append(":").append(header.getValue());
        }
        msg.append("]\n");
        return msg;
    }

    /**
     * Logs http response info.
     * 
     * @param response
     *            {@link HttpResponse}
     * @param byteEntity
     *            byte array
     * 
     */
    public void logResponse(HttpResponse response, byte[] byteEntity) {
        StringBuffer msg = new StringBuffer();
        msg.append("<< ApacheHttpClient.response ->\n");
        msg.append("< status: [");
        if (response.getStatusLine() != null) {
            msg.append(response.getStatusLine().getStatusCode()).append(" ");
            msg.append(response.getStatusLine().getReasonPhrase()).append("; ");
            msg.append(response.getStatusLine().getProtocolVersion());
        }
        msg.append("]\n");
        msg.append("< locale: [").append(response.getLocale()).append("]\n");
        msg.append("< protocol: [").append(response.getProtocolVersion()).append("]\n");
        msg.append("< headers: [");
        for (Header header : response.getAllHeaders()) {
            msg.append("\n<    ").append(header.getName()).append(":").append(header.getValue());
        }
        msg.append("]\n");
        msg.append("< entity: [");
        msg.append(new String(byteEntity, StandardCharsets.UTF_8));
        msg.append("]\n");
        LOGGER.info(msg.toString());
    }

    /**
     * Handles SSL.
     *
     * @param client
     *            http client
     * @param uri
     *            {@code URI}
     */
    protected void handleSSL(CloseableHttpClient client, URI uri) {
    }

    /**
     * Getter for http time-out.
     *
     * @return time-out milliseconds
     */
    public int getTimeOut() {
        return CONNECTION_TIMEOUT_MILLIS;
    }

    /**
     * Converts JSON object to {@link String}.
     * 
     * @param object
     *            JSON to convert
     * @return JSON {@code String}
     */
    protected String toJsonString(Object object) {
        String entityString = "";
        if (object instanceof String) {
            entityString = (String) object;
        } else {
            // Convert object to JSON string
            entityString = JsonUtil.toJson(object);
        }
        return entityString;
    }
}
