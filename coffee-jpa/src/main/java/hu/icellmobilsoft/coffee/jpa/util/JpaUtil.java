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
package hu.icellmobilsoft.coffee.jpa.util;

import java.lang.reflect.Method;

import javax.enterprise.inject.Vetoed;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.internal.QueryImpl;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * JPA common functions.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class JpaUtil {

    /**
     * Get native SQL from created Query, exception is logged only
     *
     * @param em
     *            entity manager
     * @param query
     *            created query
     * @return native SQL of query or null
     */
    public static String toNativeSQLNoEx(EntityManager em, Query query) {
        try {
            return toNativeSQL(em, query);
        } catch (BaseException e) {
            Logger.getLogger(JpaUtil.class).warn("Exception on converting Query to native SQL!", e);
        }
        return null;
    }

    /**
     * Get native SQL from created Query
     *
     * @param em
     *            entity manager
     * @param query
     *            created query
     * @return native SQL of query
     * @throws BaseException
     *             exception
     */
    public static String toNativeSQL(EntityManager em, Query query) throws BaseException {
        if (em == null) {
            return null;
        }
        String sql = getJPQLString(query);
        if (StringUtils.isNotBlank(sql)) {
            sql = getSQLString(em, sql);
            return sql;
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private static String getJPQLString(Query query) throws BaseException {
        if (query == null) {
            return null;
        }
        try {
            QueryImpl queryObj = query.unwrap(QueryImpl.class);
            return queryObj.getQueryString();
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Failed to unwrap QueryImpl from Query", e);
        }
    }

    private static String getSQLString(EntityManager em, String jpqlString) throws BaseException {
        try {
            Object sessionImpl = em.unwrap(Class.forName("org.hibernate.internal.SessionImpl"));
            Class<?> clazzInner = sessionImpl.getClass().getSuperclass();
            Class<?> clazz = clazzInner.getSuperclass();
            Method m = clazz.getDeclaredMethod("getQueryPlan", new Class[] { String.class, boolean.class });
            m.setAccessible(true);
            Object hqlQueryPlan = m.invoke(sessionImpl, new Object[] { jpqlString, false });

            Class<?> planClazz = hqlQueryPlan.getClass();
            Method plm = planClazz.getMethod("getSqlStrings");
            String[] res = (String[]) plm.invoke(hqlQueryPlan);
            return res[0];
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Getting native SQL from JPQL failed", e);
        }
    }
}
