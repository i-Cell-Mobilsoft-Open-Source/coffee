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
package hu.icellmobilsoft.coffee.module.csv.strategy;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.opencsv.bean.BeanField;
import com.opencsv.bean.BeanFieldPrimitiveTypes;
import com.opencsv.bean.CsvBind;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;

import hu.icellmobilsoft.coffee.module.csv.annotation.CsvBindByNamePosition;
import hu.icellmobilsoft.coffee.module.csv.field.CoffeeBeanField;
import hu.icellmobilsoft.coffee.module.csv.field.CoffeeBeanFieldDate;
import hu.icellmobilsoft.coffee.module.csv.field.CoffeeBeanFieldEnum;
import hu.icellmobilsoft.coffee.module.csv.field.CoffeeBeanFieldPrimitiveTypes;

/**
 * Kellett, mert a "gyári" nem tudott sorrendezést, ami tudott, az nem gyártott headert
 *
 * @param <T>
 *            type of the bean to be returned
 * @author andras.bognar
 * @since 1.0.0
 */

@Vetoed
public class CustomHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

    /**
     * Constructor for CustomHeaderColumnNameMappingStrategy.
     */
    public CustomHeaderColumnNameMappingStrategy() {
    }

    /**
     * Constructor for CustomHeaderColumnNameMappingStrategy.
     *
     * @param clazz
     *            value for {@code type} field
     */
    public CustomHeaderColumnNameMappingStrategy(Class<? extends T> clazz) {
        this.setType(clazz);
    }

    /** {@inheritDoc} */
    @Override
    public String[] generateHeader() {
        if (header == null) {
            if (fieldMap == null) {
                loadFields(type);
            }

            List<Map.Entry<String, BeanField>> list = new LinkedList<>(fieldMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, BeanField>>() {
                @Override
                public int compare(Map.Entry<String, BeanField> o1, Map.Entry<String, BeanField> o2) {
                    if (o1.getValue() instanceof CoffeeBeanField && o2.getValue() instanceof CoffeeBeanField) {
                        return (((CoffeeBeanField) o1.getValue()).getPosition()).compareTo(((CoffeeBeanField) o2.getValue()).getPosition());
                    }
                    return 0;
                }
            });
            Map<String, BeanField> result = new LinkedHashMap<>();
            for (Map.Entry<String, BeanField> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            header = result.keySet().toArray(new String[result.size()]);
        }
        return ArrayUtils.clone(header);
    }

    /** {@inheritDoc} */
    @Override
    protected void loadFieldMap() throws CsvBadConverterException {
        fieldMap = new HashMap<String, BeanField>();

        for (Field field : loadFields(getType())) {
            String columnName;
            String locale;
            int position;
            if (field.isAnnotationPresent(CsvBindByNamePosition.class)) {
                boolean required = field.getAnnotation(CsvBindByNamePosition.class).required();
                String annotationColumnName = field.getAnnotation(CsvBindByNamePosition.class).column().toUpperCase().trim();
                locale = field.getAnnotation(CsvBindByNamePosition.class).locale();
                position = field.getAnnotation(CsvBindByNamePosition.class).position();
                columnName = StringUtils.isEmpty(annotationColumnName) ? field.getName().toUpperCase() : annotationColumnName;
                if (field.isAnnotationPresent(CsvDate.class)) {
                    String formatString = field.getAnnotation(CsvDate.class).value();
                    fieldMap.put(columnName, new CoffeeBeanFieldDate(field, required, formatString, locale, position));
                } else {
                    if (field.getType().isEnum()) {
                        fieldMap.put(columnName, new CoffeeBeanFieldEnum(field, position));
                    } else {
                        fieldMap.put(columnName, new CoffeeBeanFieldPrimitiveTypes(field, required, locale, position));
                    }
                }
            } else {
                boolean required = field.getAnnotation(CsvBind.class).required();
                fieldMap.put(field.getName().toUpperCase(), new BeanFieldPrimitiveTypes(field, required, null));
            }
        }
    }

    private PropertyDescriptor[] loadDescriptors(Class<? extends T> cls) throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(cls);
        return beanInfo.getPropertyDescriptors();
    }

    private List<Field> loadFields(Class<? extends T> cls) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : FieldUtils.getAllFields(cls)) {
            if (field.isAnnotationPresent(CsvBind.class) || field.isAnnotationPresent(CsvBindByNamePosition.class)) {
                fields.add(field);
            }
        }
        annotationDriven = !fields.isEmpty();
        return fields;
    }
}
