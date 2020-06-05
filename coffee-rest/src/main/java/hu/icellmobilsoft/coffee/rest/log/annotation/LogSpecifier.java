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

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;

/**
 * Alap loggolast pontosithatjuk mit hogyan szeretnenk kiloggolni. Repeatable, tehát többször definiálható (önmagában vagy {@link LogSpecifiers}-be
 * csomagolva, de egy target-re ({@link LogSpecifierTarget#REQUEST}, {@link LogSpecifierTarget#RESPONSE}) csak egyszer szerepelhet, ezt biztosítja
 * fordítási időben a {@link hu.icellmobilsoft.coffee.rest.log.annotation.processing.LogSpecifiersAnnotationProcessor} <br>
 * Minta:
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
     * Nincs korlatozas a loggolas hossz korlatozasal, alapertelmezett ertek
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
     *         <li>{@link LogSpecifierTarget#REQUEST} => server request-re vonatkozik</li>
     *         <li>{@link LogSpecifierTarget#RESPONSE} => server response-ra vonatkozik</li>
     *         <li>{@link LogSpecifierTarget#CLIENT_REQUEST} => mp client request-re vonatkozik</li>
     *         <li>{@link LogSpecifierTarget#CLIENT_RESPONSE} => mp client response-ra vonatkozik</li>
     *         </ul>
     */
    @Nonbinding
    LogSpecifierTarget[] target() default { LogSpecifierTarget.REQUEST, LogSpecifierTarget.RESPONSE, LogSpecifierTarget.CLIENT_REQUEST,
            LogSpecifierTarget.CLIENT_RESPONSE };

    /**
     * Mekkora lehet a REST entity log maximalis merete a {@link #target()}-re vonatkozóan
     * 
     * @return
     *         <ul>
     *         <li>&lt;0 => unlimit</li>
     *         <li>=0 => nincs loggolas</li>
     *         <li>>0 => loggolas korlatozas</li>
     *         </ul>
     */
    @Nonbinding
    int maxEntityLogSize() default UNLIMIT;

    /**
     * Loggolás teljes kikapcsolása
     * 
     * @return
     *         <ul>
     *         <li>false (default) => van loggolás</li>
     *         <li>true=> nincs loggolás a {@link #target()}-re</li>
     *         </ul>
     */
    @Nonbinding
    boolean noLog() default false;
}
