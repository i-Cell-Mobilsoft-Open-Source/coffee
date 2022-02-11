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
package hu.icellmobilsoft.coffee.module.document.model;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * template parameter table for document
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "template_parameter")
public class TemplateParameter extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * template key
     */
    @Column(name = "template_key", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String templateKey;

    /**
     * template language
     */
    @Column(name = "language", nullable = false, length = 5)
    @NotNull
    @Size(max = 5)
    private String language;

    /**
     * parameter key
     */
    @Column(name = "parameter_key", nullable = false)
    @Size(max = 128)
    private String parameterKey;

    /**
     * template parameter default value
     */
    @Column(name = "default_value")
    @Size(max = 255)
    private String defaultValue;

    /**
     * Getter for the field {@code templateKey}.
     *
     * @return templateKey
     */
    public String getTemplateKey() {
        return templateKey;
    }

    /**
     * Setter for the field {@code templateKey}.
     *
     * @param templateKey
     *            templateKey to set
     */
    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    /**
     * Getter for the field {@code language}.
     *
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Setter for the field {@code language}.
     *
     * @param language
     *            language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Getter for the field {@code parameterKey}.
     *
     * @return parameterKey
     */
    public String getParameterKey() {
        return parameterKey;
    }

    /**
     * Setter for the field {@code parameterKey}.
     *
     * @param parameterKey
     *            parameterKey to set
     */
    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    /**
     * Getter for the field {@code defaultValue}.
     *
     * @return defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Setter for the field {@code defaultValue}.
     * 
     * @param defaultValue
     *            defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
