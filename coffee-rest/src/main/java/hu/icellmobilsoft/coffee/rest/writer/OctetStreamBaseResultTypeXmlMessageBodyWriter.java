package hu.icellmobilsoft.coffee.rest.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResultType;
import hu.icellmobilsoft.coffee.tool.utils.marshalling.MarshallingUtil;

/**
 * Octet stream response filter a Coffee BaseResultType típusokhoz. Eredetileg nem létezik semmi hasonló a resteasyben, egyszerűen elnyeli a response
 * body objektumot akármilyen figyelmeztetés nélkül. Válaszban XML String van becsomagolva.
 * 
 * @author imre.scheffer
 * @since 0.12.0
 *
 */
@Provider
@Produces(MediaType.APPLICATION_OCTET_STREAM)
public class OctetStreamBaseResultTypeXmlMessageBodyWriter  implements MessageBodyWriter<BaseResultType> {

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return BaseResultType.class.isAssignableFrom(type);
        }

        @Override
        public void writeTo(BaseResultType t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            String xmlFormat = MarshallingUtil.marshall(t);
            entityStream.write(xmlFormat.getBytes(StandardCharsets.UTF_8));
        }
    }
