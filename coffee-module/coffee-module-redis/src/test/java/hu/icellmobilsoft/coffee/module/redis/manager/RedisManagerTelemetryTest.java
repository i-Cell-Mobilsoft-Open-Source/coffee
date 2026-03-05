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
package hu.icellmobilsoft.coffee.module.redis.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import redis.clients.jedis.UnifiedJedis;

/**
 * Unit test for RedisManager telemetry (appendTelemetry).
 * Uses OpenTelemetry SDK testing to verify span attributes through the public
 * API,
 * without static mocking or package-private test hooks.
 *
 * @author gabor.balazs
 * @since 2.13.0
 */
class RedisManagerTelemetryTest {

    private InMemorySpanExporter spanExporter;
    private SdkTracerProvider tracerProvider;
    private Tracer tracer;

    @BeforeEach
    void setUp() {
        spanExporter = InMemorySpanExporter.create();
        tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(spanExporter))
                .build();
        tracer = tracerProvider.get("test");
    }

    @AfterEach
    void tearDown() {
        tracerProvider.close();
    }

    @Test
    void testXaddSetsSpanAttributes() throws Exception {
        // given
        RedisManager redisManager = createRedisManager("localhost:6379");

        Span parentSpan = tracer.spanBuilder("test-parent").startSpan();
        try (Scope scope = parentSpan.makeCurrent()) {
            // when - call the public run() with xadd
            redisManager.run((jedis, streamName) -> "OK", "xadd", "test-stream");
        } finally {
            parentSpan.end();
        }

        // then
        List<SpanData> spans = spanExporter.getFinishedSpanItems();
        SpanData spanData = spans.get(0);

        assertEquals("test-stream",
                spanData.getAttributes().get(AttributeKey.stringKey(SpanAttribute.Redis.Stream.REDIS_STREAM_NAME)));
        assertEquals("localhost",
                spanData.getAttributes().get(AttributeKey.stringKey(SpanAttribute.SERVER_ADDRESS)));
        assertEquals(6379L,
                spanData.getAttributes().get(AttributeKey.longKey(SpanAttribute.SERVER_PORT)));
    }

    @Test
    void testNonXaddSkipsSpanAttributes() throws Exception {
        // given
        RedisManager redisManager = createRedisManager("localhost:6379");

        Span parentSpan = tracer.spanBuilder("test-parent").startSpan();
        try (Scope scope = parentSpan.makeCurrent()) {
            // when - call with non-xadd operation
            redisManager.run((jedis, key) -> "value", "get", "someKey");
        } finally {
            parentSpan.end();
        }

        // then - no redis-specific attributes should be set
        List<SpanData> spans = spanExporter.getFinishedSpanItems();
        SpanData spanData = spans.get(0);

        assertNull(spanData.getAttributes().get(
                AttributeKey.stringKey(SpanAttribute.Redis.Stream.REDIS_STREAM_NAME)));
        assertNull(spanData.getAttributes().get(
                AttributeKey.stringKey(SpanAttribute.SERVER_ADDRESS)));
    }

    @Test
    void testXaddWithInvalidSpanSkips() throws Exception {
        // given
        RedisManager redisManager = createRedisManager("localhost:6379");

        // when - no active span context, Span.current() returns invalid span
        redisManager.run((jedis, streamName) -> "OK", "xadd", "test-stream");

        // then - no spans exported, no attributes set
        List<SpanData> spans = spanExporter.getFinishedSpanItems();
        assertEquals(0, spans.size());
    }

    private RedisManager createRedisManager(String address) throws Exception {
        RedisManager redisManager = new RedisManager();

        setField(redisManager, "log", Logger.getLogger(RedisManagerTelemetryTest.class));
        setField(redisManager, "jedis", mock(UnifiedJedis.class));
        setField(redisManager, "cachedRedisAddress", address);

        return redisManager;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
