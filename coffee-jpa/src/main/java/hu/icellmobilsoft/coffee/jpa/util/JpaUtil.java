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

import java.util.function.Supplier;

import jakarta.enterprise.inject.Vetoed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.hibernate.query.spi.DomainQueryExecutionContext;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.QueryInterpretationCache;
import org.hibernate.query.spi.SelectQueryPlan;
import org.hibernate.query.sqm.internal.ConcreteSqmSelectQueryPlan;
import org.hibernate.query.sqm.internal.DomainParameterXref;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.internal.SqmInterpretationsKey;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.sql.exec.spi.JdbcSelect;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * JPA common functions.
 *
 * @author imre.scheffer
 * @author tamas.cserhati
 * @since 1.0.0
 */
@Vetoed
public class JpaUtil {

    /**
     * Get native SQL from created Query, exception is logged only.
     *
     * @param em
     *            entity manager (ignored)
     * @param query
     *            created query
     * @return native SQL of query or null
     * @deprecated use {@link #toNativeSQLNoEx(Query)} instead
     */
    @Deprecated(since = "2.1.0", forRemoval = true)
    public static String toNativeSQLNoEx(EntityManager em, Query query) {
        try {
            return toNativeSQL(query);
        } catch (BaseException e) {
            Logger.getLogger(JpaUtil.class).warn("Exception on converting Query to native SQL!", e);
        }
        return null;
    }

    /**
     * Get native SQL from created Query, exception is logged only
     *
     * @param query
     *            created query
     * @return native SQL of query or null
     */
    public static String toNativeSQLNoEx(Query query) {
        try {
            return toNativeSQL(query);
        } catch (BaseException e) {
            Logger.getLogger(JpaUtil.class).warn("Exception on converting Query to native SQL!", e);
        }
        return null;
    }

    /**
     * Get native SQL from created Query
     *
     * @param em
     *            entity manager (ignored)
     * @param criteriaQuery
     *            created query
     * @return native SQL of query
     * @throws BaseException
     *             on error
     * @deprecated use {@link #toNativeSQL(Query)} instead
     */
    @Deprecated(since = "2.1.0", forRemoval = true)
    public static String toNativeSQL(EntityManager em, Query criteriaQuery) throws BaseException {
        return toNativeSQL(criteriaQuery);
    }

    /**
     * Get native SQL from created Query
     *
     * @param criteriaQuery
     *            created query
     * @return native SQL of query or {@code null} if criteriaQuery parameter is null
     * @throws BaseException
     *             on error
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String toNativeSQL(Query criteriaQuery) throws BaseException {
        if (criteriaQuery == null) {
            return null;
        }
        QueryImplementor query = criteriaQuery.unwrap(QueryImplementor.class);
        if (query instanceof SqmInterpretationsKey.InterpretationsKeySource && query instanceof QuerySqmImpl) {
            QueryInterpretationCache.Key cacheKey = SqmInterpretationsKey
                    .createInterpretationsKey((SqmInterpretationsKey.InterpretationsKeySource) query);
            QuerySqmImpl<?> querySqm = (QuerySqmImpl<?>) query;
            Supplier buildSelectQueryPlan = () -> {
                try {
                    return ReflectionUtils.invokeMethod(querySqm, "buildSelectQueryPlan", ConcreteSqmSelectQueryPlan.class);
                } catch (BaseException e) {
                    Logger.getLogger(JpaUtil.class).warn("Exception on calling buildSelectQueryPlan()!", e);
                    return null;
                }
            };
            SelectQueryPlan plan = cacheKey != null
                    ? ((QueryImplementor<?>) query).getSession()
                            .getFactory()
                            .getQueryEngine()
                            .getInterpretationCache()
                            .resolveSelectQueryPlan(cacheKey, buildSelectQueryPlan)
                    : (SelectQueryPlan<?>) buildSelectQueryPlan.get();
            if (plan instanceof ConcreteSqmSelectQueryPlan) {
                ConcreteSqmSelectQueryPlan<?> selectQueryPlan = (ConcreteSqmSelectQueryPlan<?>) plan;
                Object cacheableSqmInterpretation = ReflectionUtils.getFieldValueOrNull(selectQueryPlan, "cacheableSqmInterpretation", Object.class);
                if (cacheableSqmInterpretation == null) {
                    DomainQueryExecutionContext domainQueryExecutionContext = DomainQueryExecutionContext.class.cast(querySqm);
                    cacheableSqmInterpretation = ReflectionUtils.invokeStaticMethod(
                            ReflectionUtils.getMethod(
                                    ConcreteSqmSelectQueryPlan.class,
                                    "buildCacheableSqmInterpretation",
                                    SqmSelectStatement.class,
                                    DomainParameterXref.class,
                                    DomainQueryExecutionContext.class),
                            Object.class,
                            ReflectionUtils.getFieldValueOrNull(selectQueryPlan, "sqm", SqmSelectStatement.class),
                            ReflectionUtils.getFieldValueOrNull(selectQueryPlan, "domainParameterXref", DomainParameterXref.class),
                            domainQueryExecutionContext);
                }
                if (cacheableSqmInterpretation != null) {
                    JdbcSelect jdbcSelect = ReflectionUtils.getFieldValueOrNull(cacheableSqmInterpretation, "jdbcSelect", JdbcSelect.class);
                    if (jdbcSelect != null) {
                        return jdbcSelect.getSql();
                    }
                }
            } else {
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Cannot invoke buildSelectQueryPlan()");
            }
        }
        return ReflectionUtils.invokeMethod(query, "getQueryString", String.class);
    }
}
