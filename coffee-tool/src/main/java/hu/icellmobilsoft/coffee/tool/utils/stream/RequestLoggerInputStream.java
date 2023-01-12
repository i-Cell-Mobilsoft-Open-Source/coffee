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

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;

/**
 * Custom {@link InputStream} for logging request with entity
 *
 * @author mate.biro
 * @since 1.13.0
 */
@Vetoed
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
     *            original inputStream
     * @param requestPrefix
     *            original inputStream
     * @param message
     *            original inputStream
     */
    public RequestLoggerInputStream(InputStream inputStream, int logReadLimit, String requestPrefix, StringBuilder message) {
        this.inputStream = inputStream;
        this.logReadLimit = logReadLimit;
        this.requestPrefix = requestPrefix;
        this.message = message;
    }

    /** {@inheritDoc} */
    @Override
    public int read() throws IOException {
        int streamData = inputStream.read();

        buildEntity(streamData);
        logRequestWithEntity(streamData);

        return streamData;

    }

    private void buildEntity(int streamData) {
        // logoláshoz gyűjtjük a stream tartalmát amíg van, vagy még nem értük el a limitet
        if (!firstReadCycle || streamData == -1 && logReadLimit == 0) {
            return;
        }
        entity.append((char) streamData);
        logReadLimit--;
    }

    private void logRequestWithEntity(int streamData) {
        // ha a stream végére értünk vagy elértük a limitet, akkor logolunk
        if (!firstReadCycle || streamData != -1 && logReadLimit != 0) {
            return;
        }
        String maskedEntity = getMaskedEntity(entity.toString(), requestPrefix);
        message.append(maskedEntity);
        LogProducer.logToAppLogger((AppLogger appLogger) -> appLogger.info(message.toString()), RequestLoggerInputStream.class);
        firstReadCycle = false;
    }

    private String getMaskedEntity(String requestText, String prefix) {
        String maskedText = StringHelper.maskValueInXmlJson(requestText);
        return prefix + "entity: [" + maskedText + "]\n";
    }
}
