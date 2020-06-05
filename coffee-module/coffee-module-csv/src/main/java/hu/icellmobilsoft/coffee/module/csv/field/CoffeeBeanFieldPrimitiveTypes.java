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

import com.opencsv.bean.BeanFieldPrimitiveTypes;

/**
 * Kellett, mert a "gyári" nem tudott sorrendezést, ami tudott, az nem gyártott headert
 *
 * @author andras.bognar
 * @since 1.0.0
 */

@Vetoed
public class CoffeeBeanFieldPrimitiveTypes<T> extends BeanFieldPrimitiveTypes<T> implements CoffeeBeanField<T> {

    private int position;

    /**
     * <p>Constructor for CoffeeBeanFieldPrimitiveTypes.</p>
     */
    public CoffeeBeanFieldPrimitiveTypes(Field field, boolean required, String locale, int position) {
        super(field, required, locale);
        this.position = position;
    }

    /**
     * <p>Getter for the field <code>position</code>.</p>
     */
    public Integer getPosition() {
        return position;
    }
}
