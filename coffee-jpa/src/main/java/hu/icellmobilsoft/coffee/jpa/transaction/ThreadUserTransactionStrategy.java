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

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.apache.deltaspike.jpa.impl.transaction.BeanManagedUserTransactionStrategy;
import org.hibernate.engine.transaction.jta.platform.internal.JBossAppServerJtaPlatform;

/**
 * Ez akkor kell amikor egy threadban akarjuk hasznalni a @Transactional
 * annotaciot
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Dependent
@Alternative
public class ThreadUserTransactionStrategy extends BeanManagedUserTransactionStrategy {

	private static final long serialVersionUID = 1L;

	// @Resource hasznalata lenne a logikus megoldas de ennek koszonhetoen szal
	// el, ezert az @Inject van hasznalva
	@Inject
	private UserTransaction userTransaction;


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.deltaspike.jpa.impl.transaction.BeanManagedUserTransactionStrategy
	 * #resolveUserTransaction()
	 */
	/** {@inheritDoc} */
	@Override
	protected UserTransaction resolveUserTransaction() {
		if (userTransaction != null) {
			return userTransaction;
		}
		InitialContext context = null;
		try {
			context = new InitialContext();
			// "java:comp/UserTransaction"
			UserTransaction returnUserTransaction = lookup(context, JBossAppServerJtaPlatform.UT_NAME);
			if (returnUserTransaction == null) {
				// "java:jboss/UserTransaction"
				returnUserTransaction = lookup(context, JBossAppServerJtaPlatform.JBOSS_UT_NAME);
			}
			if (returnUserTransaction == null) {
				// "UserTransaction" allitolag valami extra esetekben elofordul
				// hogy csak ez van
				returnUserTransaction = lookup(context, "UserTransaction");
			}
			if (returnUserTransaction != null) {
				return returnUserTransaction;
			}
		} catch (NamingException ne) {
			Logger.getLogger(ThreadUserTransactionStrategy.class.getName()).warning(
					"Creating InitialContext failed, message: " + ne.getLocalizedMessage());
		}

		return super.resolveUserTransaction();
	}

	private UserTransaction lookup(InitialContext context, String name) {
		try {
			UserTransaction returnUserTransaction = (javax.transaction.UserTransaction) context.lookup(name);
			return returnUserTransaction;
		} catch (NamingException ne) {
			Logger.getLogger(ThreadUserTransactionStrategy.class.getName()).warning(
					"UserTransaction for [" + name + "] not found, message: " + ne.getLocalizedMessage());
		}
		return null;
	}
}
