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
package hu.icellmobilsoft.coffee.module.mp.opentracing.filter;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

/**
 * 
 * Filter to handle tracing error tags.
 * 
 * @author czenczl
 * @version 0.3.0
 */
@Provider
public class OpenTraceErrorResponseFilter implements ContainerResponseFilter {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private Tracer configuredTracer;

    /**
     * Default constructor, constructs a new object.
     */
    public OpenTraceErrorResponseFilter() {
        super();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Add error tag if http status &gt;= 500
     * Now working only on async rest calls.
     * https://github.com/opentracing-contrib/java-jaxrs/blob/master/opentracing-jaxrs2/src/main/java/io/opentracing/contrib/jaxrs2/server/SpanFinishingFilter.java#L90
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (requestContext == null || responseContext == null) {
            return;
        }

        if (responseContext.getStatus() >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            Span span = configuredTracer.activeSpan();
            if (span == null) {
                log.debug("No active span on response, skip error trace");
                return;
            }
            Tags.ERROR.set(span, true);
        }

    }

}
