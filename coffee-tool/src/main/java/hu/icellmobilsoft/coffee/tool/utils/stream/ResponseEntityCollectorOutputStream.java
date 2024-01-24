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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Custom {@link OutputStream} for collecting response entity
 *
 * @author mate.biro
 * @since 2.4.0
 */
public class ResponseEntityCollectorOutputStream extends OutputStream {

    private static final int DEFAULT_INITIAL_BYTE_ARRAY_SIZE = 1024;
    private final OutputStream originalResponseStream;
    private final ByteArrayOutputStream entityLog;
    private int logCollectLimit;

    /**
     * Constructor
     *
     * @param originalResponseStream
     *            the original intercepted output stream
     * @param logCollectLimit
     *            collect limit
     */
    public ResponseEntityCollectorOutputStream(OutputStream originalResponseStream, int logCollectLimit) {
        this.originalResponseStream = originalResponseStream;
        this.logCollectLimit = logCollectLimit;
        entityLog = new ByteArrayOutputStream(logCollectLimit > 0 ? logCollectLimit : DEFAULT_INITIAL_BYTE_ARRAY_SIZE);
    }

    /**
     * {@inheritDoc}
     *
     * <br>
     * Extra functionality: It appends the response entity data written to the original {@link OutputStream} to an internal {@link StringBuilder}
     * until the given limit has been reached.
     */
    @Override
    public void write(int b) throws IOException {
        if (logCollectLimit != 0) {
            // logoláshoz gyűjtjük a stream tartalmát amíg még nem értük el a limitet
            entityLog.write(b);
            logCollectLimit--;
        }
        originalResponseStream.write(b);
    }

    /**
     * Returns the entity as byte array
     *
     * @return Entity byte array
     */
    public byte[] getEntity() {
        return entityLog.toByteArray();
    }
}

