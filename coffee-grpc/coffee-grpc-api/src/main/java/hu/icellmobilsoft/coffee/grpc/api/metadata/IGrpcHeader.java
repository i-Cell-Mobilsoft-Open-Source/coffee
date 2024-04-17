/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.api.metadata;

import io.grpc.Metadata;
import io.grpc.Metadata.Key;

/**
 * Grpc Header metadata constants
 * 
 * @author Imre Scheffer
 * @since 2.7.0
 */
public interface IGrpcHeader {

    /**
     * Language Metadata header key
     */
    Metadata.Key<String> HEADER_LANGUAGE = Key.of("X-LANGUAGE", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * Logging, global transaction session id header key
     */
    Metadata.Key<String> HEADER_SID = Metadata.Key.of("X-SID", Metadata.ASCII_STRING_MARSHALLER);

}
