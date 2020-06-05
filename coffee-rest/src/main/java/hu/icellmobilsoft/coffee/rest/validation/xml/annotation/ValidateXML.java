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
package hu.icellmobilsoft.coffee.rest.validation.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

import hu.icellmobilsoft.coffee.cdi.annotation.Version;

/**
 * Ha egy RestService-ben szereplő metódus a request body paraméterét megannotáljuk ezzel az annotációval, akkor a deszerializációt és a validációt a
 * MessageBodyReaderBase megfelelő implementáció végzik
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

    static final class DEFAULT {};
    /**
     * Melyik verziókra aktiválódjon
     *
     * @return
     */
    @Nonbinding
    Version version() default @Version();

    /**
     * A Version-höz rendelt xsd fájl elérési útvonallal
     *
     * @return
     */
    @Nonbinding
    String xsdPath();
}
