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
 * @since 1.15.0
 */
@Vetoed
public class ResponseEntityCollectorOutputStream extends OutputStream {

    private final OutputStream originalResponseStream;
    private final StringBuilder entityLog = new StringBuilder();
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
    }

    /**
     * {@inheritDoc}
     *
     * <br/>
     * Extra functionality: It appends the response entity data written to the original {@link OutputStream} to an internal {@link StringBuilder}
     * until the given limit has been reached.
     */
    @Override
    public void write(int b) throws IOException {
        if (logCollectLimit != 0) {
            // logoláshoz gyűjtjük a stream tartalmát amíg még nem értük el a limitet
            entityLog.append((char) b);
            logCollectLimit--;
        }
        originalResponseStream.write(b);
    }

    /**
     * Returns the entity in {@link String} format.
     *
     * @return Entity text
     */
    public String getEntityText() {
        return entityLog.toString();
    }
}
