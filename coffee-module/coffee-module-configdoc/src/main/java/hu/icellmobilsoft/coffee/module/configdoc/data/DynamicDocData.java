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
package hu.icellmobilsoft.coffee.module.configdoc.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.configdoc.DynamicConfigDocs;

/**
 * Data class for the collected {@link DynamicConfigDocs} data, body will be determined by the template class
 *
 * @author mark.petrenyi
 * @since 1.10.0
 */
public class DynamicDocData {

    /**
     * DynamicDocData comparator. compares by title and template variables
     */
    public static Comparator<DynamicDocData> COMPARATOR = Comparator.comparing(DynamicDocData::getTitle)
            .thenComparing(DynamicDocData::getTemplateVariables, Arrays::compare);
    private String title;
    private String description;
    private String[] templateVariables;
    private String templateClassName;
    private String template;

    /**
     * Merges other into this. Only fields with not empty/not default values will be considered.
     *
     * @param other
     *            the other
     */
    public void merge(DynamicDocData other) {
        if (other == null) {
            return;
        }
        if (StringUtils.isNotBlank(other.getTitle()) && !StringUtils.equals(other.getTitle(), DynamicConfigDocs.NO_TITLE)) {
            this.setTitle(other.getTitle());
        }
        if (StringUtils.isNotBlank(other.getDescription()) && !StringUtils.equals(other.getDescription(), DynamicConfigDocs.NO_DESCRIPTION)) {
            this.setDescription(other.getDescription());
        }
        if (ArrayUtils.isNotEmpty(other.getTemplateVariables())) {
            this.setTemplateVariables(other.getTemplateVariables());
        }
        if (StringUtils.isNotBlank(other.getTemplateClassName())
                && !DynamicConfigDocs.NoTemplate.class.getCanonicalName().equals(other.getTemplateClassName())) {
            this.setTemplateClassName(other.getTemplateClassName());
        }
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title
     *            the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description
     *            the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get template variables string [ ].
     *
     * @return the string [ ]
     */
    public String[] getTemplateVariables() {
        return templateVariables;
    }

    /**
     * Sets template variables.
     *
     * @param templateVariables
     *            the template variables
     */
    public void setTemplateVariables(String[] templateVariables) {
        this.templateVariables = templateVariables;
    }

    /**
     * Gets template class name.
     *
     * @return the template class name
     */
    public String getTemplateClassName() {
        return templateClassName;
    }

    /**
     * Sets template class name.
     *
     * @param templateClassName
     *            the template class name
     */
    public void setTemplateClassName(String templateClassName) {
        this.templateClassName = templateClassName;
    }

    /**
     * Gets template.
     *
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets template.
     *
     * @param template
     *            the template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DynamicDocData data = (DynamicDocData) o;
        return Arrays.equals(templateVariables, data.templateVariables) && Objects.equals(title, data.title)
                && Objects.equals(description, data.description) && Objects.equals(templateClassName, data.templateClassName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(title, description, templateClassName);
        result = 31 * result + Arrays.hashCode(templateVariables);
        return result;
    }

    @Override
    public String toString() {
        return "DynamicDocData{" + "title='" + title + '\'' + ", description='" + description + '\'' + ", templateVariables="
                + Arrays.toString(templateVariables) + ", templateClassName='" + templateClassName + '\'' + '}';
    }
}
