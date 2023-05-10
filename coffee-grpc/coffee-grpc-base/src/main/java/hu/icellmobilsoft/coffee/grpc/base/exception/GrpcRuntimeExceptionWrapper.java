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

/**
 * GrpcRuntimeExceptionWrapper is a {@link RuntimeException} that wraps another exception.
 * 
 * <p>
 * This class is used to wrap checked exceptions into unchecked exceptions so that they can be propagated through GRPC. The original exception can be
 * retrieved using the {@link #getWrapped()} method.
 * 
 * @author mark.petrenyi
 * 
 * @since 1.14.0
 */
public class GrpcRuntimeExceptionWrapper extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * The original exception
     */
    private final Throwable wrapped;

    /**
     * Constructs a new GrpcRuntimeExceptionWrapper instance with the specified wrapped exception.
     * 
     * @param wrapped
     *            The wrapped exception
     */
    public GrpcRuntimeExceptionWrapper(Throwable wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    /**
     * Returns the wrapped exception.
     * 
     * @return The wrapped exception
     */
    public Throwable getWrapped() {
        return wrapped;
    }
}
