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
package hu.icellmobilsoft.coffee.grpc.base.exception;

import com.google.rpc.Status;

/**
 * ExceptionMapper is an interface for mapping an exception of type E to a gRPC {@link com.google.rpc.Status}.
 * <p>
 * This is inspired by the jax-rs {@code ExceptionMapper} interface.
 *
 * @param <E>
 *            The type of exception to be mapped to a gRPC Status
 * @author mark.petrenyi
 * @since 1.14.0
 */
public interface ExceptionMapper<E extends Throwable> {

    /**
     * Maps an exception of type E to a gRPC Status.
     *
     * @param e
     *            The exception to be mapped
     * @return The gRPC Status resulting from the mapping
     */
    Status toStatus(E e);

}
