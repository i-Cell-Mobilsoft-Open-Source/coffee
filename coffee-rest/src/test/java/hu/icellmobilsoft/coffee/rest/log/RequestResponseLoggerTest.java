package hu.icellmobilsoft.coffee.rest.log;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResponse;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;

/**
 * RequestResponseLogger class tests
 * 
 * @author peter.szabo
 */
@DisplayName("RequestResponseLogger class tests")
public class RequestResponseLoggerTest {

    private RequestResponseLogger requestResponseLogger = new RequestResponseLogger();

    @Test
    @DisplayName("Test printEntity with Json object and application/json;charset=UTF-8 media type")
    public void printJsonEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = new MediaType("application","json",StandardCharsets.UTF_8.displayName());
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [{\"context\":{\"requestId\":\"TEST\",\"timestamp\""));

    }

    @Test
    @DisplayName("Test printEntity with String object and without media type")
    public void printStringEntityTest() {
        String responseText = requestResponseLogger.printEntity("<div>Hello World</div>", null, "", false, null);
        Assertions.assertTrue(responseText.startsWith("entity: [<div>Hello World</div>]"));
    }

    @Test
    @DisplayName("Test printEntity with Object object and without media type")
    public void printObjectEntityTest() {
        String responseText = requestResponseLogger.printEntity(new Object(), null, "", false, null);
        Assertions.assertTrue(responseText.startsWith("entity: [java.lang.Object@"));
    }

    @Test
    @DisplayName("Test printEntity with XML object and application/xml media type")
    public void printXmlEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = new MediaType("application","xml",StandardCharsets.UTF_8.displayName());
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [<?xml"));
    }

    @Test
    @DisplayName("Test printEntity with XML object and application/atom+xml media type")
    public void printAtomPlusXmlMediaTypeEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [<?xml"));
    }

    @Test
    @DisplayName("Test printEntity with XML object and text/xml media type")
    public void printTextXmlMediaTypeEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = MediaType.TEXT_XML_TYPE;
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [<?xml"));
    }
}
