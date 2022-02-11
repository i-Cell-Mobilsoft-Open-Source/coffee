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
package hu.icellmobilsoft.coffee.jpa.transaction;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityTransaction;

import org.apache.deltaspike.jpa.impl.transaction.BeanManagedUserTransactionStrategy;
import org.apache.deltaspike.jpa.impl.transaction.context.EntityManagerEntry;

import hu.icellmobilsoft.coffee.jpa.transaction.event.AfterAfterProceedEvent;
import hu.icellmobilsoft.coffee.jpa.transaction.event.AfterBeforeProceedEvent;

/**
 * Kulonbozo eseteknel event van dobva, ami jobban hajlithatova teszi a
 * tranzakciot. Lehet kulonbozo tarolt scripteket futtatni ha kell
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Dependent
@Alternative
public class EventThrowTransactionStrategy extends BeanManagedUserTransactionStrategy {

	private static final long serialVersionUID = 1L;

	/**
	 * CDI event fired when {@link BeanManagedUserTransactionStrategy#beforeProceed} is called
	 */
	@Inject
	private Event<AfterBeforeProceedEvent> afterBeforeProceedEvent;

	/**
	 * CDI event fired when {@link BeanManagedUserTransactionStrategy#afterProceed} is called
	 */
	@Inject
    private Event<AfterAfterProceedEvent> afterAfterProceedEvent;

	@Override
	protected void beforeProceed(InvocationContext invocationContext, EntityManagerEntry entityManagerEntry,
			EntityTransaction transaction) {
		super.beforeProceed(invocationContext, entityManagerEntry, transaction);
		afterBeforeProceedEvent.fire(new AfterBeforeProceedEvent(entityManagerEntry.getEntityManager()));
	}

	@Override
    protected void afterProceed(InvocationContext invocationContext, EntityManagerEntry entityManagerEntry, Exception exception) {
        super.afterProceed(invocationContext, entityManagerEntry, exception);
        afterAfterProceedEvent.fire(new AfterAfterProceedEvent(entityManagerEntry.getEntityManager()));
    }
}
