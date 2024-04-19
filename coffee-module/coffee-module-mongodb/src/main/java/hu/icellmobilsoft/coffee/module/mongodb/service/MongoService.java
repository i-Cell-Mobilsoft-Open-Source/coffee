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
package hu.icellmobilsoft.coffee.module.mongodb.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.mongodb.annotation.MongoServiceBaseQualifier;
import hu.icellmobilsoft.coffee.module.mongodb.repository.MongoRepository;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Base mongo service class.
 *
 * @param <T>
 *            MongoEntity parameter
 * @author imre.scheffer
 * @since 1.0.0
 *
 */
@Dependent
@MongoServiceBaseQualifier
public class MongoService<T> {

    private static final String COLUMN_MONGO_ID = "_id";
    private static final String FILTER_NULL_ERROR_MSG = "filter is null!";
    private static final String FILTER_RESULT_ERROR_MSG = "Error occurred in finding mongo data by filter: {0}";

    @Inject
    private Logger log;

    @Inject
    private MongoRepository<T> mongoRepository;

    /**
     * Default constructor, constructs a new object.
     */
    public MongoService() {
        super();
    }

    /**
     * Mongo Collection beállítása
     *
     * @param mongoCollection
     *            MongoDb collection
     */
    public void initRepositoryCollection(MongoCollection<T> mongoCollection) {
        mongoRepository.setMongoCollection(mongoCollection);
    }

    /**
     * Objektum beszúrása az előre beállított Mongo Collection-be
     *
     * @param document
     *            MongoEntity document
     * @throws BaseException
     *             When Mongo insert fail.
     */
    public void insertOne(T document) throws BaseException {
        log.trace(">> MongoService.insertOne(document: [{0}]", document);

        if (document == null) {
            throw new InvalidParameterException("document is null!");
        }
        try {
            mongoRepository.insertOne(document);
        } catch (Exception e) {
            String msg = MessageFormat.format("Error occurred in inserting mongo data: {0}", e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        } finally {
            log.trace("<< MongoService.insertOne(document: [{0}]", document);
        }
    }

    /**
     * Objektum keresése a megadott mongoId alapján
     *
     * @param mongoId
     *            mongo document id
     * @throws BaseException
     *             When Mongo select fail.
     * @return T
     *
     */
    public T findById(String mongoId) throws BaseException {
        if (StringUtils.isBlank(mongoId)) {
            throw new InvalidParameterException("mongoId is blank!");
        }

        try {
            return findFirst(new BasicDBObject(COLUMN_MONGO_ID, new ObjectId(mongoId)));
        } catch (IllegalArgumentException e) {
            String msg = MessageFormat.format("The mongoId [{0}] is invalid: {1}", mongoId, e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        }
    }

    /**
     * Objektum keresése a megadott szűrő feltételek alapján, rendezés nélkül
     *
     * @param filter
     *            mongo select filter
     * @throws BaseException
     *             When Mongo select fail.
     * @return T
     */
    public T findFirst(Bson filter) throws BaseException {
        log.trace(">> MongoService.findFirst(filter: [{0}]", filter);

        if (filter == null) {
            throw new InvalidParameterException(FILTER_NULL_ERROR_MSG);
        }
        T found;
        try {
            found = mongoRepository.findFirst(filter);
        } catch (Exception e) {
            String msg = MessageFormat.format(FILTER_RESULT_ERROR_MSG, e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        } finally {
            log.trace("<< MongoService.findFirst(filter: [{0}]", filter);
        }

        if (found == null) {
            throw new BONotFoundException(getDefaultNotFoundFaultTypeEnum(), "Object by filter [" + filter + "] not found");
        }
        return found;
    }

    /**
     * Objektum keresése a megadott szűrő feltételek alapján, rendezéssel
     *
     * @param filter
     *            mongo select filter
     * @param order
     *            mongo select order
     * @throws BaseException
     *             When Mongo select fail.
     * @return T
     */
    public T findFirst(Bson filter, Bson order) throws BaseException {
        log.trace(">> MongoService.findFirst(filter: [{0}], order: [{1}]", filter, order);

        if (filter == null) {
            throw new InvalidParameterException(FILTER_NULL_ERROR_MSG);
        }
        T found;
        try {
            found = mongoRepository.findFirst(filter, order);
        } catch (Exception e) {
            String msg = MessageFormat.format(FILTER_RESULT_ERROR_MSG, e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        } finally {
            log.trace("<< MongoService.findFirst(filter: [{0}], order: [{1}]", filter, order);
        }
        if (found == null) {
            throw new BONotFoundException(getDefaultNotFoundFaultTypeEnum(), "Object by filter [" + filter + "] not found");
        }

        return found;
    }

    /**
     * Objektumok közötti keresés, ahol az eredmény a megadott típusú elemek listája.
     *
     * @throws BaseException
     *             When Mongo select fail.
     * @return List
     */
    public List<T> findAll() throws BaseException {
        return findAll(new BasicDBObject());
    }

    /**
     * Objektumok közötti keresés, ahol az eredmény a megadott szűrés alapján található megadott típusú elemek listája.
     *
     * @param filter
     *            mongo select filter
     * @throws BaseException
     *             When mongo select fail.
     * @return List
     */
    public List<T> findAll(Bson filter) throws BaseException {
        log.trace(">> MongoService.findAll(filter: [{0}])", filter);

        if (filter == null) {
            throw new InvalidParameterException(FILTER_NULL_ERROR_MSG);
        }
        List<T> found;
        try {
            found = mongoRepository.findAll(filter);
        } catch (Exception e) {
            String msg = MessageFormat.format(FILTER_RESULT_ERROR_MSG, e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        } finally {
            log.trace("<< MongoService.findAll(filter: [{0}])", filter);
        }
        if (found == null) {
            found = new ArrayList<>();
        }
        return found;
    }

    /**
     * Objektumok közötti keresés, ahol az eredmény a megadott szűrés, rendezés, és lapozási beállítások alapján található megadott típusú elemek
     * listája.
     *
     * @param filter
     *            mongo select filter
     * @param order
     *            mongo select order
     * @param rows
     *            result row limit
     * @param page
     *            mongo result page
     * @param clazz
     *            Mongo document class
     * @throws BaseException
     *             When Mongo pagination select fail.
     * @return List
     */
    public List<T> find(Bson filter, Bson order, int rows, int page, Class<T> clazz) throws BaseException {
        log.trace(">> MongoService.find(filter: [{0}], order: [{1}], rows: [{2}], page: [{3}], clazz: [{4}]", filter, order, rows, page, clazz);

        if (filter == null) {
            throw new InvalidParameterException(FILTER_NULL_ERROR_MSG);
        }
        List<T> found;
        try {
            found = mongoRepository.find(filter, order, rows, page, clazz);
        } catch (Exception e) {
            String msg = MessageFormat.format(FILTER_RESULT_ERROR_MSG, e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        } finally {
            log.trace("<< MongoService.find(filter: [{0}], order: [{1}], rows: [{2}], page: [{3}], clazz: [{4}]", filter, order, rows, page, clazz);
        }
        if (found == null) {
            found = new ArrayList<>();
        }
        return found;
    }

    /**
     * A megadott szűrés alapján megtalálható elemek számosságát adja vissza.
     *
     * @param filter
     *            mongo select filter
     * @throws BaseException
     *             When Mongo count fail.
     * @return long
     */
    public long count(Bson filter) throws BaseException {
        log.trace(">> MongoService.count(filter: [{0}]", filter);

        if (filter == null) {
            throw new InvalidParameterException(FILTER_NULL_ERROR_MSG);
        }
        long found;
        try {
            found = mongoRepository.count(filter);
        } catch (Exception e) {
            String msg = MessageFormat.format("Error occurred in counting mongo data by filter: {0}", e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        } finally {
            log.trace("<< MongoService.count(filter: [{0}]", filter);
        }
        return found;
    }

    /**
     * Batchelt mentést megvalósító metódus
     *
     * @param documents
     *            Documents to insert
     * @throws BaseException
     *             When insert fail.
     */
    public void insertMany(List<T> documents) throws BaseException {
        log.trace(">> BaseMongoService.insertMany(documents: [{0}]", documents);

        if (CollectionUtils.isEmpty(documents)) {
            log.info("<< BaseMongoService.insertMany(No documents to save!)");
            return;
        }

        try {
            mongoRepository.insertMany(documents);
        } catch (Exception e) {
            String msg = MessageFormat.format("Error occurred in inserting mongo datas: {0}", e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        } finally {
            log.trace("<< BaseMongoService.insertMany(documents: [{0}]", documents);
        }
    }

    /**
     * BONotFoundException FaultType felülírás lehetőségét nyújtó metódus
     *
     * @return Enum
     */
    protected Enum<?> getDefaultNotFoundFaultTypeEnum() {
        return CoffeeFaultType.ENTITY_NOT_FOUND;
    }

}
