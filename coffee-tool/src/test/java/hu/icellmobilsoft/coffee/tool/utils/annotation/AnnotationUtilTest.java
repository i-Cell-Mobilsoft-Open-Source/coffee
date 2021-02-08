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
package hu.icellmobilsoft.coffee.tool.utils.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.AppLoggerImpl;
import hu.icellmobilsoft.coffee.cdi.logger.DefaultAppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.LogContainer;
import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.coffee.tool.utils.annotation.bean.TestProducer;
import hu.icellmobilsoft.coffee.tool.utils.annotation.bean.TestQualifier;

/**
 * AnnotationUtil test class
 * 
 * @author imre.scheffer
 *
 */
@EnableWeld
@DisplayName("Testing AnnotationUtil")
@Tag("weld")
public class AnnotationUtilTest {

    /** Constant <code>TEST_STRING_FOR_SELECT = "Value for inject"</code> */
    public static final String TEST_STRING_FOR_INJECT = "Value for inject";
    /** Constant <code>TEST_STRING_FOR_SELECT = "Value for select"</code> */
    public static final String TEST_STRING_FOR_SELECT = "Value for select";

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(LogProducer.class, AppLoggerImpl.class, LogContainer.class, TestProducer.class)
            .activate(RequestScoped.class).build();

    @Inject
    @ThisLogger
    private AppLogger appLogger;

    @Inject
    private LogContainer logContainer;

    @Inject
    @TestQualifier(testString = TEST_STRING_FOR_INJECT)
    private String testString;

    @Test
    @DisplayName("Testing getAnnotation(Class, Class) from non proxy class")
    void getNonProxyAnnotationTest() {
        // given
        // when
        assertFalse(appLogger.getClass().isSynthetic(), "Injected appLogger class is proxy (synthetic)");
        DefaultAppLogger annotation = AnnotationUtil.getAnnotation(appLogger.getClass(), DefaultAppLogger.class);
        // then
        assertNotNull(annotation, "applogger must have DefaultAppLogger annotation");
    }

    @Test
    @DisplayName("Testing getAnnotation(Class, Class) from proxy class")
    void getProxyAnnotationTest() {
        // given
        // when
        assertTrue(logContainer.getClass().isSynthetic(), "Injected logContainer class is not proxy (synthetic)");
        Model annotation = AnnotationUtil.getAnnotation(logContainer.getClass(), Model.class);
        // then
        assertNotNull(annotation, "logContainer must have DefaultAppLogger annotation");
    }

    @Test
    @DisplayName("Testing getAnnotation(InjectionPoint, Class) from @Inject")
    void getInjectAnnotationTest() {
        // given
        // when
        // then
        assertEquals(TEST_STRING_FOR_INJECT, testString);
    }

    @Test
    @DisplayName("Testing getAnnotation(InjectionPoint, Class) from CDI.select")
    void getSelectAnnotationTest() {
        // given
        TestQualifier.Literal literalQualifier = new TestQualifier.Literal(TEST_STRING_FOR_SELECT);
        // when
        String selectString = CDI.current().select(String.class, literalQualifier).get();
        // then
        assertEquals(TEST_STRING_FOR_SELECT, selectString);
    }
}
