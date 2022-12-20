/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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

import java.text.MessageFormat;

import jakarta.enterprise.inject.Vetoed;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import hu.icellmobilsoft.coffee.module.localization.LocalizedMessage;

/**
 * Converts bean values to localized strings. This class can handle {@link Enum}s, {@link Boolean}s, and {@code null} values, but can be extended.<br>
 * Should only be used with {@link LocalizedHeaderColumnNameWithPositionMappingStrategy} mapping strategy.
 *
 * @param <T>
 *            Type of the bean to be manipulated
 * @param <I>
 *            Type of the index into multivalued fields
 * 
 * @author martin.nagy
 * @since 1.8.0
 */
@Vetoed
public class LocalizationConverter<T, I> extends AbstractBeanField<T, I> {
    private LocalizedMessage localizedMessage;
    private String language;

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        throw new ConverterException("LocalizationConverter not supports parsing");
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        checkInitialization();

        if (value == null) {
            return localizeNull();
        }
        if (value instanceof Enum<?>) {
            return localizeEnum((Enum<?>) value);
        }
        if (value instanceof Boolean) {
            return localizeBoolean((Boolean) value);
        }
        return localizeOther(value);
    }

    /**
     * Returns the localized string representing {@code null}
     * 
     * @return the localized string
     */
    protected String localizeNull() {
        return null;
    }

    /**
     * Returns the localized string representing the passed boolean
     * 
     * @param value
     *            the value to be localized
     * @return the localized string
     */
    protected String localizeBoolean(Boolean value) {
        return localizedMessage.messageByLanguage(language, "{" + value.getClass().getCanonicalName() + "." + value.toString().toUpperCase() + "}");
    }

    /**
     * Returns Returns the localized string representing the passed enum
     * 
     * @param value
     *            the value to be localized
     * @return the localized string
     */
    protected String localizeEnum(Enum<?> value) {
        return localizedMessage.messageByLanguage(language, "{" + value.getClass().getName() + "." + value.name() + "}");
    }

    /**
     * Returns Returns Returns the localized string representing the passed value
     * 
     * @param value
     *            the value to be localized
     * @return the localized string
     * @throws CsvDataTypeMismatchException
     *             if the value of the given type cannot be converted
     */
    protected String localizeOther(Object value) throws CsvDataTypeMismatchException {
        throw new CsvDataTypeMismatchException(value, field.getType(),
                MessageFormat.format("Type not supported! Can not convert to localized value! Actual Type: [{0}] ", field.getType().getName()));
    }

    /**
     * Throws exception if the class is not initialized
     */
    protected void checkInitialization() {
        if (localizedMessage == null || language == null) {
            throw new ConverterException("Converter not initialized. Should be used with LocalizedHeaderColumnNameWithPositionMappingStrategy!");
        }
    }

    /**
     * Returns the value of the {@link #localizedMessage} field
     *
     * @return the value of the {@link #localizedMessage} field
     */
    public LocalizedMessage getLocalizedMessage() {
        return localizedMessage;
    }

    /**
     * Sets the {@link #localizedMessage} field
     *
     * @param localizedMessage
     *            the new value for the {@link #localizedMessage} field
     */
    public void setLocalizedMessage(LocalizedMessage localizedMessage) {
        this.localizedMessage = localizedMessage;
    }

    /**
     * Returns the value of the {@link #language} field
     *
     * @return the value of the {@link #language} field
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the {@link #language} field
     *
     * @param language
     *            the new value for the {@link #language} field
     */
    public void setLanguage(String language) {
        this.language = language;
    }

}
