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
package hu.icellmobilsoft.coffee.jpa.sql.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import com.google.common.collect.Lists;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionFunction;
import hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionFunction2;
import hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionFunction3;

/**
 * Helper class for entity handling.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class EntityHelper {

    /** Constant <code>MAX_PARAMETER_COUNT=1000</code> */
    public static final int MAX_PARAMETER_COUNT = 1000;

    /**
     * Default constructor, constructs a new object.
     */
    public EntityHelper() {
        super();
    }

    /**
     * Can get Lazy loaded Entitys id, else org.hibernate.LazyInitializationException: could not initialize proxy - no Session will be thrown
     *
     * @param entity
     *            a E object.
     * @param <E>
     *            a E object.
     * @return a {@link java.lang.String} object.
     */
    public static <E> String getLazyId(E entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) entity).getHibernateLazyInitializer();
            if (lazyInitializer.isUninitialized()) {
                return (String) lazyInitializer.getIdentifier();
            }
        }
        return getId(entity);
    }

    /**
     * Getting identifier value from entity (annotated {@link Id})
     *
     * @param entity
     *            a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getId(Object entity) {
        if (entity == null) {
            return null;
        }
        Instance<EntityManager> instance = CDI.current().select(EntityManager.class);
        EntityManager em = instance.get();
        try {
            return (String) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        } finally {
            instance.destroy(em);
        }
    }

    /**
     * Általános bemeneti id listát partícionál {@link #MAX_PARAMETER_COUNT} alapján, a partícionált id listával lefuttatja f függvény alapján a kért
     * lekérdezéseket
     *
     * @param ids
     *            a {@link java.util.Collection} object.
     * @param function
     *            Callable function {@link hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionFunction} object.
     * @param <T>
     *            a T object.
     * @return result of id list select, partitioned to {@value #MAX_PARAMETER_COUNT} subselects
     * @throws hu.icellmobilsoft.coffee.exception.BaseException
     *             if any.
     */
    public static <T> List<T> partitionedQuery(Collection<String> ids, BaseExceptionFunction<List<String>, List<T>> function) throws BaseException {
        if (ids == null) {
            throw new InvalidParameterException("ids parameter is null");
        }

        List<String> copy = new ArrayList<>(ids);
        List<List<String>> lists = Lists.partition(copy, MAX_PARAMETER_COUNT);
        // memory optimalization
        copy = null;
        List<T> results = new ArrayList<>();
        for (List<String> sub : lists) {
            results.addAll(function.apply(sub));
        }
        return results;
    }

    /**
     * Általános bemeneti id listát partícionál MAX_PARAMETER_COUNT alapján, a partícionált id listával és param1 felhasználásával lefuttatja f
     * függvény alapján a kért lekérdezéseket
     *
     * @param <T>
     *            T object
     * @param <P>
     *            P object
     * @param ids
     *            id lista
     * @param param1
     *            select paraméter
     * @param function
     *            hívott funkció {@link BaseExceptionFunction2}
     * @return id lista select eredménye, mely {@value #MAX_PARAMETER_COUNT} sublista selectekre volt bontva
     * @throws BaseException
     *             exception
     */
    public static <T, P> List<T> partitionedQuery(Collection<String> ids, P param1, BaseExceptionFunction2<List<String>, P, List<T>> function)
            throws BaseException {
        if (ids == null || param1 == null) {
            throw new InvalidParameterException("ids or param1 is null");
        }

        List<String> copy = new ArrayList<>(ids);
        List<List<String>> lists = Lists.partition(copy, MAX_PARAMETER_COUNT);
        // memory optimalization
        copy = null;
        List<T> results = new ArrayList<>();
        for (List<String> sub : lists) {
            results.addAll(function.apply(sub, param1));
        }
        return results;
    }

    /**
     * Általános bemeneti id listát partícionál MAX_PARAMETER_COUNT alapján, a partícionált id listával és param1, param2 felhasználásával lefuttatja
     * f függvény alapján a kért lekérdezéseket
     *
     * @param <T>
     *            a T object.
     * @param <P1>
     *            a P1 object.
     * @param <P2>
     *            a P2 object.
     * @param ids
     *            id lista
     * @param param1
     *            select paraméter
     * @param param2
     *            select paraméter
     * @param function
     *            hívott funkció {@link BaseExceptionFunction3}
     * @return id lista select eredménye, mely {@value #MAX_PARAMETER_COUNT} sublista selectekre volt bontva
     * @throws BaseException
     *             exception
     */
    public static <T, P1, P2> List<T> partitionedQuery(Collection<String> ids, P1 param1, P2 param2,
            BaseExceptionFunction3<List<String>, P1, P2, List<T>> function) throws BaseException {
        if (ids == null || param1 == null || param2 == null) {
            throw new InvalidParameterException("ids, param1 or param2 is null");
        }

        List<String> copy = new ArrayList<>(ids);
        List<List<String>> lists = Lists.partition(copy, MAX_PARAMETER_COUNT);
        // memory optimalization
        copy = null;
        List<T> results = new ArrayList<>();
        for (List<String> sub : lists) {
            results.addAll(function.apply(sub, param1, param2));
        }
        return results;
    }
}
