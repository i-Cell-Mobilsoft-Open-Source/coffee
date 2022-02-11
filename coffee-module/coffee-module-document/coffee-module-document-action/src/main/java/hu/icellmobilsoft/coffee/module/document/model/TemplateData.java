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

import java.util.Date;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.module.document.model.enums.TemplateDataType;

/**
 * template table for document
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "template_data")
public class TemplateData extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * template key
     */
    @Column(name = "template_key", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String templateKey;

    /**
     * template format
     */
    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateDataType dataType;

    /**
     * template language
     */
    @Column(name = "language", nullable = false, length = 5)
    @NotNull
    @Size(max = 5)
    private String language;

    /**
     * template subject
     */
    @Column(name = "subject", length = 255)
    @Size(max = 255)
    private String subject;

    /**
     * template data
     */
    @Column(name = "data")
    private String data;

    /**
     * default file name
     */
    @Column(name = "default_filename", length = 255)
    @Size(max = 255)
    private String defaultFilename;

    /**
     * template valid from
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_from", nullable = false)
    @NotNull
    private Date validFrom;

    /**
     * template valid to
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_to")
    private Date validTo;

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
     * Getter for the field {@code dataType}.
     *
     * @return dataType
     */
    public TemplateDataType getDataType() {
        return dataType;
    }

    /**
     * Setter for the field {@code dataType}.
     *
     * @param dataType
     *            dataType to set
     */
    public void setDataType(TemplateDataType dataType) {
        this.dataType = dataType;
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
     * Getter for the field {@code subject}.
     *
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Setter for the field {@code subject}.
     *
     * @param subject
     *            subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Getter for the field {@code data}.
     *
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * Setter for the field {@code data}.
     *
     * @param data
     *            data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Getter for the field {@code defaultFilename}.
     *
     * @return defaultFilename
     */
    public String getDefaultFilename() {
        return defaultFilename;
    }

    /**
     * Setter for the field {@code defaultFilename}.
     *
     * @param defaultFilename
     *            defaultFilename to set
     */
    public void setDefaultFilename(String defaultFilename) {
        this.defaultFilename = defaultFilename;
    }

    /**
     * Getter for the field {@code validFrom}.
     *
     * @return validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * Setter for the field {@code validFrom}.
     *
     * @param validFrom
     *            validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Getter for the field {@code validTo}.
     *
     * @return validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Setter for the field {@code validTo}.
     *
     * @param validTo
     *            validTo to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
}
