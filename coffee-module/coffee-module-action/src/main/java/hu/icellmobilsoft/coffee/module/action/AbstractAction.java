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
package hu.icellmobilsoft.coffee.module.action;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Abstract definition of an action.
 *
 * @param <T>
 *            the type of the processed parameter
 *
 * @author attila-kiss-it
 * @since 2.10.0
 */
public abstract class AbstractAction<T> {

    /**
     * Initiates the action based on the {@code parameter}. It is the implementation's responsibility to save the {@code parameter} in the memory so
     * that it is available in later steps.
     *
     * @param parameter
     *            the parameter of the action
     * @throws BaseException
     *             in case of error
     */
    protected abstract void init(T parameter) throws BaseException;

    /**
     * Validates the {@code parameter} and checks if operation can be executed.
     *
     * @throws BaseException
     *             in case of error or if the the operation cannot be executed
     */
    protected abstract void validateState() throws BaseException;

    /**
     * Collecting and loading the data necessary for processing.
     *
     * @throws BaseException
     *             in case of error
     */
    protected abstract void collectData() throws BaseException;

    /**
     * Processes the collected data based on the business logic. It can call further actions and helpers to produce more structured and readable code.
     *
     * @throws BaseException
     *             in case of error
     */
    protected abstract void processData() throws BaseException;

    /**
     * Saving the data in one transaction. It is the implementation's responsibility to provide the transaction.
     *
     * @throws BaseException
     *             in case of error
     */
    protected abstract void saveData() throws BaseException;

}
