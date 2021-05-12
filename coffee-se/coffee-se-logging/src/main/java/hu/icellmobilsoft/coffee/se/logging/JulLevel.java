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
package hu.icellmobilsoft.coffee.se.logging;

import java.util.logging.Level;

/**
 * Java Util Logging level extension, to enable use of DEBUG, TRACE levels. <br>
 * Based on {@code org.jboss.logging.JDKLevel}
 *
 * @author mark.petrenyi
 * @since 1.1.0
 */
public final class JulLevel extends Level {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Jul level.
     *
     * @param name
     *            the level name
     * @param value
     *            the level value
     */
    protected JulLevel(final String name, final int value) {
        super(name, value);
    }

    /**
     * The JulLevel ERROR, indicating some errors.
     */
    public static final JulLevel ERROR = new JulLevel("ERROR", 1000);
    /**
     * The JulLevel WARN, indicating potential problems of misconfiguration.
     */
    public static final JulLevel WARN = new JulLevel("WARN", 900);
    /**
     * The JulLevel INFO, providing informational massages.
     */
    @SuppressWarnings("hiding")
    public static final JulLevel INFO = new JulLevel("INFO", 800);
    /**
     * The JulLevel DEBUG, providing detailed informations for debugging.
     */
    public static final JulLevel DEBUG = new JulLevel("DEBUG", 500);
    /**
     * The JulLevel TRACE, providing fine grade trace information.
     */
    public static final JulLevel TRACE = new JulLevel("TRACE", 400);

}
