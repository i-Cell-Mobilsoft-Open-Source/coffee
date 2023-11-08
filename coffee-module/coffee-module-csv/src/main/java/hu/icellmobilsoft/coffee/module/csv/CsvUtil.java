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
package hu.icellmobilsoft.coffee.module.csv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.exceptionhandler.ExceptionHandlerThrow;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.BusinessException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.csv.localization.LocalizedHeaderColumnNameWithPositionMappingStrategy;
import hu.icellmobilsoft.coffee.module.csv.strategy.HeaderColumnNameWithPositionMappingStrategy;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Csv utility.
 *
 * @author karoly.tamas
 * @author martin.nagy
 * @since 1.0.0
 */
public class CsvUtil {
    private static final Logger LOGGER = Logger.getLogger(CsvUtil.class);

    private static final char DEFAULT_SEPARATOR = ';';

    /**
     * Default constructor, constructs a new object.
     */
    public CsvUtil() {
        super();
    }

    /**
     * Converts bean list to CSV.
     *
     * @param <T>
     *            type of beans
     * @param beans
     *            {@link List} of beans to convert
     * @param clazz
     *            class of beans
     * @return converted CSV text
     * @throws BaseException
     *             if CSV file cannot be generated from beans.
     */
    public static <T> String toCsv(List<T> beans, Class<T> clazz) throws BaseException {
        return toCsv(beans, clazz, getDefaultMappingStrategy(clazz));
    }

    /**
     * Converts bean list to localized CSV.
     *
     * @param <T>
     *            type of beans
     * @param beans
     *            {@link List} of beans to convert
     * @param clazz
     *            class of beans
     * @param language
     *            the language of the CSV
     * @return converted CSV text
     * @throws BaseException
     *             if CSV file cannot be generated from beans.
     */
    public static <T> String toLocalizedCsv(List<T> beans, Class<T> clazz, String language) throws BaseException {
        if (beans == null || clazz == null || language == null) {
            throw new InvalidParameterException("beans or clazz or language is null!");
        }
        CDI<Object> cdi = CDI.current();
        LocalizedHeaderColumnNameWithPositionMappingStrategy<T> mappingStrategy = cdi
                .select(LocalizedHeaderColumnNameWithPositionMappingStrategy.class).get();
        mappingStrategy.setLanguage(language);
        try {
            return toCsv(beans, clazz, mappingStrategy);
        } finally {
            cdi.destroy(mappingStrategy);
        }
    }

    /**
     * Converts bean list to CSV.
     *
     * @param <T>
     *            type of beans
     * @param beans
     *            {@link List} of beans to convert
     * @param clazz
     *            class of beans
     * @param mappingStrategy
     *            the object that handle translating between the columns in the CSV file to an actual object
     * @return converted CSV text
     * @throws BaseException
     *             if CSV file cannot be generated from beans
     */
    public static <T> String toCsv(List<T> beans, Class<T> clazz, MappingStrategy<T> mappingStrategy) throws BaseException {
        if (beans == null || clazz == null || mappingStrategy == null) {
            throw new InvalidParameterException("beans or clazz or mappingStrategy is null!");
        }
        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw, DEFAULT_SEPARATOR, ICSVWriter.DEFAULT_QUOTE_CHARACTER, ICSVWriter.DEFAULT_ESCAPE_CHARACTER,
                System.lineSeparator());
        StatefulBeanToCsv<T> bc = new StatefulBeanToCsv<>(initMappingStrategy(mappingStrategy, clazz), new ExceptionHandlerThrow(), true, csvWriter,
                new ArrayListValuedHashMap<>(), null);

        try {
            bc.write(beans);
            return sw.toString();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            LOGGER.error("CSV file generation error", e);
            throw new BusinessException(CoffeeFaultType.CSV_GENERATE_FAULT, e.getMessage());
        }
    }

    /**
     * Converts CSV to bean list.
     *
     * @param <T>
     *            type of beans
     * @param csv
     *            CSV text
     * @param clazz
     *            class of beans
     * @return converted list of beans
     * @throws BaseException
     *             if CSV file cannot be read and converted to beans
     */
    public static <T> List<T> toBean(String csv, Class<? extends T> clazz) throws BaseException {
        if (StringUtils.isBlank(csv) || clazz == null) {
            throw new InvalidParameterException("csv or clazz is blank!");
        }
        try (CSVReader csvReader = createCsvReader(csv)) {
            return toBean(csvReader, clazz);
        } catch (Exception e) {
            LOGGER.error("CSV file read error", e);
            throw new BusinessException(CoffeeFaultType.INVALID_REQUEST, e.getMessage());
        }
    }

    /**
     * Converts CSV input stream to bean list.
     *
     * @param <T>
     *            type of beans
     * @param inputStream
     *            CSV text stream
     * @param clazz
     *            class of beans
     * @return converted list of beans
     * @throws BaseException
     *             if CSV file cannot be read and converted to beans
     */
    public static <T> List<T> toBean(InputStream inputStream, Class<? extends T> clazz) throws BaseException {
        if (inputStream == null || clazz == null) {
            throw new InvalidParameterException("inputStream or clazz is null!");
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                CSVReader csvReader = createCsvReader(inputStreamReader)) {
            return toBean(csvReader, clazz);
        } catch (Exception e) {
            LOGGER.error("CSV file read error", e);
            throw new BusinessException(CoffeeFaultType.INVALID_REQUEST, e.getMessage());
        }
    }

    private static CSVReader createCsvReader(String csv) {
        return new CSVReaderBuilder(new StringReader(csv)).withCSVParser(createCsvParser()).build();
    }

    private static CSVReader createCsvReader(InputStreamReader inputStreamReader) {
        return new CSVReaderBuilder(inputStreamReader).withCSVParser(createCsvParser()).build();
    }

    private static CSVParser createCsvParser() {
        return new CSVParserBuilder().withSeparator(DEFAULT_SEPARATOR).build();
    }

    private static <T> List<T> toBean(CSVReader csvReader, Class<? extends T> clazz) {
        CsvToBean<T> csvToBean = new CsvToBean<>();
        csvToBean.setMappingStrategy(getDefaultMappingStrategy(clazz));
        csvToBean.setCsvReader(csvReader);
        return csvToBean.parse();
    }

    private static <T> MappingStrategy<T> getDefaultMappingStrategy(Class<T> clazz) {
        MappingStrategy<T> mappingStrategy = new HeaderColumnNameWithPositionMappingStrategy<>();
        return initMappingStrategy(mappingStrategy, clazz);
    }

    private static <T> MappingStrategy<T> initMappingStrategy(MappingStrategy<T> mappingStrategy, Class<T> clazz) {
        mappingStrategy.setType(clazz);
        return mappingStrategy;
    }
}
