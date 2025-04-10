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
package hu.icellmobilsoft.coffee.quarkus.extension.tool.deployment;

import java.util.List;

import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.tool.jsonb.FieldOnlyVisibilityStrategy;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

/**
 * Registers necessary coffee-tool related classes for reflection to ensure compatibility with Quarkus native
 *
 * @author janos.hamrak
 * @since 2.11.0
 */
public class ToolReflectionProcessor {

    private static final Logger log = Logger.getLogger(ToolReflectionProcessor.class);

    /**
     * Default constructor
     */
    public ToolReflectionProcessor() {
        // Default constructor for java 21
    }

    /**
     * Register coffee-tool classes for reflection.
     *
     * @return build item with registered classes
     */
    @BuildStep
    ReflectiveClassBuildItem registerForReflection() {
        List<Class<?>> classes = List
                .of(FieldOnlyVisibilityStrategy.class);
        log.info("Registering classes for reflection: " + classes);
        return ReflectiveClassBuildItem.builder(classes.toArray(new Class<?>[0])).constructors(true).methods(true).fields(true).build();
    }
}
