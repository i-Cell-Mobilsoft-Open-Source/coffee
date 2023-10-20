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
package hu.icellmobilsoft.coffee.tool.utils.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Copy original output stream to byte[] object. Usage:<br>
 *
 * <pre>
 * // get the stream
 * OutputStream originalStream = context.getOutputStream();
 * // create this stream copier object
 * OutputStreamCopier osc = new OutputStreamCopier(originalStream);
 * // set back this created stream
 * context.setOutputStream(osc);
 * // call stream consumer, example:
 * try {
 *     context.proceed();
 * } finally {
 *     context.setOutputStream(originalStream);
 * }
 * // get the stream data copy and use to anything
 * byte[] byteCopy = osc.getCopy();
 * </pre>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class OutputStreamCopier extends OutputStream {

    private OutputStream outputStream;
    private ByteArrayOutputStream copy;

    /**
     * Constructor
     *
     * @param outputStream
     *            original outputStream what we want to copy
     */
    public OutputStreamCopier(OutputStream outputStream) {
        this.outputStream = outputStream;
        // 1024 byte buffer
        this.copy = new ByteArrayOutputStream(1024);
    }

    /** {@inheritDoc} */
    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        copy.write(b);
    }

    /**
     * Copy of used output stream data.
     * 
     * @return byte array copy
     */
    public byte[] getCopy() {
        return copy.toByteArray();
    }
}
