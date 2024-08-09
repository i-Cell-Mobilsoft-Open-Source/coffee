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
package hu.icellmobilsoft.coffee.rest.log.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;

/**
 * We can specify the basic logging more precisely, indicating how and what we want to log.
 * It should be repeatable, meaning it can be defined multiple times (either directly or wrapped in {@link LogSpecifiers}), but each target ({@link LogSpecifierTarget#REQUEST},
 * {@link LogSpecifierTarget#RESPONSE}) can only appear once. This is ensured at compile time by the {@link hu.icellmobilsoft.coffee.rest.log.annotation.processing.LogSpecifiersAnnotationProcessor}. <br>
 * Example:
 *
 * <pre>
 * &#64;POST
 * &#64;Consumes(value = { MediaType.TEXT_XML, MediaType.APPLICATION_XML })
 * &#64;Produces(value = { MediaType.TEXT_XML, MediaType.APPLICATION_XML })
 * &#64;LogSpecifier(target = LogSpecifierTarget.REQUEST, maxEntityLogSize = RequestResponseLogger.BYTECODE_MAX_LOG)
 * &#64;LogSpecifier(target = LogSpecifierTarget.RESPONSE, maxEntityLogSize = 50)
 * ExampleResponse postExampleRequest(ExampleRequest exampleRequest) throws InterfaceBaseException;
 * </pre>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Qualifier
@Target({ TYPE, METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@Repeatable(LogSpecifiers.class)
public @interface LogSpecifier {

    /**
     * There is no limit on the length of logging, defaulting to unrestricted.
     */
    public static final int UNLIMIT = -1;
    /**
     * Nincs entity loggolas
     */
    public static final int NO_LOG = 0;

    /**
     * (Required) LogSpecifier target-ei
     * 
     * @return Array of:
     *         <ul>
     *         <li>{@link LogSpecifierTarget#REQUEST} =&gt; server request-re vonatkozik</li>
     *         <li>{@link LogSpecifierTarget#RESPONSE} =&gt; server response-ra vonatkozik</li>
     *         <li>{@link LogSpecifierTarget#CLIENT_REQUEST} =&gt; mp client request-re vonatkozik</li>
     *         <li>{@link LogSpecifierTarget#CLIENT_RESPONSE} =&gt; mp client response-ra vonatkozik</li>
     *         </ul>
     */
    @Nonbinding
    LogSpecifierTarget[] target() default { LogSpecifierTarget.REQUEST, LogSpecifierTarget.RESPONSE, LogSpecifierTarget.CLIENT_REQUEST,
            LogSpecifierTarget.CLIENT_RESPONSE };

    /**
     * What is the maximum allowed REST entity log size for the specified {@link #target()}?
     * 
     * @return
     *         <ul>
     *         <li>&lt;0 =&gt; unlimit</li>
     *         <li>=0 =&gt; Logging is disabled</li>
     *         <li>&gt;0 =&gt; Logging limitation</li>
     *         </ul>
     */
    @Nonbinding
    int maxEntityLogSize() default UNLIMIT;

    /**
     * Disabling logging completely
     * 
     * @return
     *         <ul>
     *         <li>false (default) =&gt; Logging is enabled</li>
     *         <li>true =&gt; No logging for the {@link #target()}.</li>
     *         </ul>
     */
    @Nonbinding
    boolean noLog() default false;
}
