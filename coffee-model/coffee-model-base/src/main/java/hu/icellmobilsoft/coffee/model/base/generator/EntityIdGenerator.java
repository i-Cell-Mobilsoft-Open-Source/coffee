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
package hu.icellmobilsoft.coffee.model.base.generator;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import hu.icellmobilsoft.coffee.model.base.IIdentifiedEntity;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;

/**
 * Entity identifier generator.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class EntityIdGenerator implements IdentifierGenerator {

    /**
     * Default constructor, constructs a new object.
     */
    public EntityIdGenerator() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        if (object instanceof IIdentifiedEntity) {
            IIdentifiedEntity<?> entity = (IIdentifiedEntity<?>) object;
            if (entity.getId() == null) {
                return generateId();
            }
            return entity.getId();
        }
        return generateId();
    }

    /**
     * generate fix 16 length id!
     *
     * @return entityId
     */
    public static String generateId() {
        return RandomUtil.generateId();
    }

}
