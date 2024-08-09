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
package hu.icellmobilsoft.coffee.cdi.logger;

import java.io.Serializable;

/**
 * <p>
 * AppLogger interface.
 * </p>
 *
 * @author ischeffer
 * @since 1.0.0
 */
public interface AppLogger extends hu.icellmobilsoft.coffee.se.logging.Logger, Serializable {

    /**
     * Accessing a variable stored at the request level
     *
     * @param key
     *            key
     * @return value
     */
    public Object getValue(String key);

    /**
     * Saving a variable stored at the request level
     *
     * @param key
     *            key
     * @param value
     *            value
     */
    public void setValue(String key, Object value);

    /**
     * Deleting a variable stored at the request level
     *
     * @param key
     *            key
     */
    public void removeValue(String key);

    /**
     * Logging the contents of the container to INFO
     */
    public void writeLogToInfo();

    /**
     * Logging the contents of the container to ERROR
     */
    public void writeLogToError();

    /**
     * Logging the contents of the container at the lowest level of error type present in the container.
     * So, if there is an ERROR in the container, it will log as ERROR; if only INFO is present, it will log to INFO.
     */
    public void writeLog();

    /**
     * <p>
     * isThereAnyError.
     * </p>
     *
     * @return True if there is an ERROR or higher level log present in it
     */
    public boolean isThereAnyError();

    /**
     * <p>
     * isThereAnyWarning.
     * </p>
     *
     * @return True if there is a WARNING or higher level log present in it
     */
    public boolean isThereAnyWarning();
}
