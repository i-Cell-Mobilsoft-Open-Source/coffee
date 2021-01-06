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
package hu.icellmobilsoft.coffee.rest.validation.catalog;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * Implements LSInput. All that we need is the systemId since the Catalog has already resolved it.
 *
 * @author mark.petrenyi
 * @author imre.scheffer
 * @since 1.0.0
 */
public class CatalogLsInputImpl implements LSInput {

    private String systemId;

    /**
     * Constructor for CatalogLsInputImpl.
     *
     * @param systemId
     *            system id
     */
    public CatalogLsInputImpl(String systemId) {
        this.systemId = systemId;
    }

    /** {@inheritDoc} */
    @Override
    public Reader getCharacterStream() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setCharacterStream(Reader characterStream) {
        // default
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getByteStream() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setByteStream(InputStream byteStream) {
        // default
    }

    /** {@inheritDoc} */
    @Override
    public String getStringData() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setStringData(String stringData) {
        // default
    }

    /** {@inheritDoc} */
    @Override
    public String getSystemId() {
        return systemId;
    }

    /** {@inheritDoc} */
    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /** {@inheritDoc} */
    @Override
    public String getPublicId() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setPublicId(String publicId) {
        // default
    }

    /** {@inheritDoc} */
    @Override
    public String getBaseURI() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setBaseURI(String baseURI) {
        // default
    }

    /** {@inheritDoc} */
    @Override
    public String getEncoding() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setEncoding(String encoding) {
        // default
    }

    /** {@inheritDoc} */
    @Override
    public boolean getCertifiedText() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setCertifiedText(boolean certifiedText) {
        // default
    }
}
