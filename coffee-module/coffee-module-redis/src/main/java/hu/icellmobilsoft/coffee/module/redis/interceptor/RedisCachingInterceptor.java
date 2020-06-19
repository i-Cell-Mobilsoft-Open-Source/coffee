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
package hu.icellmobilsoft.coffee.module.redis.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.Envelope;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.interceptor.annotation.RedisCached;
import hu.icellmobilsoft.coffee.module.redis.service.AbstractRedisService;
import hu.icellmobilsoft.coffee.module.redis.service.RedisService;
import hu.icellmobilsoft.coffee.tool.gson.ClassTypeAdapter;

/**
 * <p>
 * RedisCachingInterceptor class.
 * </p>
 *
 * @since 1.0.0
 */
@Interceptor
@RedisCached
public class RedisCachingInterceptor {

    @Inject
    @ThisLogger
    private AppLogger log;

    private Gson gson;

    /**
     * <p>
     * Constructor for RedisCachingInterceptor.
     * </p>
     */
    public RedisCachingInterceptor() {
        gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassTypeAdapter()).create();
    }

    /**
     * <p>
     * perform.
     * </p>
     */
    @AroundInvoke
    public Object perform(final InvocationContext ctx) throws Exception {

        final Object objectToReturn = getReturnOfCache(ctx);

        return objectToReturn;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getReturnOfCache(final InvocationContext ctx) throws Exception {
        Object objectToReturn = null;

        AbstractRedisService redisService = getCdiRedisService(ctx.getMethod());

        final String key = getKey(ctx.getMethod(), ctx.getParameters());

        try {

            final Optional<String> json = redisService.getRedisDataOpt(key, String.class);

            if (!json.isPresent()) {
                log.debug("Data is not cached in Redis, caching key: [{0}]", key);
                objectToReturn = ctx.proceed();

                int timeToExpire = getTime(ctx.getMethod());
                Envelope envelope = new Envelope(gson.toJson(objectToReturn), objectToReturn.getClass());

                String statusCode = redisService.setRedisData(key, timeToExpire, envelope);

                if (!statusCode.equals("OK")) {
                    log.warn("Problems in recording cache - status code [{0}]", statusCode);
                }
            } else {
                Envelope envelope = gson.fromJson(json.get(), Envelope.class);
                Class type = envelope.getTypeOfJson();

                objectToReturn = gson.fromJson(envelope.getJson(), type);

                if (objectToReturn == null) {
                    objectToReturn = ctx.proceed();
                    log.warn("Problems whith the object type - Type Envelop [{0}]", type);
                } else {
                    log.debug("Data from Redis: [{0}]", objectToReturn);
                }
            }
        } catch (JsonSyntaxException e) {
            log.error("Syntax problem, removing the key!", e);
            redisService.removeRedisData(key);
            objectToReturn = ctx.proceed();

        } catch (Exception e) {
            log.error("Exception on Redis [{0}]", e.getMessage(), e);
            objectToReturn = ctx.proceed();
        }
        return objectToReturn;
    }

    private AbstractRedisService getCdiRedisService(Method method) {
        AbstractRedisService redisService = null;
        RedisConnection redisConnection = method.getAnnotation(RedisConnection.class);
        if (redisConnection == null) {
            redisConnection = method.getDeclaringClass().getAnnotation(RedisConnection.class);
        }

        if (redisConnection != null) {
            String configKey = redisConnection.configKey();
            redisService = CDI.current().select(RedisService.class, new RedisConnection.Literal(configKey)).get();
        }
        return redisService;
    }

    private int getTime(Method method) {
        RedisCached enableCaching = method.getAnnotation(RedisCached.class);

        if (enableCaching == null) {
            enableCaching = method.getDeclaringClass().getAnnotation(RedisCached.class);
        }
        final int timeToExpire = enableCaching.expireInSeconds();

        return timeToExpire;
    }

    private String getKey(Method method, Object[] parameters) {
        final String parametersInLineCustom = Arrays.toString(parameters).replace(" ", "").replace("null", "");

        final String key = method.getDeclaringClass().getSimpleName() + method.getName() + parametersInLineCustom;

        return key;
    }
}
