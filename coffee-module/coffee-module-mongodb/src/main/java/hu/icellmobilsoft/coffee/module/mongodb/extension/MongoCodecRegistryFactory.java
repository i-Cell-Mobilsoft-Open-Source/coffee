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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;

import hu.icellmobilsoft.coffee.module.mongodb.codec.time.duration.DurationAsDocumentCodec;
import hu.icellmobilsoft.coffee.module.mongodb.codec.time.xmlgregoriancalendar.XMLGregorianCalendarCodec;

/**
 * Mongo codec registry
 * 
 * @author czenczl
 * @since 1.1.0
 */
@ApplicationScoped
public class MongoCodecRegistryFactory {

    /**
     * produce codecs for mongoDB, pay attention to registration order (first default, then custom, last base pojo)
     *
     * @return CodecRegistry
     */
    @Produces
    @ApplicationScoped
    public CodecRegistry produceCodecRegistry() {
        return CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), // default codecs
                CodecRegistries.fromCodecs(new DurationAsDocumentCodec(), new XMLGregorianCalendarCodec()), // custom codecs
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

}
