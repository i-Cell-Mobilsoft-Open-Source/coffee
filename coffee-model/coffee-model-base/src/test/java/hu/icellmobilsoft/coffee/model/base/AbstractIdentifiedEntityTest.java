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
package hu.icellmobilsoft.coffee.model.base;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.model.base.AbstractEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing AbstractIdentifiedEntity class
 *
 * @author arnold.bucher
 */
@DisplayName("Testing AbstractIdentified class")
public class AbstractIdentifiedEntityTest {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Testing default version")
    public void defaultVersionTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();

        // when

        // then
        assertEquals(0, entity.getVersion());
    }

    @Test
    @DisplayName("Testing version")
    public void versionTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();

        // when
        entity.setVersion(1L);
        entity.setVersion(2L);

        // then
        assertEquals(2, entity.getVersion());
    }

    @Test
    @DisplayName("Testing version")
    public void duplicateTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();

        // when
        entity.setVersion(1L);
        entity.setVersion(1L);

        // then
        assertEquals(1, entity.getVersion());
    }

    @Test
    @DisplayName("Testing rollbackVersion")
    public void rollbackVersionTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();

        // when
        entity.rollbackVersion();
        entity.setVersion(1L);
        entity.rollbackVersion();

        // then
        assertEquals(0, entity.getVersion());
    }

    @Test
    @DisplayName("Testing maxVersion")
    public void maxVersionTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();

        // when
        entity.setVersion(Long.MAX_VALUE);

        // then
        assertEquals(Long.MAX_VALUE, entity.getVersion());
    }

    @Test
    @DisplayName("Testing maxVersion bean validation")
    public void maxVersionBeanValidationTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();

        // when
        entity.setVersion(Long.MAX_VALUE);
        Set<ConstraintViolation<AbstractEntity>> violations = validator.validate(entity);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing update version")
    public void updateVersionTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();

        // when
        entity.setVersion(Long.MAX_VALUE);
        entity.updateVersion();

        // then
        assertEquals(Long.MAX_VALUE, entity.getVersion());
    }

    @Test
    @DisplayName("Testing maxVersion bean validation")
    public void idTest() {
        // given
        DefaultImplAbstractIdentifiedEntity entity = new DefaultImplAbstractIdentifiedEntity();
        String id = "2OEVAEF1H0ITAO0A";

        // when
        entity.setId(id);

        // then
        assertEquals(id, entity.getId());
    }

}
