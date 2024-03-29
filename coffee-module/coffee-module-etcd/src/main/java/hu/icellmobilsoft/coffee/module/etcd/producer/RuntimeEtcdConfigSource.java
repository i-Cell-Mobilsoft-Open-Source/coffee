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
package hu.icellmobilsoft.coffee.module.etcd.producer;

import java.util.Collections;
import java.util.Set;

/**
 * Runtime activable ETCD Config source
 * 
 * @author imre.scheffer
 * @since 2.6.0
 */
public class RuntimeEtcdConfigSource extends DefaultEtcdConfigSource {

    static boolean active = false;

    /**
     * Default constructor, constructs a new object.
     */
    public RuntimeEtcdConfigSource() {
        super();
    }

    @Override
    public Set<String> getPropertyNames() {
        if (!active) {
            return Collections.emptySet();
        }
        return super.getPropertyNames();
    }

    @Override
    public String getValue(String propertyName) {
        if (!active) {
            return null;
        }
        return super.getValue(propertyName);
    }

    /**
     * Activate property finding in ETCD
     * 
     * @param active
     *            true - properties is find on ETCD, false - properties not finding and return default null
     */
    public static void setActive(boolean active) {
        RuntimeEtcdConfigSource.active = active;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
