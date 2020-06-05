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

    @Column(name = "template_key", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String templateKey;

    @Column(name = "data_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateDataType dataType;

    @Column(name = "language", nullable = false, length = 5)
    @NotNull
    @Size(max = 5)
    private String language;

    @Column(name = "subject", length = 255)
    @Size(max = 255)
    private String subject;

    @Column(name = "data")
    private String data;

    @Column(name = "default_filename", length = 255)
    @Size(max = 255)
    private String defaultFilename;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_from", nullable = false)
    @NotNull
    private Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_to")
    private Date validTo;

    /**
     * <p>Getter for the field <code>templateKey</code>.</p>
     */
    public String getTemplateKey() {
        return templateKey;
    }

    /**
     * <p>Setter for the field <code>templateKey</code>.</p>
     */
    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }

    /**
     * <p>Getter for the field <code>dataType</code>.</p>
     */
    public TemplateDataType getDataType() {
        return dataType;
    }

    /**
     * <p>Setter for the field <code>dataType</code>.</p>
     */
    public void setDataType(TemplateDataType dataType) {
        this.dataType = dataType;
    }

    /**
     * <p>Getter for the field <code>language</code>.</p>
     */
    public String getLanguage() {
        return language;
    }

    /**
     * <p>Setter for the field <code>language</code>.</p>
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * <p>Getter for the field <code>subject</code>.</p>
     */
    public String getSubject() {
        return subject;
    }

    /**
     * <p>Setter for the field <code>subject</code>.</p>
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * <p>Getter for the field <code>data</code>.</p>
     */
    public String getData() {
        return data;
    }

    /**
     * <p>Setter for the field <code>data</code>.</p>
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * <p>Getter for the field <code>defaultFilename</code>.</p>
     */
    public String getDefaultFilename() {
        return defaultFilename;
    }

    /**
     * <p>Setter for the field <code>defaultFilename</code>.</p>
     */
    public void setDefaultFilename(String defaultFilename) {
        this.defaultFilename = defaultFilename;
    }

    /**
     * <p>Getter for the field <code>validFrom</code>.</p>
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * <p>Setter for the field <code>validFrom</code>.</p>
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * <p>Getter for the field <code>validTo</code>.</p>
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * <p>Setter for the field <code>validTo</code>.</p>
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
}
