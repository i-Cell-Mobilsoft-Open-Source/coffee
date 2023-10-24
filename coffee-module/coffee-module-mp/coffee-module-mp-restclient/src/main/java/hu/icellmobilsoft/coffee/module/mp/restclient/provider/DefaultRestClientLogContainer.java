/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.mp.restclient.provider;

import jakarta.enterprise.context.RequestScoped;

/**
 * Log container to concatenate the filter and interceptor logs.
 * 
 * @author czenczl
 * @since 2.3.0
 */
@RequestScoped
public class DefaultRestClientLogContainer {

    private StringBuilder logBuilder;

    /**
     * Gets the logBuilder
     * 
     * @return the string builder
     */
    public StringBuilder getLogBuilder() {
        return logBuilder;
    }

    /**
     * Sets the logbuilder
     * 
     * @param logBuilder
     *            to store logs
     */
    public void setLogBuilder(StringBuilder logBuilder) {
        this.logBuilder = logBuilder;
    }

}
