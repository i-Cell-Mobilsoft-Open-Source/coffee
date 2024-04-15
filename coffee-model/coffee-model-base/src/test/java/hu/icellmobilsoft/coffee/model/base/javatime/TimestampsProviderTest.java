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
package hu.icellmobilsoft.coffee.model.base.javatime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.model.base.DefaultImplAbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.model.base.GetterAnnotatedEntity;
import hu.icellmobilsoft.coffee.model.base.javatime.listener.TimestampsProvider;

/**
 * Testing TimestampsProvider class
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
@DisplayName("Testing TimestampsProvider class")
class TimestampsProviderTest {

    private final String TIMEZONE_ID_PROP = "coffee.model.base.java.time.timezone.id";

    @Test
    @DisplayName("prePersist test")
    void testPrePersist() {
        // given
        TimestampsProvider timestampsProvider = new TimestampsProvider();
        DefaultImplAbstractIdentifiedAuditEntity testEntity = new DefaultImplAbstractIdentifiedAuditEntity();
        // when
        timestampsProvider.prePersist(testEntity);
        // then
        assertNotNull(testEntity.getCreationDate());
        assertNull(testEntity.getModificationDate());
    }

    @Test
    @DisplayName("preUpdate test")
    void testPreUpdate() {
        // given
        TimestampsProvider timestampsProvider = new TimestampsProvider();
        DefaultImplAbstractIdentifiedAuditEntity testEntity = new DefaultImplAbstractIdentifiedAuditEntity();
        // when
        timestampsProvider.preUpdate(testEntity);
        // then
        assertNull(testEntity.getCreationDate());
        assertNotNull(testEntity.getModificationDate());
    }

    @Test
    @DisplayName("getter annotated prePersist test")
    void testGetterAnnotatedPrePersist() {
        // given
        TimestampsProvider timestampsProvider = new TimestampsProvider();
        GetterAnnotatedEntity testEntity = new GetterAnnotatedEntity();
        // when
        timestampsProvider.prePersist(testEntity);
        // then
        assertNotNull(testEntity.getCreationDate());
        assertNull(testEntity.getModificationDate());
    }

    @Test
    @DisplayName("getter annotated preUpdate test")
    void testGetterAnnotatedPreUpdate() {
        // given
        TimestampsProvider timestampsProvider = new TimestampsProvider();
        GetterAnnotatedEntity testEntity = new GetterAnnotatedEntity();
        // when
        timestampsProvider.preUpdate(testEntity);
        // then
        assertNull(testEntity.getCreationDate());
        assertNotNull(testEntity.getModificationDate());
    }

    @Test
    @DisplayName("zoneId through system property test")
    void testTimeZoneProperty() {
        // given
        System.setProperty(TIMEZONE_ID_PROP,"America/New_York");
        TimestampsProvider timestampsProvider = new TimestampsProvider();
        GetterAnnotatedEntity testEntity = new GetterAnnotatedEntity();
        // when
        timestampsProvider.prePersist(testEntity);
        // then
        assertNotNull(testEntity.getCreationDate());
        assertNull(testEntity.getModificationDate());
        Assertions.assertEquals(ZoneId.of("America/New_York").getRules().getOffset(Instant.now()),testEntity.getCreationDate().toZonedDateTime().getZone());
    }


}
