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

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.model.base.AbstractEntity;
import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing AbstractIdentifiedAuditEntity class
 *
 * @author arnold.bucher
 */
@DisplayName("Testing AbstractIdentifiedAuditEntity class")
public class AbstractIdentifiedAuditEntityTest {

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
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when

        // then
        assertEquals(0, entity.getVersion());
    }

    @Test
    @DisplayName("Testing version")
    public void versionTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

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
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

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
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

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
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when
        entity.setVersion(Long.MAX_VALUE);

        // then
        assertEquals(Long.MAX_VALUE, entity.getVersion());
    }

    @Test
    @DisplayName("Testing maxVersion bean validation")
    public void maxVersionBeanValidationTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when
        entity.setCreationDate(new Date());
        entity.setCreatorUser("0");
        entity.setVersion(Long.MAX_VALUE);
        Set<ConstraintViolation<AbstractEntity>> violations = validator.validate(entity);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing maxVersion bean validation")
    public void idTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();
        String id = "2OEVAEF1H0ITAO0A";

        // when
        entity.setId(id);

        // then
        assertEquals(id, entity.getId());
    }

    @Test
    @DisplayName("Testing creationDate")
    public void creationDateTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();
        Date creationDate = new Date();

        // when
        entity.setCreationDate(creationDate);
        entity.setCreatorUser("0");

        // then
        assertEquals(creationDate.getTime(), entity.getCreationDate().getTime());
    }

    @Test
    @DisplayName("Testing creationDate bean validation")
    public void creationDateBeanValidationTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when
        entity.setCreationDate(null);
        entity.setCreatorUser("0");
        Set<ConstraintViolation<AbstractIdentifiedAuditEntity>> violations = validator.validate(entity);

        // then
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Testing modificationDate")
    public void modificationDateTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();
        Date modificationDate = new Date();

        // when
        entity.setCreationDate(new Date());
        entity.setCreatorUser("0");
        entity.setModificationDate(modificationDate);

        // then
        assertEquals(modificationDate.getTime(), entity.getModificationDate().getTime());
    }

    @Test
    @DisplayName("Testing modificationDate bean validation")
    public void modificationDateBeanValidationTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when
        entity.setCreationDate(new Date());
        entity.setCreatorUser("0");
        entity.setModificationDate(null);
        Set<ConstraintViolation<AbstractIdentifiedAuditEntity>> violations = validator.validate(entity);

        // then
        assertTrue(violations.isEmpty());
        assertNull(entity.getModificationDate());
    }

    @Test
    @DisplayName("Testing creatorUser")
    public void creatorUserTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();
        String creatorUser = "2OEVAEF1H0ITAO0A";

        // when
        entity.setCreationDate(new Date());
        entity.setCreatorUser(creatorUser);

        // then
        assertEquals(creatorUser, entity.getCreatorUser());
    }

    @Test
    @DisplayName("Testing creatorUser null bean validation")
    public void creatorUserNullBeanValidationTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when
        entity.setCreationDate(new Date());
        entity.setCreatorUser(null);
        Set<ConstraintViolation<AbstractIdentifiedAuditEntity>> violations = validator.validate(entity);

        // then
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Testing modifierUser")
    public void modifierUserTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();
        String modifierUser = "2OEVAEF1H0ITAO0A";

        // when
        entity.setCreationDate(new Date());
        entity.setCreatorUser("0");
        entity.setModifierUser(modifierUser);

        // then
        assertEquals(modifierUser, entity.getModifierUser());
    }

    @Test
    @DisplayName("Testing modifierUser bean validation")
    public void modifierUserBeanValidationTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when
        entity.setCreationDate(new Date());
        entity.setCreatorUser("0");
        entity.setModifierUser(null);
        Set<ConstraintViolation<AbstractIdentifiedAuditEntity>> violations = validator.validate(entity);

        // then
        assertTrue(violations.isEmpty());
        assertNull(entity.getModifierUser());
    }

}
