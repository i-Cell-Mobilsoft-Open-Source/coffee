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

import java.util.Optional;
import java.util.regex.Pattern;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Runtime activable ETCD Config source
 * 
 * @author imre.scheffer
 * @since 2.6.0
 */
public class FilteredEtcdConfigSource extends DefaultEtcdConfigSource {

    private Pattern includePattern;
    private Pattern excludePattern;

    /**
     * Config key for include regex pattern
     */
    public static final String CONFIG_PATTERN_INCLUDE = IConfigKey.COFFEE_CONFIG_SOURCE_PREFIX + "." + FilteredEtcdConfigSource.class.getSimpleName()
            + ".pattern.include";
    /**
     * Config key for exclude regex pattern
     */
    public static final String CONFIG_PATTERN_EXCLUDE = IConfigKey.COFFEE_CONFIG_SOURCE_PREFIX + "." + FilteredEtcdConfigSource.class.getSimpleName()
            + ".pattern.exclude";

    /**
     * Default constructor, constructs a new object.
     */
    public FilteredEtcdConfigSource() {
        super();
        Optional<String> includePatternValue = getConfig().getOptionalValue(CONFIG_PATTERN_INCLUDE, String.class);
        System.out.println(includePatternValue);
        if (includePatternValue.isPresent()) {
            includePattern = Pattern.compile(includePatternValue.get());
        }
        Optional<String> excludePatternValue = getConfig().getOptionalValue(CONFIG_PATTERN_EXCLUDE, String.class);
        System.out.println(excludePatternValue);
        if (excludePatternValue.isPresent()) {
            excludePattern = Pattern.compile(excludePatternValue.get());
        }
    }

    @Override
    protected Optional<String> readValue(String propertyName) throws BaseException {
        System.out.println(propertyName);
        System.out.println(matchPatterns(propertyName));
        System.out.println(toString());
        if (!matchPatterns(propertyName)) {
            return null;
        }
        return super.readValue(propertyName);
    }

    private boolean matchPatterns(String propertyName) {
        boolean excludeNotMatch = true;
        if (excludePattern != null) {
            excludeNotMatch = !excludePattern.matcher(propertyName).find();
        }
        if (!excludeNotMatch) {
            return false;
        }
        boolean includeMatch = true;
        if (includePattern != null) {
            includeMatch = includePattern.matcher(propertyName).find();
        }
        return excludeNotMatch && includeMatch;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return "FilteredEtcdConfigSource [includePattern=" + includePattern + ", excludePattern=" + excludePattern + "]";
    }
}
