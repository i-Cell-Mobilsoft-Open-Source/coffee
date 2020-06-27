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

import java.util.Objects;

import javax.enterprise.context.Dependent;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * Alap MongoService implementáció a régi BasicDBObject-es használat végett Ez a MongoDbHandler-ben, a getMongoService metódus miatt szükséges
 *
 * @deprecated use @MongoDbClient or extend @MongoService<T>
 *
 * @author balazs.joo
 * @since 1.0.0
 */
@Dependent
@Deprecated
public class MongoServiceImpl extends MongoService<BasicDBObject> {

    private MongoCollection<BasicDBObject> mongoCollection;

    /** {@inheritDoc} */
    @Override
    protected MongoCollection<BasicDBObject> getMongoCollection() throws BaseException {
        if (Objects.isNull(mongoCollection)) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Mongo collection not set!");
        }
        return mongoCollection;
    }

    /**
     * <p>
     * Setter for the field <code>mongoCollection</code>.
     * </p>
     */
    public void setMongoCollection(MongoCollection<BasicDBObject> mongoCollection) {
        // init repository collection
        initRepositoryCollection(mongoCollection);
        this.mongoCollection = mongoCollection;
    }
}
