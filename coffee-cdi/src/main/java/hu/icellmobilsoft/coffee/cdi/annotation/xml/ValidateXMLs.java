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

import jakarta.enterprise.util.Nonbinding;

/**
 * If we annotate a method's request body parameter in a RestService with this annotation,
 * the deserialization and validation are handled by the appropriate implementation of MessageBodyReaderBase.
 *
 * Example of use:
 *
 * <pre>
 * ExampleResponse postExampleRequest(@Context HttpHeaders headers, @Context HttpServletRequest servletRequest,
 *         &#64;ValidateXMLs({ @ValidateXML(version = @Version(include = @Range(from = "1.0", to = "1.9")), xsdPath = ""), // There is no XSD validation
 *                 &#64;ValidateXML(version = @Version(include = @Range(from = "1.10")), xsdPath = "sample.xsd") }) ExampleRequest exampleRequest)
 *         throws BaseException;
 * </pre>
 *
 * LIt can be a ValidateXML annotation on its own.
 *
 * Full instructions: /docs/howto/xsd_xml_validation_depend_on_version.adoc
 *
 * @see ValidateXML
 * @author ferenc.lutischan
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface ValidateXMLs {

    /**
     * The version-specific mapping of XSD validations (i.e., Version -&gt; XSD mapping)
     *
     * @return {@code ValidateXML} array
     */
    @Nonbinding
    ValidateXML[] value();
}
