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
package hu.icellmobilsoft.coffee.grpc.server.config;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * gRPC server configuration interface
 * 
 * @author czenczl
 * @since 2.1.0
 */
public interface IGrpcServerConfig {

    /**
     * Default server configuration key
     */
    String DEFAULT_SERVER_KEY = "server";

    /**
     * Gets the server port
     * 
     * @return the port
     * @throws BaseException
     *             Exception on read properties
     */
    Integer getPort() throws BaseException;

    /**
     * Gets custom max connection age, connection lasting longer than which will be gracefully terminated. An unreasonably small value might be
     * increased. A random jitter of +/-10% will be added to it. {@code Long.MAX_VALUE} nano seconds or an unreasonably large value will disable max
     * connection age.
     * 
     * @return the max connection age
     * @throws BaseException
     *             Exception on read properties
     */
    Long getMaxConnectionAge() throws BaseException;

    /**
     * Gets custom grace time for the graceful connection termination. Once the max connection age is reached, RPCs have the grace time to complete.
     * RPCs that do not complete in time will be cancelled, allowing the connection to terminate. {@code Long.MAX_VALUE} nano seconds or an
     * unreasonably large value are considered infinite.
     * 
     * @return the max connection age grace
     * @throws BaseException
     *             Exception on read properties
     */
    Long getMaxConnectionAgeGrace() throws BaseException;

    /**
     * Specify the most aggressive keep-alive time clients are permitted to configure. The server will try to detect clients exceeding this rate and
     * when detected will forcefully close the connection. The default is 5 minutes.
     * 
     * @return the keep alive time
     * @throws BaseException
     *             Exception on read properties
     */
    Long getKeepAliveTime() throws BaseException;

    /**
     * Gets a custom keepalive timeout, the timeout for keepalive ping requests. An unreasonably small value might be increased. The default is 20
     * seconds.
     * 
     * @return the keep alive timeout
     * @throws BaseException
     *             Exception on read properties
     */
    Long getKeepAliveTimeout() throws BaseException;

    /**
     * Gets a custom max connection idle time, connection being idle for longer than which will be gracefully terminated. Idleness duration is defined
     * since the most recent time the number of outstanding RPCs became zero or the connection establishment. An unreasonably small value might be
     * increased. {@code Long.MAX_VALUE} nano seconds or an unreasonably large value will disable max connection idle.
     * 
     * @return the max connection idle
     * @throws BaseException
     *             Exception on read properties
     */
    Long getMaxConnectionIdle() throws BaseException;

    /**
     * Gets the maximum message size allowed to be received on the server. If not called, defaults to 4 MiB. The default provides protection to
     * servers who haven't considered the possibility of receiving large messages while trying to be large enough to not be hit in normal usage.
     * 
     * @return the max inbound message size
     * @throws BaseException
     *             Exception on read properties
     */
    Integer getMaxInboundMessageSize() throws BaseException;

    /**
     * Gets the maximum size of metadata allowed to be received. This is cumulative size of the entries with some overhead, as defined for
     * <a href="http://httpwg.org/specs/rfc7540.html#rfc.section.6.5.2"> HTTP/2's SETTINGS_MAX_HEADER_LIST_SIZE</a>. The default is 8 KiB.
     * 
     * @return the max inbound metadata size
     * @throws BaseException
     *             Exception on read properties
     */
    Integer getMaxInboundMetadataSize() throws BaseException;

    /**
     * Specify the most aggressive keep-alive time clients are permitted to configure. The server will try to detect clients exceeding this rate and
     * when detected will forcefully close the connection. The default is 5 minutes.
     *
     * @return the permit keep alive time
     * @throws BaseException
     *             Exception on read properties
     */
    Long getPermitKeepAliveTime() throws BaseException;

    /**
     * Sets whether to allow clients to send keep-alive HTTP/2 PINGs even if there are no outstanding RPCs on the connection. Defaults to
     * {@code false}.
     * 
     * @return the permit keep alive withput calls
     * @throws BaseException
     *             Exception on read properties
     */
    boolean isPermitKeepAliveWithoutCalls() throws BaseException;

    /**
     * Sets the core pool size, the default is {@code 32}.
     * 
     * @return the number of threads to keep in the pool, even if they are idle
     * @throws BaseException
     *             Exception on read properties
     */
    Integer getThreadPoolCorePoolSize() throws BaseException;

    /**
     * Sets the maxmimum pool size, the default is {@code 32}.
     * 
     * @return the maximum number of threads to allow in the pool
     * @throws BaseException
     *             Exception on read properties
     */
    Integer getThreadPoolMaximumPoolSize() throws BaseException;

    /**
     * Sets the keep alive time for the pool, the default is {@code 0}.
     * 
     * @return when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before
     *         terminating.
     * @throws BaseException
     *             Exception on read properties
     */
    Long getThreadPoolKeepAliveTime() throws BaseException;

    /**
     * Gets whether to use {@code ManagedExecutorService} provided by Jakarta, the default is false.
     * 
     * @return the server uses {@code ManagedExecutorService} to handle threads.
     * @throws BaseException
     *             Exception on read properties
     */
    boolean isThreadPoolJakartaActive() throws BaseException;

}
