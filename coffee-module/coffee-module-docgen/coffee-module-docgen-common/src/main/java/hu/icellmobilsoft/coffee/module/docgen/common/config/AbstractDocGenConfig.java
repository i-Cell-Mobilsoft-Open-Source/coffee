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
package hu.icellmobilsoft.coffee.module.docgen.common.config;

import java.util.Map;

/**
 * Abstract configuration class for generating documentation
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public abstract class AbstractDocGenConfig {

    private static final String OUTPUT_DIR_KEY = "outputDir";
    private static final String OUTPUT_FILE_NAME_KEY = "outputFileName";
    private static final String OUTPUT_TO_CLASS_PATH_KEY = "outputToClassPath";

    private final String outputDir;
    private final String outputFileName;
    private final boolean outputToClassPath;

    /**
     * Creates the abstract config based on properties
     * 
     * @param properties
     *            the map which contains the config properties
     */
    protected AbstractDocGenConfig(Map<String, String> properties) {
        outputDir = properties.getOrDefault(getOutputDirKey(), getDefaultOutputPath());
        outputFileName = properties.getOrDefault(getOutputFileNameKey(), getDefaultOutputFileName());
        outputToClassPath = Boolean.parseBoolean(properties.getOrDefault(getOutputToClassPathKey(), Boolean.TRUE.toString()));
    }

    private String getOutputDirKey() {
        return getConfigPrefix() + OUTPUT_DIR_KEY;
    }

    private String getOutputFileNameKey() {
        return getConfigPrefix() + OUTPUT_FILE_NAME_KEY;
    }

    private String getOutputToClassPathKey() {
        return getConfigPrefix() + OUTPUT_TO_CLASS_PATH_KEY;
    }

    /**
     * Returns the config key prefix
     * 
     * @return the config key prefix
     */
    protected abstract String getConfigPrefix();

    /**
     * Returns the default output path
     * 
     * @return the default output path
     */
    protected abstract String getDefaultOutputPath();

    /**
     * Returns the default output file name
     * 
     * @return the default output file name
     */
    protected abstract String getDefaultOutputFileName();

    /**
     * Returns the directory for the generated file
     *
     * @return the directory for the generated file
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Returns the generated file name
     *
     * @return the generated file name
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * Returns {@literal true} if the output folder should be on the classpath
     *
     * @return {@literal true} if the output folder should be on the classpath
     */
    public boolean isOutputToClassPath() {
        return outputToClassPath;
    }
}
