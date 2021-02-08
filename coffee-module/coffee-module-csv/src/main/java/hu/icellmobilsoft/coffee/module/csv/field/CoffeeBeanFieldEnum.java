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
package hu.icellmobilsoft.coffee.module.csv.field;

import java.lang.reflect.Field;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 * CoffeeBeanFieldEnum class.
 *
 * @param <T>
 *            type of the bean
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class CoffeeBeanFieldEnum<T> extends AbstractBeanField<T> implements CoffeeBeanField<T> {

    private Integer position;

    /**
     * Constructor for {@code CoffeeBeanFieldEnum}.
     *
     * @param field
     *            value for {@code field} field
     * @param position
     *            value for {@code position} field
     */
    public CoffeeBeanFieldEnum(Field field, int position) {
        super(field);
        this.position = position;
    }

    /** {@inheritDoc} */
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, CsvConstraintViolationException {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Class<? extends Enum> enumType = (Class<? extends Enum>) getField().getType();
        return Enum.valueOf(enumType, value);
    }

    /** {@inheritDoc} */
    public Integer getPosition() {
        return position;
    }
}
