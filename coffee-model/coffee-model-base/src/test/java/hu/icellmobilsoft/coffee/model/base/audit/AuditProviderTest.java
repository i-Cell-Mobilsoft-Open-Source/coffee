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
package hu.icellmobilsoft.coffee.model.base.audit;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.model.base.DefaultImplAbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.model.base.audit.AuditProvider;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing AuditProvider class
 *
 * @author arnold.bucher
 */
@EnableWeld
@Tag("weld")
@ExtendWith(MockitoExtension.class)
@DisplayName("Testing AuditProvider class")
public class AuditProviderTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(AuditProvider.class, BeanManager.class).activate(RequestScoped.class).build();

    @Inject
    private AuditProvider auditProvider;

    @Test
    @DisplayName("prePersist should throw Principal not found exception")
    public void prePersistPrincipalNotFoundTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when

        // then
        assertThrows(IllegalArgumentException.class, () -> auditProvider.prePersist(entity));
    }

    @Test
    @DisplayName("preUpdate should throw Principal not found exception")
    public void preUpdatePrincipalNotFoundTest() {
        // given
        DefaultImplAbstractIdentifiedAuditEntity entity = new DefaultImplAbstractIdentifiedAuditEntity();

        // when

        // then
        assertThrows(IllegalArgumentException.class, () -> auditProvider.preUpdate(entity));
    }

}
