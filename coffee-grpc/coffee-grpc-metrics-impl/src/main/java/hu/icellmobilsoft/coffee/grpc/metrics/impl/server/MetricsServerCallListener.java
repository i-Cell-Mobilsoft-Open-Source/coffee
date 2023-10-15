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
package hu.icellmobilsoft.coffee.grpc.metrics.impl.server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import hu.icellmobilsoft.coffee.grpc.metrics.impl.bundle.MetricsBundle;
import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall.Listener;
import io.grpc.Status;
import io.grpc.Status.Code;

/**
 * Forwarding server call listener to handle metrics. Handles request counter metrics and process duration.
 * 
 * @author czenczl
 * @since 1.14.0
 * @param <ReqT>
 *            The type of message received.
 */
public class MetricsServerCallListener<ReqT> extends ForwardingServerCallListener<ReqT> {

    private final Listener<ReqT> delegate;
    private MetricsBundle metricBundle;
    private Supplier<Code> responseCode;

    /**
     * Creates a new listener to collect metrics
     * 
     * @param delegate
     *            original listener to wrap
     * @param metricBundle
     *            counter and timer function container
     * @param responseCode
     *            determined response code
     */
    public MetricsServerCallListener(Listener<ReqT> delegate, MetricsBundle metricBundle, Supplier<Code> responseCode) {
        this.delegate = delegate;
        this.responseCode = responseCode;
        this.metricBundle = metricBundle;
    }

    @Override
    protected Listener<ReqT> delegate() {
        return delegate;
    }

    @Override
    public void onMessage(ReqT message) {
        metricBundle.getRequestCounter().inc();
        super.onMessage(message);
    }

    @Override
    public void onCancel() {
        updateTimer(Status.Code.CANCELLED);
        super.onCancel();
    }

    @Override
    public void onComplete() {
        updateTimer(responseCode.get());
        super.onComplete();
    }

    private void updateTimer(Code responseCode) {
        metricBundle.getTimerCodeFunction().apply(responseCode).update(Duration.between(metricBundle.getStartTime(), LocalDateTime.now()));
    }

}
