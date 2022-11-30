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
package hu.icellmobilsoft.coffee.rest.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

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
public class OctetStreamBaseResultTypeXmlMessageBodyWriter implements MessageBodyWriter<BaseResultType> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return BaseResultType.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(BaseResultType t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        String xmlFormat = MarshallingUtil.marshall(t);
        if (StringUtils.isNotEmpty(xmlFormat)) {
            entityStream.write(xmlFormat.getBytes(StandardCharsets.UTF_8));
        }
    }
}
