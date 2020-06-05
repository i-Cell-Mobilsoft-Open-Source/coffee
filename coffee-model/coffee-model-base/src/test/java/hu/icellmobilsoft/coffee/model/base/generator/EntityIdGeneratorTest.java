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
package hu.icellmobilsoft.coffee.model.base.generator;

import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.model.base.DefaultImplAbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.model.base.generator.EntityIdGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testing EntityIdGenerator class
 *
 * @author arnold.bucher
 */
@DisplayName("Testing EntityIdGenerator class")
public class EntityIdGeneratorTest {

    @Test
    @DisplayName("Testing generateId size")
    public void generateIdTest() {
        // given

        // when

        // then
        assertNotNull(EntityIdGenerator.generateId());
    }

    @Test
    @DisplayName("Testing generateId length")
    public void generateIdLengthTest() {
        // given

        // when

        // then
        assertEquals(16, EntityIdGenerator.generateId().length());
    }

    @Test
    @DisplayName("Testing direct identifierGenerator")
    public void identifierGeneratorTest() {
        // given
        EntityIdGenerator entityIdGenerator = new EntityIdGenerator();

        // when
        Serializable generatedId = entityIdGenerator.generate(null, null);

        // then
        assertNotNull(generatedId);
    }

    @Test
    @DisplayName("Testing direct identifierGenerator with AbstractIdentifiedAuditEntity")
    public void identifierGeneratorWithEntityTest() {
        // given
        EntityIdGenerator entityIdGenerator = new EntityIdGenerator();
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when
        Serializable generatedId = entityIdGenerator.generate(null, entity);

        // then
        assertNotNull(generatedId);
    }

    @Test
    @DisplayName("Testing direct identifierGenerator with AbstractIdentifiedAuditEntity")
    public void identifierGeneratorWithEntityIdTest() {
        // given
        EntityIdGenerator entityIdGenerator = new EntityIdGenerator();
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();
        entity.setId("123");

        // when
        Serializable generatedId = entityIdGenerator.generate(null, entity);

        // then
        assertEquals(entity.getId(), generatedId);
    }
}
