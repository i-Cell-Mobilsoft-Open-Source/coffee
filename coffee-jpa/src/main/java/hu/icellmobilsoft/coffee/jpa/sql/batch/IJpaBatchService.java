/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.jpa.sql.batch;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import hu.icellmobilsoft.coffee.jpa.sql.batch.enums.Status;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * JPA batch műveletek.
 *
 * @author attila-kiss-ot
 * @since 1.0.0
 */
public interface IJpaBatchService {

    /**
     * Batch delete.
     *
     * @param <E>
     *            entitas tipusa
     * @param entities
     *            torolni kivant collection
     * @param clazz
     *            a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az torolt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     */
    <E> Map<String, Status> batchDeleteNative(Collection<E> entities, Class<E> clazz) throws BaseException;

    /**
     * Batch insert mentes.
     *
     * @param <E>
     *            entitas tipusa
     * @param entities
     *            insertalni kivant collection
     * @param clazz
     *            a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az insertalt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     */
    <E> Map<String, Status> batchInsertNative(Collection<E> entities, Class<E> clazz) throws BaseException;

    /**
     * JPA batch mentes.
     *
     * @param <E>
     *            entitas tipusa
     * @param entities
     *            merge-olni kivant collection
     * @return merge-elt entitas id-k {@link List}-je
     * @throws BaseException
     *             exception
     */
    <E> List<String> batchMerge(Collection<E> entities) throws BaseException;

    /**
     * Szetvalogatja a beerkezo entitasokat az szerint hogy az id ki van-e toltve vagy sem es aszerint kuldi be a megfelelo metodusokba.
     *
     * @param <E>
     *            entitas tipusa
     * @param entities
     *            merge-olni kivant collection
     * @param clazz
     *            a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az merge-elt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     * @see #batchInsertNative(Collection, Class)
     * @see #batchUpdateNative(Collection, Class)
     */
    <E> Map<String, Status> batchMergeNative(Collection<E> entities, Class<E> clazz) throws BaseException;

    /**
     * Batch update mentes.
     *
     * @param <E>
     *            entitas tipusa
     * @param entities
     *            update-elni kivant collection
     * @param clazz
     *            a collection-ben levo osztalyok tipusa
     * @return {@link Map}, benne az update-elt entitas id-k es a hozzajuk tartozo feldolgozas sikeressege
     * @throws BaseException
     *             exception
     */
    <E> Map<String, Status> batchUpdateNative(Collection<E> entities, Class<E> clazz) throws BaseException;

}
