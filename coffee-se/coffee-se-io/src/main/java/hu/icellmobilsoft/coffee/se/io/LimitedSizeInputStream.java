/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.se.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import hu.icellmobilsoft.coffee.se.io.exception.SizeLimitExceededIOException;

/**
 * {@link FilterInputStream} extension that throws {@link SizeLimitExceededIOException} if the {@code maxSize} is reached while reading the stream.
 *
 * @author balazs.joo
 * @author zsolt.doma
 * @author attila-kiss-it
 * @since 2.8.0
 */
public class LimitedSizeInputStream extends FilterInputStream {

    private final long maxSize;

    private long readBytes;

    private long markedPosition = -1;

    /**
     * Creates a {@code LimitedSizeInputStream} by assigning the argument {@code in} to the field {@code this.in} so as to remember it for later use.
     * 
     * @param in
     *            the underlying input stream
     * @param maxSize
     *            the maximum size in bytes that is allowed to read
     */
    public LimitedSizeInputStream(InputStream in, long maxSize) {
        super(in);
        this.maxSize = maxSize;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If {@code maxSize} is reached it throws a {@link SizeLimitExceededIOException} that can be handled separately from other {@link IOException}.
     */
    @Override
    public int read() throws IOException {
        int i = super.read();
        if (i >= 0) {
            incrementCounter(1);
        }
        return i;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If {@code maxSize} is reached it throws a {@link SizeLimitExceededIOException} that can be handled separately from other {@link IOException}.
     */
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If {@code maxSize} is reached it throws a {@link SizeLimitExceededIOException} that can be handled separately from other {@link IOException}.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = super.read(b, off, len);
        if (i >= 0) {
            incrementCounter(i);
        }
        return i;
    }

    private void incrementCounter(long size) throws SizeLimitExceededIOException {
        readBytes += size;
        if (readBytes > maxSize) {
            throw new SizeLimitExceededIOException(
                    MessageFormat.format("InputStream exceeded maximum size in bytes. Read bytes [{0}], maximum size [{1}]", readBytes, maxSize));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * If {@code maxSize} is reached while skipping bytes it throws a {@link SizeLimitExceededIOException} that can be handled separately from other
     * {@link IOException}.
     */
    @Override
    public long skip(long n) throws IOException {
        long skipped = super.skip(n);
        incrementCounter(skipped);
        return skipped;
    }

    @Override
    public synchronized void mark(int readlimit) {
        super.mark(readlimit);
        markedPosition = readBytes;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        readBytes = markedPosition;
    }

}
