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

import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;

/**
 * Custom {@link InputStream} for logging request with entity
 *
 * @author mate.biro
 * @since 2.4.0
 */
@SuppressWarnings("InputStreamSlowMultibyteRead")
public class RequestLoggerInputStream extends InputStream {

    private final InputStream inputStream;
    private final String requestPrefix;
    private final StringBuilder entityLog = new StringBuilder();
    private final StringBuilder logMessage;
    private int logCollectLimit;
    private boolean isLogged = false;

    /**
     * Constructor
     *
     * @param requestEntityStream
     *            original requestEntityStream
     * @param logCollectLimit
     *            read limit
     * @param requestPrefix
     *            request log prefix
     * @param logMessage
     *            log message
     */
    public RequestLoggerInputStream(InputStream requestEntityStream, int logCollectLimit, String requestPrefix, StringBuilder logMessage) {
        this.inputStream = requestEntityStream;
        this.logCollectLimit = logCollectLimit;
        this.requestPrefix = requestPrefix;
        this.logMessage = logMessage;
    }

    /**
     * {@inheritDoc}
     *
     * <br>
     * Extra functionality: On read the request entity data is appended from the original {@link InputStream} to an internal {@link StringBuilder}
     * until a given limit is reached (or until the end of stream if log limit exceeds the length of the stream). Then publishes an event to log the
     * given request message with the appended request entity.
     *
     */
    @Override
    public int read() throws IOException {
        int streamData = -1;
        try {
            streamData = inputStream.read();
        } catch (IOException e) {
            // hiba keletkezett az olvasasnal, logolni kell
            prepareAndSendLoggingEvent();

            isLogged = true;

            throw e;
        }

        if (streamData != -1 && logCollectLimit != 0) {
            // logoláshoz gyűjtjük a stream tartalmát amíg van, és még nem értük el a limitet
            entityLog.append((char) streamData);
            logCollectLimit--;
        } else if (!isLogged) {
            // ha a stream végére értünk vagy elértük a limitet és még nem logoltunk,
            // akkor küldünk egy eventet hogy megtörténjen a logolás
            prepareAndSendLoggingEvent();

            isLogged = true;
        }

        return streamData;
    }

    private void prepareAndSendLoggingEvent() {
        String maskedEntityLog = getMaskedEntity(entityLog.toString());
        logMessage.append(maskedEntityLog);

        LoggingEvent event = new LoggingEvent(logMessage.toString());
        LoggingPublisher loggingPublisher = CDI.current().select(LoggingPublisher.class).get();
        loggingPublisher.publish(event);
    }

    private String getMaskedEntity(String entityLogText) {
        String maskedText = StringHelper.maskValueInXmlJson(entityLogText);
        return requestPrefix + "entity: [" + maskedText + "]\n";
    }
}
