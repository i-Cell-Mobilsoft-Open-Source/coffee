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
package hu.icellmobilsoft.coffee.jpa.transaction.event;

import javax.persistence.EntityManager;

/**
 * <p>AfterAfterProceedEvent class.</p>
 *
 * @since 1.0.0
 */
public class AfterAfterProceedEvent {

	private EntityManager entityManager;

    /**
     * <p>
     * Constructor for AfterAfterProceedEvent.
     * </p>
     * 
     * @param entityManager
     *            entity manager
     */
	public AfterAfterProceedEvent(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

    /**
     * <p>
     * Getter for the field <code>entityManager</code>.
     * </p>
     * 
     * @return entity manager
     */
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
