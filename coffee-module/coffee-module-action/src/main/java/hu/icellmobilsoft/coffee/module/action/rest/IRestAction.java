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
package hu.icellmobilsoft.coffee.module.action.rest;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Action defining a REST operation.
 *
 * @param <T>
 *            the type of the processed request
 * @param <R>
 *            the type of the response
 * 
 * @author attila-kiss-it
 * @since 2.10.0
 */
public interface IRestAction<T, R> {

    /**
     * The function that starts the execution of the operation.
     *
     * @param request
     *            the request
     * @return the response
     * @throws BaseException
     *             in case of error
     */
    R process(T request) throws BaseException;

}
