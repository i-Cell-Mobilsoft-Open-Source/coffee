/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.action.evict;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

import hu.icellmobilsoft.coffee.cdi.util.ProxyUtils;
import hu.icellmobilsoft.coffee.configuration.ApplicationConfiguration;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.dto.common.config.evict.EvictResponse;
import hu.icellmobilsoft.coffee.dto.evict.Evictable;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

/**
 * Services implementing {@link Evictable} can clear their state on demand. The action iterates through these services.
 *
 * @author tamas.cserhati
 * @author gyorgy.gassama
 * @since 2.11.0
 */
public abstract class EvictAction {

    @Inject
    private ApplicationConfiguration applicationConfiguration;

    @Any
    @Inject
    private Instance<Evictable> evictables;

    /**
     * Eviction operation that iterates over implementations of the {@link Evictable} interface. Explicitly invokes the eviction function for known
     * framework-level services.
     *
     * @return {@link EvictResponse} DTO containing a list of class names implementing {@link Evictable}, supplemented with the names of known
     *         framework-level services.
     */
    public EvictResponse evict() {
        EvictResponse response = new EvictResponse();
        response.setEvictionStart(DateUtil.nowUTC());

        List<String> evicted = new ArrayList<>();

        applicationConfiguration.clear();
        evicted.add(getName(applicationConfiguration));

        if (!evictables.isUnsatisfied()) {
            evictables.forEach(evictable -> {
                evictable.evict();
                evicted.add(getName(evictable));
            });
        }

        for (ConfigSource configSource : ConfigProvider.getConfig().getConfigSources()) {
            if (configSource instanceof Evictable evictable) {
                evictable.evict();
                evicted.add(getName(evictable));
            }
        }

        response.setEvictionEnd(DateUtil.nowUTC());
        response.withEvicted(evicted);
        response.setContext(createContext());
        response.setFuncCode(FunctionCodeType.OK);
        return response;
    }

    private String getName(Object evictable) {
        String name = ProxyUtils.getUnproxiedClass(evictable.getClass()).getName();
        return name.length() <= 255 ? name : name.substring(name.length() - 255);
    }

    /**
     * create default context
     * 
     * @return context
     */
    protected ContextType createContext() {
        ContextType context = new ContextType();
        context.setRequestId(RandomUtil.generateId());
        context.setTimestamp(DateUtil.nowUTCTruncatedToMillis());
        return context;
    }
}
