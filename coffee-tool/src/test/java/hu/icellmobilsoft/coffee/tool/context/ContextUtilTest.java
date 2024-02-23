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
package hu.icellmobilsoft.coffee.tool.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.exception.TechnicalException;
import hu.icellmobilsoft.coffee.tool.context.ContextUtil;

/**
 * @author mark.petrenyi
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("Testing ContextUtil")
class ContextUtilTest {

    @Mock
    private InitialContext initialContext;
    @Mock
    private InitialContextFactory initialContextFactory;

    private static InitialContextFactoryBuilder initialContextFactoryBuilder;

    static class SomeResource {
        private String property;

        public SomeResource(String property) {
            this.property = property;
        }
    }

    private static final SomeResource TEST_RESOURCE = new SomeResource("test");

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        initialContextFactoryBuilder = Mockito.mock(InitialContextFactoryBuilder.class);
        NamingManager.setInitialContextFactoryBuilder(initialContextFactoryBuilder);
    }

    void givenWeHaveInitialContext() throws Exception {
        Mockito.when(initialContextFactoryBuilder.createInitialContextFactory(ArgumentMatchers.any())).thenReturn(initialContextFactory);
        Mockito.when(initialContextFactory.getInitialContext(ArgumentMatchers.any())).thenReturn(initialContext);
    }

    @Nested
    @DisplayName("Test cases for doLookup() method")
    class DoLookUpTest {
        @Test
        @DisplayName("Testing doLookup() if the given jndi exists")
        void jndiExists() throws Exception {
            // given
            givenWeHaveInitialContext();
            SomeResource expected = TEST_RESOURCE;
            String testJndi = "testjndi";
            Mockito.when(initialContext.lookup(testJndi)).thenReturn(TEST_RESOURCE);

            // when
            SomeResource some = ContextUtil.doLookup(testJndi, SomeResource.class);

            // then
            assertSame(expected, some);
        }

        @Test
        @DisplayName("Testing doLookup() if the given jndi does not exist")
        void jndiDoesntExists() throws Exception {
            // given
            givenWeHaveInitialContext();
            SomeResource expected = null;
            String testJndi = "nonExistingJNDI";

            // when
            SomeResource some = ContextUtil.doLookup(testJndi, SomeResource.class);

            // then
            assertSame(expected, some);
        }

        @Test
        @DisplayName("Testing doLookup() if NamingException is thrown")
        void namingExceptionIsThrown() throws Exception {
            // given
            givenWeHaveInitialContext();
            String testJndi = "testjndi";
            Enum<?> expectedFault = CoffeeFaultType.INVALID_REQUEST;
            Mockito.when(initialContext.lookup(testJndi)).thenThrow(new NamingException());

            // when
            TechnicalException exception = assertThrows(TechnicalException.class, () -> ContextUtil.doLookup(testJndi, SomeResource.class));

            // then
            assertEquals(expectedFault, exception.getFaultTypeEnum());
        }

        @Test
        @DisplayName("Testing doLookup() if input JNDI is null")
        void inputStringIsNull() throws Exception {
            // given
            SomeResource expected = null;
            String testJndi = null;

            // when
            SomeResource some = ContextUtil.doLookup(testJndi, SomeResource.class);

            // then
            assertSame(expected, some);
        }

        @Test
        @DisplayName("Testing doLookup() if input clazz is null")
        void inputClazzIsNull() throws Exception {
            // given
            String testJndi = "testjndi";
            Enum<?> expectedFault = CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS;

            // when
            TechnicalException exception = assertThrows(TechnicalException.class, () -> ContextUtil.doLookup(testJndi, null));

            // then
            assertEquals(expectedFault, exception.getFaultTypeEnum());
        }

    }

}
