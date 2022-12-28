/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.projectstage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;

@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("ProjectStageProducer tests")
@ExplicitParamInjection
class ProjectStageProducerTest {

    private static final String[] configs = new String[] { "coffee.app.projectStage", "org.apache.deltaspike.ProjectStage" };

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
            // add beans
            .addBeanClasses(ProjectStageProducer.class))
            // build
            .build();

    @BeforeEach
    public void cleanConfigs() {
        // Clean configs
        Arrays.stream(configs).forEach(element -> System.setProperty(element, ""));
    }

    @Test
    @DisplayName("default ProjectStage is Production test")
    void defaultProjectStage() {
        ProjectStage projectStage = CDI.current().select(ProjectStage.class).get();
        Assertions.assertTrue(projectStage.isProductionStage());
        Assertions.assertEquals(ProjectStageEnum.PRODUCTION, projectStage.getProjectStageEnum());
    }

    @Test
    @DisplayName("test")
    void testSameProjectStage() throws InterruptedException {
        int thread = 10000;
        ExecutorService service = Executors.newFixedThreadPool(thread);
        CountDownLatch latch = new CountDownLatch(thread);
        for (int i = 0; i < thread; i++) {
            service.submit(() -> {
                ProjectStage projectStage = CDI.current().select(ProjectStage.class).get();
                Assertions.assertTrue(projectStage.isProductionStage());
                latch.countDown();
            });
        }
        latch.await();
    }


    @Test
    @DisplayName("default ProjectStage is Production after mp.config.profile set other value test")
    void defaultLocaleAfterConfigChange() {
        ProjectStage projectStage = CDI.current().select(ProjectStage.class).get();
        Assertions.assertEquals(ProjectStageEnum.PRODUCTION, projectStage.getProjectStageEnum());
        System.setProperty(IConfigKey.COFFEE_APP_PROJECT_STAGE, "Test");
        projectStage = CDI.current().select(ProjectStage.class).get();
        Assertions.assertEquals(ProjectStageEnum.PRODUCTION, projectStage.getProjectStageEnum());
    }

    @DisplayName("default ProjectStage is Production test - projectStage config")
    @ParameterizedTest
    @MethodSource("methodForProjectStageTest")
    void projectStageTest(String config, String projectStageConfig, ProjectStageEnum projectStageEnum) {
        System.setProperty(config, projectStageConfig);
        ProjectStage projectStage = CDI.current().select(ProjectStage.class).get();
        Assertions.assertEquals(projectStage.getProjectStageEnum(), projectStageEnum);
    }

    private static Stream<Arguments> methodForProjectStageTest() {
        List<Arguments> argumentsList = new ArrayList<>();
        for (String config : configs) {
            for (ProjectStageEnum projectStageEnum : ProjectStageEnum.values()) {
                for (String alternativeName : projectStageEnum.getAlternativeNames()) {
                    argumentsList.add(Arguments.of(config, alternativeName, projectStageEnum));
                    argumentsList.add(Arguments.of(config, alternativeName.toLowerCase(), projectStageEnum));
                    argumentsList.add(Arguments.of(config, alternativeName.toUpperCase(), projectStageEnum));
                }
            }
            argumentsList.add(Arguments.of(config, RandomStringUtils.random(5), ProjectStageEnum.PRODUCTION));
        }
        return argumentsList.stream();
    }
}
