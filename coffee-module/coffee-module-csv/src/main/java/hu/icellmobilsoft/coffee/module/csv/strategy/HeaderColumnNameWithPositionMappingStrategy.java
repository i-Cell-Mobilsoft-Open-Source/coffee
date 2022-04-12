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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.BeanFieldSingleValue;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvBindByPositions;
import com.opencsv.bean.CsvConverter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;

import hu.icellmobilsoft.coffee.module.csv.annotation.CsvBindByNamePosition;
import hu.icellmobilsoft.coffee.module.csv.annotation.CsvCustomBindByNamePosition;

/**
 * Maps data to objects using the column names in the first row of the CSV file as reference. With this class the column order can also be given.
 *
 * @param <T>
 *            type of the bean to be returned
 * @author andras.bognar
 * @author martin.nagy
 * @since 1.0.0
 */
@Vetoed
public class HeaderColumnNameWithPositionMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

    /**
     * Stores the index of the field by the name of the field
     */
    protected Map<String, Integer> fieldIndexByName;

    @Override
    protected void loadFieldMap() throws CsvBadConverterException {
        fieldIndexByName = new HashMap<>();
        writeOrder = this::compare;
        super.loadFieldMap();
    }

    private int compare(String column1, String column2) {
        Integer position1 = fieldIndexByName.get(column1);
        Integer position2 = fieldIndexByName.get(column2);
        if (position1 != null && position2 != null) {
            return position1.compareTo(position2);
        }
        if (position1 != null) {
            return -1;
        }
        if (position2 != null) {
            return 1;
        }
        return 0;
    }

    @Override
    protected Set<Class<? extends Annotation>> getBindingAnnotations() {
        Set<Class<? extends Annotation>> bindingAnnotations = super.getBindingAnnotations();
        bindingAnnotations.add(CsvBindByNamePosition.class);
        bindingAnnotations.add(CsvCustomBindByNamePosition.class);
        bindingAnnotations.add(CsvBindByPosition.class);
        bindingAnnotations.add(CsvBindByPositions.class);
        return bindingAnnotations;
    }

    @Override
    protected void loadAnnotatedFieldMap(ListValuedMap<Class<?>, Field> fields) {
        super.loadAnnotatedFieldMap(fields);

        for (Map.Entry<Class<?>, Field> classAndField : fields.entries()) {
            Class<?> localType = classAndField.getKey();
            Field localField = classAndField.getValue();

            if (localField.isAnnotationPresent(CsvBindByNamePosition.class)) {
                CsvBindByNamePosition annotation = selectAnnotationForProfile(localField.getAnnotationsByType(CsvBindByNamePosition.class),
                        CsvBindByNamePosition::profiles);
                if (annotation != null) {
                    registerBinding(annotation, localType, localField);
                }
            } else if (localField.isAnnotationPresent(CsvCustomBindByNamePosition.class)) {
                CsvCustomBindByNamePosition annotation = selectAnnotationForProfile(
                        localField.getAnnotationsByType(CsvCustomBindByNamePosition.class), CsvCustomBindByNamePosition::profiles);
                if (annotation != null) {
                    registerBinding(annotation, localType, localField);
                }
            } else if (localField.isAnnotationPresent(CsvBindByPosition.class) || localField.isAnnotationPresent(CsvBindByPositions.class)) {
                CsvBindByPosition annotation = selectAnnotationForProfile(localField.getAnnotationsByType(CsvBindByPosition.class),
                        CsvBindByPosition::profiles);
                if (annotation != null) {
                    registerBinding(annotation, localType, localField);
                }
            }
        }
    }

    private void registerBinding(CsvCustomBindByNamePosition annotation, Class<?> localType, Field localField) {
        Class<? extends AbstractBeanField<T, String>> converter = (Class<? extends AbstractBeanField<T, String>>) annotation.converter();
        String columnName = getFieldName(localField);
        int position = annotation.position();

        BeanField<T, String> bean = instantiateCustomConverter(converter);
        bean.setType(localType);
        bean.setField(localField);
        bean.setRequired(annotation.required());

        getFieldMap().put(columnName, bean);
        fieldIndexByName.put(columnName, position);
    }

    private void registerBinding(CsvBindByPosition annotation, Class<?> localType, Field localField) {
        String fieldLocale = annotation.locale();
        String fieldWriteLocale = annotation.writeLocaleEqualsReadLocale() ? fieldLocale : annotation.writeLocale();
        String columnName = getFieldName(localField);
        CsvConverter converter = determineConverter(localField, localField.getType(), fieldLocale, fieldWriteLocale, null);
        fieldMap.put(columnName, new BeanFieldSingleValue<>(localType, localField, annotation.required(), errorLocale, converter,
                annotation.capture(), annotation.format()));
        fieldIndexByName.put(columnName, annotation.position());
    }

    private void registerBinding(CsvBindByNamePosition annotation, Class<?> localType, Field localField) {
        CsvConverter converter = determineConverter(localField, localField.getType(), annotation.locale(), annotation.locale(), null);
        int position = annotation.position();
        String columnName = getColumnName(annotation, localField);

        getFieldMap().put(columnName, new BeanFieldSingleValue<>(localType, localField, annotation.required(), errorLocale, converter,
                annotation.capture(), annotation.format()));
        fieldIndexByName.put(columnName, position);
    }

    private String getColumnName(CsvBindByNamePosition annotation, Field localField) {
        String columnName = annotation.column().toUpperCase().trim();
        if (StringUtils.isEmpty(columnName)) {
            return getFieldName(localField);
        }
        return columnName;
    }

    /**
     * Returns the name of the given field
     * 
     * @param field
     *            the field we search the name for
     * @return the name of the field
     */
    protected String getFieldName(Field field) {
        return field.getName().toUpperCase();
    }

}
