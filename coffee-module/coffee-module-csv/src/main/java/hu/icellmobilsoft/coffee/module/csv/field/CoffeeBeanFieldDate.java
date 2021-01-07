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

import com.opencsv.bean.BeanFieldDate;

/**
 * Kellett, mert a "gyári" nem tudott sorrendezést, ami tudott, az nem gyártott headert
 *
 * @param <T>
 *            type of the bean
 * @author andras.bognar
 * @since 1.0.0
 */

@Vetoed
public class CoffeeBeanFieldDate<T> extends BeanFieldDate<T> implements CoffeeBeanField<T> {

    private Integer position;

    /**
     * Constructor for {@code CoffeeBeanFieldDate}.
     *
     * @param field
     *            value for {@code field} field
     * @param required
     *            value for {@code required} field
     * @param formatString
     *            value for {@code formatString} field
     * @param locale
     *            value for {@code locale} field
     * @param position
     *            value for {@code position} field
     */
    public CoffeeBeanFieldDate(Field field, boolean required, String formatString, String locale, int position) {
        super(field, required, formatString, locale);
        this.position = position;
    }

    /** {@inheritDoc} */
    public Integer getPosition() {
        return position;
    }
}
