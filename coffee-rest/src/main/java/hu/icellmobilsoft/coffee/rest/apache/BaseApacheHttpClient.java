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
 * <p>BaseApacheHttpClient class.</p>
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Dependent
public class BaseApacheHttpClient {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(BaseApacheHttpClient.class);

    /**
     * connection time-out: 1 perc alatt kell hogy valaszoljanak
     */
    public static final int CONNECTION_TIMEOUT_MILLIS = (int) TimeUnit.MINUTES.toMillis(1);

    /** Constant <code>CONTENT_TYPE_TEXT_PLAIN_UTF8</code> */
    public static final ContentType CONTENT_TYPE_TEXT_PLAIN_UTF8 = ContentType.create("text/plain", Consts.UTF_8);

    /** "application/xml" */
    public final static String APPLICATION_XML = "application/xml";
    /** "text/xml" */
    public final static String TEXT_XML = "text/xml";
    /** "application/json" */
    public final static String APPLICATION_JSON = "application/json";

    /**
     * <p>beforeAll.</p>
     */
    protected void beforeAll(HttpRequestBase request) throws BaseException {
    }

    /**
     * <p>beforeGet.</p>
     */
    protected void beforeGet(HttpGet get) throws BaseException {
        beforeAll(get);
    }

    /**
     * <p>createRequestConfig.</p>
     */
    protected RequestConfig.Builder createRequestConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(getTimeOut()).setConnectTimeout(getTimeOut()).setSocketTimeout(getTimeOut());
    }

    /**
     * <p>createHttpClientBuilder.</p>
     */
    protected HttpClientBuilder createHttpClientBuilder(RequestConfig requestConfig) throws BaseException {
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
    }

    /**
     * <p>sendClientBaseGet.</p>
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
     * <p>beforePost.</p>
     */
    protected void beforePost(HttpPost post) throws BaseException {
        beforeAll(post);
    }

    /**
     * <p>sendClientBasePost.</p>
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
     * <p>sendClientBasePost.</p>
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
            logRequest(post, org.apache.commons.lang3.StringUtils.abbreviate(new String(request), 80));
            // kuldjuk
            return client.execute(post);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>beforePut.</p>
     */
    protected void beforePut(HttpPut put) throws BaseException {
        beforeAll(put);
    }

    /**
     * <p>sendClientBasePut.</p>
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
     * <p>sendClientBasePut.</p>
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
            logRequest(put, org.apache.commons.lang3.StringUtils.abbreviate(new String(request), 80));
            // kuldjuk
            return client.execute(put);
        } catch (ClientProtocolException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "HTTP protocol exception: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>beforeDelete.</p>
     */
    protected void beforeDelete(HttpDelete delete) throws BaseException {
        beforeAll(delete);
    }

    /**
     * <p>sendClientBaseDelete.</p>
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
     * <p>addParam.</p>
     */
    public void addParam(Map<String, Object> map, String key, Object value) {
        if (map != null && StringUtils.isNotBlank(key) && value != null) {
            map.put(key, value);
        }
    }

    /**
     * <p>urlEncodeUTF8.</p>
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
     * <p>urlEncodeUTF8.</p>
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
     * <p>logRequest.</p>
     *
     * @param httpPost
     *            lehet majd boviteni hogyha HttpRequestBase oset hasznaljuk, de most gyorsan meglegyen
     * @param entity
     *            ne a inputstream-et fogdossuk...
     */
    protected void logRequest(HttpRequestBase request, String entity) {
        StringBuffer msg = createLogRequest(request);
        msg.append("> entity: [").append(entity).append("]\n");
        LOGGER.info(msg.toString());
    }

    /**
     * <p>logRequest.</p>
     */
    protected void logRequest(HttpRequestBase request) {
        LOGGER.info(createLogRequest(request).toString());
    }

    /**
     * <p>createLogRequest.</p>
     *
     * @param HttpRequestBase
     *            lehet majd boviteni hogyha HttpRequestBase oset hasznaljuk, de most gyorsan meglegyen
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
     * <p>logResponse.</p>
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
     * <p>handleSSL.</p>
     */
    protected void handleSSL(CloseableHttpClient client, URI uri) {
    }

    /**
     * Http timeOut kezelese
     *
     * @return millisecond
     */
    public int getTimeOut() {
        return CONNECTION_TIMEOUT_MILLIS;
    }

    /**
     * <p>toJsonString.</p>
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
