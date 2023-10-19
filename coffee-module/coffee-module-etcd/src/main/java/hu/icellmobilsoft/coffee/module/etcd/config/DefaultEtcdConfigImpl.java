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
package hu.icellmobilsoft.coffee.module.etcd.config;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import hu.icellmobilsoft.coffee.tool.utils.config.ConfigUtil;

/**
 * ETCD configuration values from microprofile-config
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class DefaultEtcdConfigImpl implements EtcdConfig {

    /** {@inheritDoc} */
    @Override
    public String[] getUrl() {
        String url = getValue(URL_KEY, "http://localhost:2379", String.class);
        return StringUtils.split(url, ",");
    }

    /** {@inheritDoc} */
    @Override
    public long getConnectionTimeout() {
        return getValue(CONNECTION_TIMEOUT_KEY, 500L, Long.class);
    }

    /** {@inheritDoc} */
    @Override
    public long getRetryDelay() {
        return getValue(RETRY_DELAY, 500L, Long.class);
    }

    /** {@inheritDoc} */
    @Override
    public long getRetryMaxDelay() {
        return getValue(RETRY_MAX_DELAY, 2500L, Long.class);
    }

    /** {@inheritDoc} */
    @Override
    public long getKeepaliveTime() {
        return getValue(KEEPALIVE_TIME, 30L, Long.class);
    }

    /** {@inheritDoc} */
    @Override
    public long getKeepaliveTimeout() {
        return getValue(KEEPALIVE_TIMEOUT, 10L, Long.class);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isKeepaliveWithoutCalls() {
        return getValue(KEEPALIVE_WITHOUT_CALLS, Boolean.TRUE, Boolean.class);
    }

    /** {@inheritDoc} */
    @Override
    public ChronoUnit getRetryChronoUnit() {
        String chronoUnit = getValue(RETRY_CHRONO_UNIT, "MILLIS", String.class);
        return EnumUtils.getEnum(ChronoUnit.class, chronoUnit, ChronoUnit.MILLIS);
    }

    /** {@inheritDoc} */
    @Override
    public long getRetryMaxDuration() {
        return getValue(RETRY_MAX_DURATION, 10L, Long.class);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isWaitForReady() {
        return getValue(WAIT_FOR_READY, Boolean.TRUE, Boolean.class);
    }

    private static <T> T getValue(String key, T defaultValue, Class<T> type) {
        T value;
        Optional<T> optValue = ConfigUtil.getInstance().defaultConfig().getOptionalValue(key, type);
        if (optValue.isEmpty()) {
            Config config = ConfigProviderResolver.instance()
                    .getBuilder()
                    .forClassLoader(DefaultEtcdConfigImpl.class.getClassLoader())
                    .addDefaultSources()
                    .build();
            value = config.getOptionalValue(key, type).orElse(defaultValue);
        } else {
            value = optValue.get();
        }
        return value;
    }

}
