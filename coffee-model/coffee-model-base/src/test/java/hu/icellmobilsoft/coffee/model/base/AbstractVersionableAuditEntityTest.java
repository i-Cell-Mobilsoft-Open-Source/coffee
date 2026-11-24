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

import java.time.OffsetDateTime;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testing AbstractVersionableAuditEntity class
 *
 * @author arnold.bucher
 */
@DisplayName("Testing AbstractVersionableAuditEntity class")
public class AbstractVersionableAuditEntityTest {

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
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when

        // then
        Assertions.assertEquals(0, entity.getVersion());
    }

    @Test
    @DisplayName("Testing version")
    public void versionTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setVersion(1L);
        entity.setVersion(2L);

        // then
        Assertions.assertEquals(2, entity.getVersion());
    }

    @Test
    @DisplayName("Testing version")
    public void duplicateTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setVersion(1L);
        entity.setVersion(1L);

        // then
        Assertions.assertEquals(1, entity.getVersion());
    }

    @Test
    @DisplayName("Testing rollbackVersion")
    public void rollbackVersionTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.rollbackVersion();
        entity.setVersion(1L);
        entity.rollbackVersion();

        // then
        Assertions.assertEquals(0, entity.getVersion());
    }

    @Test
    @DisplayName("Testing maxVersion")
    public void maxVersionTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setVersion(Long.MAX_VALUE);

        // then
        Assertions.assertEquals(Long.MAX_VALUE, entity.getVersion());
    }

    @Test
    @DisplayName("Testing maxVersion bean validation")
    public void maxVersionBeanValidationTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setCreationDate(OffsetDateTime.now());
        entity.setCreatorUser("0");
        entity.setVersion(Long.MAX_VALUE);
        Set<ConstraintViolation<AbstractEntity>> violations = validator.validate(entity);

        // then
        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing maxVersion bean validation")
    public void idTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();
        String id = "2OEVAEF1H0ITAO0A";

        // when
        entity.setId(id);

        // then
        Assertions.assertEquals(id, entity.getId());
    }

    @Test
    @DisplayName("Testing creationDate")
    public void creationDateTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();
        OffsetDateTime creationDate = OffsetDateTime.now();

        // when
        entity.setCreationDate(creationDate);
        entity.setCreatorUser("0");

        // then
        Assertions.assertEquals(creationDate, entity.getCreationDate());
    }

    @Test
    @DisplayName("Testing creationDate bean validation")
    public void creationDateBeanValidationTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setCreationDate(null);
        entity.setCreatorUser("0");
        Set<ConstraintViolation<AbstractVersionableAuditEntity<String>>> violations = validator.validate(entity);

        // then
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Testing modificationDate")
    public void modificationDateTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();
        OffsetDateTime modificationDate = OffsetDateTime.now();

        // when
        entity.setCreationDate(OffsetDateTime.now());
        entity.setCreatorUser("0");
        entity.setModificationDate(modificationDate);

        // then
        Assertions.assertEquals(modificationDate, entity.getModificationDate());
    }

    @Test
    @DisplayName("Testing modificationDate bean validation")
    public void modificationDateBeanValidationTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setCreationDate(OffsetDateTime.now());
        entity.setCreatorUser("0");
        entity.setModificationDate(null);
        Set<ConstraintViolation<AbstractVersionableAuditEntity<String>>> violations = validator.validate(entity);

        // then
        Assertions.assertTrue(violations.isEmpty());
        Assertions.assertNull(entity.getModificationDate());
    }

    @Test
    @DisplayName("Testing creatorUser")
    public void creatorUserTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();
        String creatorUser = "2OEVAEF1H0ITAO0A";

        // when
        entity.setCreationDate(OffsetDateTime.now());
        entity.setCreatorUser(creatorUser);

        // then
        Assertions.assertEquals(creatorUser, entity.getCreatorUser());
    }

    @Test
    @DisplayName("Testing creatorUser null bean validation")
    public void creatorUserNullBeanValidationTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setCreationDate(OffsetDateTime.now());
        entity.setCreatorUser(null);
        Set<ConstraintViolation<AbstractVersionableAuditEntity<String>>> violations = validator.validate(entity);

        // then
        Assertions.assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Testing modifierUser")
    public void modifierUserTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();
        String modifierUser = "2OEVAEF1H0ITAO0A";

        // when
        entity.setCreationDate(OffsetDateTime.now());
        entity.setCreatorUser("0");
        entity.setModifierUser(modifierUser);

        // then
        Assertions.assertEquals(modifierUser, entity.getModifierUser());
    }

    @Test
    @DisplayName("Testing modifierUser bean validation")
    public void modifierUserBeanValidationTest() {
        // given
        DefaultImplAbstractVersionableAuditEntity entity = new DefaultImplAbstractVersionableAuditEntity();

        // when
        entity.setCreationDate(OffsetDateTime.now());
        entity.setCreatorUser("0");
        entity.setModifierUser(null);
        Set<ConstraintViolation<AbstractVersionableAuditEntity<String>>> violations = validator.validate(entity);

        // then
        Assertions.assertTrue(violations.isEmpty());
        Assertions.assertNull(entity.getModifierUser());
    }

}
