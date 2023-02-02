/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.jpa.sql.batch.types;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.internal.NamedBasicTypeImpl;

/**
 * Class for wrap String as hibernate basic type.
 * 
 * @author csaba.balogh
 * @since 2.0.0
 */
public class StringBasicType extends NamedBasicTypeImpl<String> {

    /**
     * Instance of {@link StringBasicType}.
     */
    public static final StringBasicType INSTANCE = new StringBasicType();

    private StringBasicType() {
        super(StringJavaType.INSTANCE, VarcharJdbcType.INSTANCE, StandardBasicTypes.STRING.getName());
    }
}
