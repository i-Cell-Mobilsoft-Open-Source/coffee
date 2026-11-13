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
package hu.icellmobilsoft.coffee.rest.action.evict;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.enterprise.inject.Instance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.configuration.ApplicationConfiguration;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.dto.common.config.evict.EvictResponse;
import hu.icellmobilsoft.coffee.dto.evict.Evictable;

/**
 * Verifies that the default eviction action runs and clears configuration.
 *
 * @author balazs.bakos
 * @since 4.0.0
 */
@ExtendWith(MockitoExtension.class)
class EvictActionTest {

    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @Mock
    private Instance<Evictable> evictables;

    @InjectMocks
    private EvictAction evictAction;

    @Test
    void evictClearsConfiguration() {
        when(evictables.isUnsatisfied()).thenReturn(true);

        EvictResponse response = evictAction.evict();

        verify(applicationConfiguration).clear();
        assertEquals(FunctionCodeType.OK, response.getFuncCode());
    }
}
