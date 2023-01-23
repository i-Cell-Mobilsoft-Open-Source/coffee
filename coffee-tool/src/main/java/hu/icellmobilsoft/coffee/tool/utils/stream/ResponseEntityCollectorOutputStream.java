/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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

import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.inject.Vetoed;

/**
 * Custom {@link OutputStream} for collecting response entity
 *
 * @author mate.biro
 * @since 1.14.0
 */
@Vetoed
public class ResponseEntityCollectorOutputStream extends OutputStream {

    private final OutputStream outputStream;
    private final StringBuilder entity = new StringBuilder();
    private int collectLimit;

    /**
     * Constructor
     *
     * @param outputStream
     *            original outputStream
     * @param collectLimit
     *            collect limit
     */
    public ResponseEntityCollectorOutputStream(OutputStream outputStream, int collectLimit) {
        this.outputStream = outputStream;
        this.collectLimit = collectLimit;
    }

    /**
     * {@inheritDoc}
     *
     * Extra functionality: It appends the response entity data written to the original {@link OutputStream} to an internal {@link StringBuilder}
     * until the given limit has been reached.
     */
    @Override
    public void write(int b) throws IOException {
        if (collectLimit != 0) {
            entity.append((char) b);
            collectLimit--;
        }
        outputStream.write(b);
    }

    /**
     * Returns the entity in {@link String} format.
     *
     * @return Entity text
     */
    public String getEntityText() {
        return entity.toString();
    }
}
