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
package hu.icellmobilsoft.coffee.module.csv.localization;

import java.lang.reflect.Field;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.BeanField;
import com.opencsv.exceptions.CsvBadConverterException;

import hu.icellmobilsoft.coffee.module.csv.strategy.HeaderColumnNameWithPositionMappingStrategy;
import hu.icellmobilsoft.coffee.module.localization.LocalizedMessage;

/**
 * Maps data to objects using the localized column names in the first row of the CSV file as reference. With this class the column order can also be
 * given.<br>
 * Initializes the {@link LocalizationConverter}s.
 *
 * @param <T>
 *            type of the bean to be returned
 * @author martin.nagy
 * @since 1.8.0
 */
@Dependent
public class LocalizedHeaderColumnNameWithPositionMappingStrategy<T> extends HeaderColumnNameWithPositionMappingStrategy<T> {

    @Inject
    private LocalizedMessage localizedMessage;

    private String language;

    @Override
    protected String getFieldName(Field field) {
        String key = getLocalizationKey(field);
        return localizedMessage.messageByLanguage(language, key).toUpperCase();
    }

    @Override
    protected BeanField<T, String> instantiateCustomConverter(Class<? extends AbstractBeanField<T, String>> converter)
            throws CsvBadConverterException {
        BeanField<T, String> beanField = super.instantiateCustomConverter(converter);
        if (beanField instanceof LocalizationConverter) {
            LocalizationConverter<T, String> localizationConverter = (LocalizationConverter<T, String>) beanField;
            localizationConverter.setLocalizedMessage(localizedMessage);
            localizationConverter.setLanguage(language);
        }
        return beanField;
    }

    protected String getLocalizationKey(Field field) {
        return "{" + field.getDeclaringClass().getCanonicalName() + '.' + field.getName() + "}";
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
