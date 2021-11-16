/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.mp.opentracing.extension;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;

/**
 * 
 * Resolve {@link Tracer} implementation
 * 
 * @author czenczl
 * @since 1.7.0
 */
@ApplicationScoped
public class OpenTraceResolver {

    @Inject
    private Instance<Tracer> tracerInstance;

    private volatile Tracer tracer = null;

    /**
     * uses volatile read and synchronized block to avoid possible duplicate creation of Tracer in multi-threaded env
     * 
     * @return {@link Tracer} Tracer implementation
     */
    public Tracer resolveTracer() {
        Tracer val = tracer;
        if (val != null) {
            return val;
        }
        synchronized (this) {
            if (tracer == null) {
                if (null != tracerInstance && !tracerInstance.isUnsatisfied()) {
                    tracer = this.tracerInstance.get();
                } else {
                    tracer = TracerResolver.resolveTracer();
                }
            }
            return tracer;
        }
    }

}
