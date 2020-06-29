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
package hu.icellmobilsoft.coffee.module.mongodb.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.mongodb.service.MongoService;

/**
 * Factory class for MongoService producer template
 * 
 * @author czenczl
 * @since 1.1.0
 *
 */
@ApplicationScoped
public class MongoServiceProducerFactory {

    /**
     * Producer template method for generating mongoService producer. No @Produces annotation, its just a template!
     * 
     * @param <T>
     * @param injectionPoint
     * @return
     */
    @SuppressWarnings("unchecked")
    @MongoServiceConfiguration(configKey = "", collectionKey = "")
    public <T> MongoService<T> mongoServiceTemplateProducer(final InjectionPoint injectionPoint) {
        MongoServiceConfiguration annotation = injectionPoint.getAnnotated().getAnnotation(MongoServiceConfiguration.class);
        String configKey = annotation.configKey();
        String collectionKey = annotation.collectionKey();

        // check required
        if (StringUtils.isAnyBlank(configKey, collectionKey)) {
            throw new IllegalStateException("configKey and collectionKey required!");
        }

        // create config helper
        Annotation qualifier = new MongoClientConfiguration.Literal(configKey);
        Instance<MongoDbClient> instance = CDI.current().select(MongoDbClient.class, qualifier);
        MongoDbClient mongoDbClient = instance.get();

        // get type under inject
        Class<T> pType = (Class<T>) injectionPoint.getAnnotated().getBaseType();

        // get superclass generic type, mongoEntity.class
        ParameterizedType parameterizedType = (ParameterizedType) pType.getGenericSuperclass();
        Class<T> valueClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];

        // select concrate implementation
        MongoService<T> mongoService = (MongoService<T>) CDI.current().select(pType).get();

        // init repositroy with collection
        mongoService.initRepositoryCollection(mongoDbClient.getMongoDatabase().getCollection(collectionKey, valueClass));
        return mongoService;
    }

}
