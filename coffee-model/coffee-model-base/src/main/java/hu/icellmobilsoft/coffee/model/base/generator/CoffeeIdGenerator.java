/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.model.base.generator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import org.hibernate.annotations.IdGeneratorType;

/**
 * {@link String} entity id generator using {@link RandomUtil#generateId()}
 *
 * @author martin.nagy
 * @since 3.0.0
 */
@IdGeneratorType(EntityIdGenerator.class)
@Retention(RUNTIME)
@Target({ METHOD, FIELD })
public @interface CoffeeIdGenerator {
}
