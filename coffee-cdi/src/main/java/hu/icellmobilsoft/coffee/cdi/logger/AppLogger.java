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
     * request szinten eltarolt valtozo elkerese
     *
     * @param key
     *            key
     * @return value
     */
    public Object getValue(String key);

    /**
     * request szinten eltarolt valtozo elmentese
     *
     * @param key
     *            key
     * @param value
     *            value
     */
    public void setValue(String key, Object value);

    /**
     * request szinten eltarolt valtozo torlese
     *
     * @param key
     *            key
     */
    public void removeValue(String key);

    /**
     * Kiirja a container tartalmat INFO-ba
     */
    public void writeLogToInfo();

    /**
     * Kiirja a container tartalmat ERROR-ba
     */
    public void writeLogToError();

    /**
     * Kiirja a container tartalmat a legmabasabb szintu hibatipuskent, ami a containerben van. Tehat ha van a containerben ERROR akkor error-kent
     * fogja loggolni ha csak INFO akkor infoba fog menni kiiras...
     */
    public void writeLog();

    /**
     * <p>
     * isThereAnyError.
     * </p>
     *
     * @return true hogyha ERROR vagy magasabb szintu log van benne
     */
    public boolean isThereAnyError();

    /**
     * <p>
     * isThereAnyWarning.
     * </p>
     *
     * @return true hogyha WARNING vagy magasabb szintu log van benne
     */
    public boolean isThereAnyWarning();
}
