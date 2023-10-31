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
import java.io.InputStream;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;

/**
 * Custom {@link InputStream} for logging request with entity
 *
 * @author mate.biro
 * @since 1.15.0
 */
@Vetoed
@SuppressWarnings("InputStreamSlowMultibyteRead")
public class RequestLoggerInputStream extends InputStream {

    private final InputStream inputStream;
    private final String requestPrefix;
    private final StringBuilder entity = new StringBuilder();
    private final StringBuilder message;
    private int logReadLimit;
    private boolean firstReadCycle = true;

    /**
     * Constructor
     *
     * @param inputStream
     *            original inputStream
     * @param logReadLimit
     *            read limit
     * @param requestPrefix
     *            request log prefix
     * @param message
     *            log message
     */
    public RequestLoggerInputStream(InputStream inputStream, int logReadLimit, String requestPrefix, StringBuilder message) {
        this.inputStream = inputStream;
        this.logReadLimit = logReadLimit;
        this.requestPrefix = requestPrefix;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     *
     * Extra functionality: On first read cycle it appends the read request entity data from the original {@link InputStream} to an internal
     * {@link StringBuilder} until a given limit is reached (or until the end of stream if limit is higher). Then logs the given request message with
     * the appended request entity.
     *
     */
    @Override
    public int read() throws IOException {
        int streamData = inputStream.read();

        buildEntity(streamData);
        logRequestWithEntity(streamData);

        return streamData;
    }

    private void buildEntity(int streamData) {
        // logoláshoz gyűjtjük a stream tartalmát amíg van, és még nem értük el a limitet
        if (!firstReadCycle || streamData == -1 || logReadLimit == 0) {
            return;
        }
        entity.append((char) streamData);
        logReadLimit--;
    }

    private void logRequestWithEntity(int streamData) {
        // ha a stream végére értünk vagy elértük a limitet, akkor logolunk
        if (!firstReadCycle || (streamData != -1 && logReadLimit != 0)) {
            return;
        }
        String maskedEntity = getMaskedEntity(entity.toString(), requestPrefix);
        message.append(maskedEntity);

        LoggingEvent event = new LoggingEvent(message.toString());
        LoggingPublisher loggingPublisher = CDI.current().select(LoggingPublisher.class).get();
        loggingPublisher.publish(event);

        firstReadCycle = false;
    }

    private String getMaskedEntity(String requestText, String prefix) {
        String maskedText = StringHelper.maskValueInXmlJson(requestText);
        return prefix + "entity: [" + maskedText + "]\n";
    }
}
