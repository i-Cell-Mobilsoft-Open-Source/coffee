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
package hu.icellmobilsoft.coffee.cdi.annotation.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import hu.icellmobilsoft.coffee.cdi.annotation.Version;
import jakarta.enterprise.util.Nonbinding;

/**
 * If we annotate a method parameter in a RestService with this annotation,
 * then the deserialization and validation are handled by the appropriate implementation of MessageBodyReaderBase.
 *
 * @see ValidateXMLs
 * @see Version
 * @author attila.nyers
 * @author ferenc.lutischan
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface ValidateXML {

    /**
     * Which versions should be activated
     *
     * @return {@link Version}
     */
    @Nonbinding
    Version version() default @Version();

    /**
     * The file path associated with the Version for the XSD file
     *
     * @return path of XSD file
     */
    @Nonbinding
    String xsdPath();
}
