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
package hu.icellmobilsoft.coffee.module.redis.config.converter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.microprofile.config.spi.Converter;

import redis.clients.jedis.HostAndPort;

/**
 * {@link Converter} which is used for converting from "${host}:${port}" pattern {@link String} configured value to {@link HostAndPort} Java type.
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class HostAndPortConverter implements Converter<HostAndPort> {

    private static final String DELIMITER = ":";

    /**
     * Default constructor, constructs a new object.
     */
    public HostAndPortConverter() {
        super();
    }

    @Override
    public HostAndPort convert(String value) throws IllegalArgumentException, NullPointerException {
        String[] hostAndPort = value.split(DELIMITER);
        if (hostAndPort.length != 2 || !StringUtils.isNumeric(hostAndPort[1])) {
            throw new IllegalArgumentException("Invalid host and port format: " + value);
        }
        return new HostAndPort(hostAndPort[0], NumberUtils.toInt(hostAndPort[1]));
    }
}
