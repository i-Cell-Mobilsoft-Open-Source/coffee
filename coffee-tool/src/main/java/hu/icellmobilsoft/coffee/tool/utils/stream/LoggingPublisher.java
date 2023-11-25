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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * Logging event publisher
 *
 * @author mate.biro
 * @since 1.15.0
 */
@ApplicationScoped
public class LoggingPublisher {

    @Inject
    private Event<LoggingEvent> loggingEvent;

    /**
     * Publisher method for the logging event
     *
     * @param event
     *            containing the message to be logged
     */
    public void publish(LoggingEvent event) {
        loggingEvent.fire(event);
    }
}
