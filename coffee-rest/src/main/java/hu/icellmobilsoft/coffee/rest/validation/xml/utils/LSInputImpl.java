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

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

import javax.enterprise.inject.Vetoed;

/**
 * LSInput implementáció
 *
 * @see XsdResourceResolver
 * @author ferenc.lutischan
 * @since 1.0.0
 */
@Vetoed
public class LSInputImpl implements LSInput {
    private Reader characterStream;
    private InputStream byteStream;
    private String stringData;
    private String systemId;
    private String publicId;
    private String baseURI;
    private String encoding;
    private boolean certifiedText;

    /** {@inheritDoc} */
    @Override
    public Reader getCharacterStream() {
        return characterStream;
    }

    /** {@inheritDoc} */
    @Override
    public void setCharacterStream(Reader characterStream) {
        this.characterStream = characterStream;
    }

    /** {@inheritDoc} */
    @Override
    public InputStream getByteStream() {
        return byteStream;
    }

    /** {@inheritDoc} */
    public void setByteStream(InputStream byteStream) {
        this.byteStream = byteStream;
    }

    /** {@inheritDoc} */
    @Override
    public String getStringData() {
        return stringData;
    }

    /** {@inheritDoc} */
    @Override
    public void setStringData(String stringData) {
        this.stringData = stringData;
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
        return publicId;
    }

    /** {@inheritDoc} */
    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    /** {@inheritDoc} */
    @Override
    public String getBaseURI() {
        return baseURI;
    }

    /** {@inheritDoc} */
    @Override
    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    /** {@inheritDoc} */
    @Override
    public String getEncoding() {
        return encoding;
    }

    /** {@inheritDoc} */
    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * <p>Getter for the field <code>certifiedText</code>.</p>
     */
    public boolean getCertifiedText() {
        return certifiedText;
    }

    /** {@inheritDoc} */
    @Override
    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }
}
