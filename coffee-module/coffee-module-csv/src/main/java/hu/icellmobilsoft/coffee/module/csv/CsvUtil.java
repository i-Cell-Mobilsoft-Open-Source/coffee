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

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.BusinessException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.csv.strategy.CustomHeaderColumnNameMappingStrategy;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Csv utility.
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
public class CsvUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(CsvUtil.class);

    private static final char DEFAULT_SEPARATOR = ';';
    /** Constant <code>DEFAULT_LIST_SEPARATOR=','</code> */
    public static final char DEFAULT_LIST_SEPARATOR = ',';

    /** Constant <code>SIMPLE_CSV_DATE_FORMAT="yyyy-MM-dd"</code> */
    public static final String SIMPLE_CSV_DATE_FORMAT = "yyyy-MM-dd";
    /** Constant <code>CSV_FILE_NAME_DATE_FORMAT="yyyyMMddHHmmss"</code> */
    public static final String CSV_FILE_NAME_DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * Bean list to csv.
     *
     * @param beans
     * @param clazz
     * @throws BaseException
     */
    public static <T> String toCsv(List<T> beans, Class<? extends T> clazz) throws BaseException {
        StringWriter sw = new StringWriter();
        try {
            sw = new StringWriter();
            StatefulBeanToCsv<T> bc = new StatefulBeanToCsv<>(CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END,
                    new CustomHeaderColumnNameMappingStrategy<>(clazz), CSVWriter.DEFAULT_QUOTE_CHARACTER, DEFAULT_SEPARATOR, false, sw);
            bc.write(beans);
            return sw.getBuffer().toString();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            LOGGER.error("CSV file generation error", e);
            throw new BusinessException(CoffeeFaultType.CSV_GENERATE_FAULT, e.getMessage());
        }
    }

    /**
     * Csv to bean list.
     *
     * @param csv
     * @param clazz
     * @throws BaseException
     */
    public static <T> List<T> toBean(String csv, Class<? extends T> clazz) throws BaseException {
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new StringReader(csv), DEFAULT_SEPARATOR);
            return toBean(csvReader, clazz);
        } catch (Exception e) {
            LOGGER.error("CSV file read error", e);
            throw new BusinessException(CoffeeFaultType.INVALID_REQUEST, e.getMessage());
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (Exception e) {
                    LOGGER.error("Unable to close csvReader", e);
                    throw new BusinessException(CoffeeFaultType.OPERATION_FAILED, e.getMessage());
                }
            }
        }
    }

    /**
     * Csv stream to bean list.
     *
     * @param inputStream
     * @param clazz
     * @throws BaseException
     */
    public static <T> List<T> toBean(InputStream inputStream, Class<? extends T> clazz) throws BaseException {
        CSVReader csvReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            csvReader = new CSVReader(inputStreamReader, DEFAULT_SEPARATOR);
            return toBean(csvReader, clazz);
        } catch (Exception e) {
            LOGGER.error("CSV file read error", e);
            throw new BusinessException(CoffeeFaultType.INVALID_REQUEST, e.getMessage());
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (Exception e) {
                    LOGGER.error("Unable to close csvReader", e);
                    throw new BusinessException(CoffeeFaultType.OPERATION_FAILED, e.getMessage());
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    LOGGER.error("Unable to close inputStreamReader", e);
                    throw new BusinessException(CoffeeFaultType.OPERATION_FAILED, e.getMessage());
                }
            }
        }
    }

    private static <T> List<T> toBean(CSVReader csvReader, Class<? extends T> clazz) {
        CsvToBean<T> cb = new CsvToBean<>();
        return cb.parse(new CustomHeaderColumnNameMappingStrategy<>(clazz), csvReader);
    }
}
