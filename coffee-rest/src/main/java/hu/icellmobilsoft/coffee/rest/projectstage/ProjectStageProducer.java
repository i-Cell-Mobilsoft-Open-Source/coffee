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

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;
import hu.icellmobilsoft.coffee.tool.utils.config.ConfigUtil;

/**
 * <p>
 * Produces {@link ProjectStage} configurations.
 * </p>
 *
 * <p>
 * The producer will try to detect the currently active ProjectStage on startup and use that for all generated fields.
 * </p>
 * <p>
 * Usage:
 * </p>
 * Simply inject the current ProjectStage into any bean:
 * 
 * <pre>
 * public class MyBean {
 *     private @Inject ProjectStage projectStage;
 *
 *     public void fn() {
 *         if (projectStage.isProductionStage()) {
 *             // do some production stuff...
 *         }
 *     }
 * }
 * </pre>
 *
 * @author speter555
 * @since 1.13.0
 */
@ApplicationScoped
public class ProjectStageProducer {

    /**
     * These config keys will get used to detect the ProjectStage. We iterate through them until we find the first non-empty value.
     */
    protected static final String[] CONFIG_SETTING_KEYS = { IConfigKey.COFFEE_APP_PROJECT_STAGE, "org.apache.deltaspike.ProjectStage" };

    /**
     * Logging
     */
    protected static final Logger LOG = Logger.getLogger(ProjectStageProducer.class.getName());

    /**
     * The detected ProjectStage
     */
    private ProjectStage projectStage;

    /**
     * We can only produce @Dependent scopes since an enum is final.
     * 
     * @return current ProjectStage
     */
    @Produces
    @ApplicationScoped
    public ProjectStage getProjectStage() {
        if (projectStage == null) {
            initProjectStage();
        }
        return projectStage;
    }

    /**
     * Resolves the project-stage. Read configurations from config sources and create ProjectStage from first found config, otherwise create Project
     */
    private void initProjectStage() {
        Config config = ConfigUtil.getInstance().defaultConfig();
        Iterator<String> iterator = Arrays.stream(CONFIG_SETTING_KEYS).iterator();
        while (projectStage == null && iterator.hasNext()) {
            String projectStageConfig = iterator.next();
            String stageName = config.getOptionalValue(projectStageConfig, String.class).orElse(null);
            if (StringUtils.isNotBlank(stageName)) {
                projectStage = new ProjectStage(stageName);
            }
        }
        if (projectStage == null) {
            projectStage = new ProjectStage();
        }
        LOG.info("Computed the following ProjectStage: " + projectStage.getProjectStageEnum());
    }

}
