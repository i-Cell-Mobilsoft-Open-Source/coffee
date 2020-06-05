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
package hu.icellmobilsoft.coffee.tool.utils.string;

import java.util.Optional;

import javax.enterprise.context.Dependent;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;

/**
 * Helper class for String utility functions with 3rd party dependencies (ie. microprofile-config)
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@Dependent
public class StringHelper {

    /** Constant <code>DEFAULT_PATTERN=".*?(pass|secret).*?"</code> */
    public static final String DEFAULT_PATTERN = ".*?(pass|secret).*?";

    // PM: Csak a default config source-okat huzhatjuk be, mivel ez az osztály használva van az etcd config sourc-ban (emiatt a
    // @org.eclipse.microprofile.config.inject.ConfigProperty annotacio nem megfelelo)
    private Config config = ConfigProviderResolver.instance().getBuilder().addDefaultSources().build();

    /**
     * Masks value, if key ignore-case matches a pattern.
     *
     * The pattern can be set with {@value IConfigKey#LOG_SENSITIVE_KEY_PATTERN} using microprofile-config's default config sources:
     * <ol>
     * <li>System properties</li>
     * <li>Environment properties</li>
     * <li>/META-INF/microprofile-config.properties</li>
     * </ol>
     *
     * If pattern is not set via config it dafaults to {@value #DEFAULT_PATTERN}
     *
     * @param key
     *            The key to check against keyPattern.
     * @param value
     *            The value to mask
     * @return "*" if key and pattern are not blank and key matches pattern (case ignored); value otherwise
     */
    public String maskPropertyValue(String key, Object value) {
        return StringUtil.maskPropertyValue(key, value, getSensitiveKeyPattern());
    }

    /**
     * Masks values belonging to properties ignore-case matching a defined keyPattern in XML or JSON texts. <br>
     * The pattern can be set with {@value IConfigKey#LOG_SENSITIVE_KEY_PATTERN} using microprofile-config's default config sources:
     * <ol>
     * <li>System properties</li>
     * <li>Environment properties</li>
     * <li>/META-INF/microprofile-config.properties</li>
     * </ol>
     *
     * If pattern is not set via config it dafaults to {@value #DEFAULT_PATTERN} <br>
     * ie witch default pattern:<br>
     * keypattern = {@code .*?(pass|secret).*?}
     * <table border="1">
     * <tbody>
     * <tr>
     * <td>text</td>
     * <td>result</td>
     * </tr>
     * <tr>
     * <td>{@code <Password>abc</Password>}</td>
     * <td>{@code <Password>*</Password>}</td>
     * </tr>
     * <tr>
     * <td>{@code <verySecretToken>abc</verySecretToken>}</td>
     * <td>{@code <verySecretToken>*</verySecretToken>}</td>
     * </tr>
     * <tr>
     * <td>{@code <userName>abc</userName>}</td>
     * <td>{@code <userName>abc</userName>}</td>
     * </tr>
     * <tr>
     * <td>"pass":"abc"</td>
     * <td>"pass":"*"</td>
     * </tr>
     * <tr>
     * <td>"userPassword":"abc"</td>
     * <td>"userPassword":"*"</td>
     * </tr>
     * <tr>
     * <td>"userName":"abc"</td>
     * <td>"userName":"abc"</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param text
     *            XML or JSON text to replace sensitive data
     */
    public String maskValueInXmlJson(String text) {
        // Default config sources (sys, env, microprofile-config.properties)
        return StringUtil.maskValueInXmlJson(text, getSensitiveKeyPattern());
    }

    /**
     * <p>getSensitiveKeyPattern.</p>
     *
     * @return value of config key {@value IConfigKey#LOG_SENSITIVE_KEY_PATTERN} if set, {@value DEFAULT_PATTERN} otherwise.
     */
    public String getSensitiveKeyPattern() {
        // Default config sources (sys, env, microprofile-config.properties)
        Optional<String> patternOpt = config.getOptionalValue(IConfigKey.LOG_SENSITIVE_KEY_PATTERN, String.class);
        return patternOpt.orElse(DEFAULT_PATTERN);
    }

}
