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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.Envelope;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.interceptor.annotation.RedisCached;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManagerConnection;
import hu.icellmobilsoft.coffee.tool.gson.ClassTypeAdapter;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import redis.clients.jedis.Jedis;

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
     * Returns {@link Object} from JSON using Redis cache.
     *
     * @param ctx
     *            context
     * @return {@code Object} from JSON
     * @throws Exception
     *             exception
     */
    @AroundInvoke
    public Object perform(final InvocationContext ctx) throws Exception {
        return getReturnOfCache(ctx);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object getReturnOfCache(final InvocationContext ctx) throws Exception {
        RedisManager redisManager = getRedisManager(ctx.getMethod());
        final String key = getKey(ctx.getMethod(), ctx.getParameters());

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            Optional<String> json = redisManager.run(Jedis::get, "get", key);

            if (json.isEmpty()) {
                log.debug("Data is not cached in Redis, caching key: [{0}]", key);
                Object objectToReturn = ctx.proceed();

                long timeToExpire = getTime(ctx.getMethod());
                Envelope envelope = new Envelope(gson.toJson(objectToReturn), objectToReturn.getClass());

                Optional<String> statusCode = redisManager.run(Jedis::setex, "setex", key, timeToExpire, JsonUtil.toJsonEx(envelope));

                if (statusCode.isPresent() && !StringUtils.equals(statusCode.get(), "OK")) {
                    log.warn("Problems in recording cache - status code [{0}]", statusCode);
                }
                return objectToReturn;
            } else {
                Envelope envelope = gson.fromJson(json.get(), Envelope.class);
                Class type = envelope.getTypeOfJson();

                Object objectToReturn = gson.fromJson(envelope.getJson(), type);

                if (objectToReturn == null) {
                    log.warn("Problems with the object type - Type Envelop [{0}]", type);
                    return ctx.proceed();
                } else {
                    log.debug("Data from Redis: [{0}]", objectToReturn);
                    return objectToReturn;
                }
            }
        } catch (JsonSyntaxException e) {
            log.error("Syntax problem, removing the key!", e);
            redisManager.run(Jedis::del, "del", key);
            return ctx.proceed();
        } catch (Exception e) {
            log.error("Exception on Redis [{0}]", e.getMessage(), e);
            return ctx.proceed();
        } finally {
            CDI.current().destroy(redisManager);
        }
    }

    private RedisManager getRedisManager(Method method) throws BaseException {
        RedisConnection redisConnection = method.getAnnotation(RedisConnection.class);
        if (redisConnection == null) {
            redisConnection = method.getDeclaringClass().getAnnotation(RedisConnection.class);
        }

        if (redisConnection != null) {
            String configKey = redisConnection.configKey();
            String poolConfigKey = redisConnection.poolConfigKey();
            return CDI.current().select(RedisManager.class, new RedisConnection.Literal(configKey, poolConfigKey)).get();
        } else {
            throw new BaseException(MessageFormat.format("@RedisConnection annotation is missing from method: {0}#{1}",
                    method.getDeclaringClass().getCanonicalName(), method.getName()));
        }
    }

    private long getTime(Method method) {
        RedisCached enableCaching = method.getAnnotation(RedisCached.class);

        if (enableCaching == null) {
            enableCaching = method.getDeclaringClass().getAnnotation(RedisCached.class);
        }

        return enableCaching.expireInSeconds();
    }

    private String getKey(Method method, Object[] parameters) {
        final String parametersInLineCustom = Arrays.toString(parameters).replace(" ", "").replace("null", "");

        return method.getDeclaringClass().getSimpleName() + method.getName() + parametersInLineCustom;
    }
}
