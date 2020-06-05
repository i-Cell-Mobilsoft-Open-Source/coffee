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
package hu.icellmobilsoft.coffee.rest.validation.xml.utils;

import org.w3c.dom.ls.LSResourceResolver;

/**
 * LSResourceResolver interfészt terjeszti ki<br>
 * Alap implementációja a:<br>
 *
 * @see XsdResourceResolver
 * @author ferenc.lutischan
 * @since 1.0.0
 */
public interface IXsdResourceResolver extends LSResourceResolver {

    /**
     * A paraméterben kapott Xsd elérési útvonalat lementi egy belső változóba (ha szükséges).
     *
     * @param xsdDirPath
     */
    void setXsdDirPath(String xsdDirPath);
}
