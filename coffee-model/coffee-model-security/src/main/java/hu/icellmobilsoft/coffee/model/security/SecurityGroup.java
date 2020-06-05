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
package hu.icellmobilsoft.coffee.model.security;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * <p>SecurityGroup class.</p>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "security_group")
public class SecurityGroup extends AbstractIdentifiedAuditEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "description", length = 255)
	@Size(max = 255)
	private String description;

	@Column(name = "deletable", nullable = false)
	private boolean deletable;

	@Column(name = "name", nullable = false, length = 100)
	@Size(max = 100)
	@NotNull
	private String name;

	/**
	 * <p>Getter for the field <code>description</code>.</p>
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <p>Setter for the field <code>description</code>.</p>
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * <p>isDeletable.</p>
	 */
	public boolean isDeletable() {
		return deletable;
	}

	/**
	 * <p>Setter for the field <code>deletable</code>.</p>
	 */
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 */
	public void setName(String name) {
		this.name = name;
	}
}
