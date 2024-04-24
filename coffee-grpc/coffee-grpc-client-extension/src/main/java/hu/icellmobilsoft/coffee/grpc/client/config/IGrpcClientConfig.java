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
package hu.icellmobilsoft.coffee.grpc.client.config;

/**
 * gRPC client configuration interface
 * 
 * @author Imre Scheffer
 * @since 2.1.0
 */
public interface IGrpcClientConfig {

    /**
     * Gets the server host
     * 
     * @return the host
     */
    String getHost();

    /**
     * Gets the server port
     * 
     * @return the port
     */
    Integer getPort();

    /**
     * Get Max inbound Metadata header size in byte. Grpc default is 8192. If an error occurs on the server and sends debugging information to the
     * client, the default size is not sufficient
     * 
     * @return Inbound metadata max size
     */
    Integer getMaxInboundMetadataSize();
}
