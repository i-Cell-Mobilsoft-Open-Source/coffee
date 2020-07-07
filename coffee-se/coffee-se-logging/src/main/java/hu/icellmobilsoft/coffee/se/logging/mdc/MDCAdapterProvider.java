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
package hu.icellmobilsoft.coffee.se.logging.mdc;

/**
 * Provider interface for defining custom MDCAdapters.<br>
 *
 * Implementations can be activated via service loader mechanism.<br>
 * ie.: placing the fully qualified name of the implementing class into
 * {@code META-INF/services/hu.icellmobilsoft.coffee.cdi.logger.mdc.MDCAdapterProvider} file
 * 
 * @author mark.petrenyi
 * @since 1.1.0
 */
public interface MDCAdapterProvider {

    /**
     * Provide and initializes MDCAdapter instance. Note that Exception can be thrown.
     * 
     * @return MDCAdapter instance
     * @throws Exception
     *             if could not initialize MDCAdapter; if thrown another provider will be tried.
     */
    public MDCAdapter getAdapter() throws Exception;

}
