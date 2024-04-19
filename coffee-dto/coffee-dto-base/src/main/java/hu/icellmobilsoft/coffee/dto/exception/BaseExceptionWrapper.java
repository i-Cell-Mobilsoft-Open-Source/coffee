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
package hu.icellmobilsoft.coffee.dto.exception;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Nem céges kivételek, amik nem a BaseException-bol öröklődnek, ezen az interfészen keresztül tudnak kapcsolódni a céges kivételkezelő rendszerhez.
 *
 * Az interfész célja, hogy a céges kivételeket be lehessen burkolni olyan alacsony szintű JAX-RS metódusokban, amik kötött dobási szabályal
 * rendelkeznek.
 *
 * Példa:
 *
 * <pre>
 * <code>
 * public class SpecialProcessingException extends ProcessingException implements BaseExceptionWrapper&lt;MyException&gt; {
 *     private MyException baseException;
 *
 *     &#64;Override
 *     public void setBaseException(final MyException baseException) {
 *         this.baseException = baseException;
 *     }
 *
 *     &#64;Override
 *     public MyException getBaseException() {
 *         return getBaseException();
 *     }
 * }
 * </code>
 * </pre>
 *
 * <pre>
 * <code>
 * &#64;Override
 * public BasicRequestType readFrom(Class&lt;BasicRequestType&gt; type, Type genericType, Annotation[] annotations, MediaType mediaType,
 *         MultivaluedMap&lt;String, String&gt; httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
 *
 *     BasicRequestType result = null;
 *     try {
 *         // ...
 *     } catch (MyException e) {
 *         throw new SpecialProcessingException(e.getMessage(), e);
 *     }
 *     return result;
 * }
 * </code>
 * </pre>
 *
 * @param <E>
 *            Kivétel osztály, ami a keretrendszer BaseException osztályából származik.
 * @author attila.gluck
 * @author attila.nyers
 * @author ferenc.lutischan
 * @since 1.0.0
 */
public interface BaseExceptionWrapper<E extends BaseException> {
    /**
     * setException.
     *
     * @param exception
     *            exception
     */
    void setException(final E exception);

    /**
     * getException.
     *
     * @return E
     */
    E getException();
}
