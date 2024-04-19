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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Cache optimized ETCD Config source
 *
 * @author imre.scheffer
 * @since 1.3.0
 */
public class CachedEtcdConfigSource extends DefaultEtcdConfigSource {

    /**
     * Konfigurációs kulcsokat cachel-i, hogy ne legyenek mindig lekérdezve. Vannak helyzetek amikor többszörösen hívodik (futás közben ugyse kerül be
     * uj konfigurációs kulcs)
     */
    private static final Set<String> PROPERTY_NAME_CACHE = Collections.synchronizedSet(new HashSet<>());

    /**
     * Default constructor, constructs a new object.
     */
    public CachedEtcdConfigSource() {
        super();
    }

    /**
     * Visszaadja a config-source-okon elérhető kulcsokat, az első hívás eredményét cacheli, minden továbbit a cache-ből szolgál ki
     *
     * <br>
     * {@inheritDoc}
     */
    @Override
    public Set<String> getPropertyNames() {
        if (!isEnabled()) {
            // not enabled, dont create cache
            Collections.emptyMap();
        }
        Set<String> propertyNames = new HashSet<>();
        if (PROPERTY_NAME_CACHE.isEmpty()) {
            PROPERTY_NAME_CACHE.addAll(super.getPropertyNames());
        }
        propertyNames.addAll(PROPERTY_NAME_CACHE);
        return propertyNames;
    }

    @Override
    protected Optional<String> readValue(String propertyName) throws BaseException {
        return EtcdConfigSourceCache.instance().getValue(propertyName);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
