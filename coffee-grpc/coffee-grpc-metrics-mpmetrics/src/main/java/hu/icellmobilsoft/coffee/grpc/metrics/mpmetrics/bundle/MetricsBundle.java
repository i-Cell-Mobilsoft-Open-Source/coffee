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
package hu.icellmobilsoft.coffee.grpc.metrics.mpmetrics.bundle;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Timer;

import io.grpc.Status.Code;

/**
 * Container for metrics data.
 * 
 * @author czenczl
 * @since 2.1.0
 */
public class MetricsBundle {

    private Counter requestCounter;
    private Counter responseCounter;
    private Function<Code, Timer> timerCodeFunction;

    private LocalDateTime startTime;

    /**
     * Default constructor, constructs a new object.
     */
    public MetricsBundle() {
        super();
    }

    /**
     * Gets counter that counts requests.
     * 
     * @return counter for request metric
     */
    public Counter getRequestCounter() {
        return requestCounter;
    }

    /**
     * Sets counter that counts requests.
     * 
     * @param requestCounter
     *            counter that counts requests
     */
    public void setRequestCounter(Counter requestCounter) {
        this.requestCounter = requestCounter;
    }

    /**
     * Gets counter that counts response.
     * 
     * @return counter for response metric
     */
    public Counter getResponseCounter() {
        return responseCounter;
    }

    /**
     * Sets counter that counts response.
     * 
     * @param responseCounter
     *            counter that counts response
     */
    public void setResponseCounter(Counter responseCounter) {
        this.responseCounter = responseCounter;
    }

    /**
     * Gets the function that representing process duration
     * 
     * @return function for duration and status code
     */
    public Function<Code, Timer> getTimerCodeFunction() {
        return timerCodeFunction;
    }

    /**
     * Gets the function that representing process duration
     * 
     * @param timerCodeFunction
     *            function that representing process duration and status code
     */
    public void setTimerCodeFunction(Function<Code, Timer> timerCodeFunction) {
        this.timerCodeFunction = timerCodeFunction;
    }

    /**
     * Gets the start time of the process
     * 
     * @return process start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the process
     * 
     * @param startTime
     *            metric timer start time
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

}
