/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.csv.configuration;

import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;
import com.opencsv.ResultSetHelper;

import hu.icellmobilsoft.coffee.module.csv.CsvUtil;

/**
 * Config holder for csv writer. All values are filled with default values so no need to set all values because of defaults. Use the Builder creating
 * new config:
 * 
 * <pre>
 * CsvWriterConfig csvWriterConfig = new CsvWriterConfig.Builder().withQuotechar('\'').withSeparator(',').build();
 * </pre>
 *
 */
public class CsvWriterConfig {
    /**
     * Multiple csv format options. {@link ICSVParser}
     */
    private ICSVParser parser;
    /**
     * Separator char in csv
     */
    private Character separator;
    /**
     * Quote char in csv
     */
    private Character quotechar;
    /**
     * Escape char in csv
     */
    private Character escapechar;
    /**
     * Result set can be adjust with this interface.{@link ResultSetHelper}
     */
    private ResultSetHelper resultSetHelper;
    /**
     * Ends of lines in csv
     */
    private String lineEnd;

    /**
     * Getter for the field {@code parser}.
     *
     * @return parser
     */
    public ICSVParser getParser() {
        return parser;
    }

    /**
     * Getter for the field {@code separator}.
     *
     * @return separator
     */
    public Character getSeparator() {
        return separator;
    }

    /**
     * Getter for the field {@code quotechar }.
     *
     * @return quotechar
     */
    public Character getQuotechar() {
        return quotechar;
    }

    /**
     * Getter for the field {@code escapechar }.
     *
     * @return escapechar
     */
    public Character getEscapechar() {
        return escapechar;
    }

    /**
     * Getter for the field {@code resultSetHelper }.
     *
     * @return resultSetHelper
     */

    public ResultSetHelper getResultSetHelper() {
        return resultSetHelper;
    }

    /**
     * Getter for the field {@code lineEnd }.
     *
     * @return lineEnd
     */

    public String getLineEnd() {
        return lineEnd;
    }

    private CsvWriterConfig(Builder builder) {
        parser = builder.parser;
        separator = builder.separator;
        quotechar = builder.quotechar;
        escapechar = builder.escapechar;
        resultSetHelper = builder.resultSetHelper;
        lineEnd = builder.lineEnd;
    }

    /**
     * {@code CsvWriterConfig} builder static inner class.
     */
    public static final class Builder {
        private ICSVParser parser;
        private Character separator = CsvUtil.DEFAULT_SEPARATOR;
        private Character quotechar = ICSVWriter.DEFAULT_QUOTE_CHARACTER;
        private Character escapechar = ICSVWriter.DEFAULT_ESCAPE_CHARACTER;
        private ResultSetHelper resultSetHelper;
        private String lineEnd = System.lineSeparator();

        /**
         * Default constructor of Builder
         */
        public Builder() {
        }

        /**
         * Sets the {@code parser} and returns a reference to this Builder enabling method chaining.
         *
         * @param val
         *            the {@code parser} to set
         * @return a reference to this Builder
         */
        public Builder withParser(ICSVParser val) {
            parser = val;
            return this;
        }

        /**
         * Sets the {@code separator} and returns a reference to this Builder enabling method chaining.
         *
         * @param val
         *            the {@code separator} to set
         * @return a reference to this Builder
         */
        public Builder withSeparator(Character val) {
            separator = val;
            return this;
        }

        /**
         * Sets the {@code quotechar} and returns a reference to this Builder enabling method chaining.
         *
         * @param val
         *            the {@code quotechar} to set
         * @return a reference to this Builder
         */
        public Builder withQuotechar(Character val) {
            quotechar = val;
            return this;
        }

        /**
         * Sets the {@code escapechar} and returns a reference to this Builder enabling method chaining.
         *
         * @param val
         *            the {@code escapechar} to set
         * @return a reference to this Builder
         */
        public Builder withEscapechar(Character val) {
            escapechar = val;
            return this;
        }

        /**
         * Sets the {@code resultSetHelper} and returns a reference to this Builder enabling method chaining.
         *
         * @param val
         *            the {@code resultSetHelper} to set
         * @return a reference to this Builder
         */
        public Builder withResultSetHelper(ResultSetHelper val) {
            resultSetHelper = val;
            return this;
        }

        /**
         * Sets the {@code lineEnd} and returns a reference to this Builder enabling method chaining.
         *
         * @param val
         *            the {@code lineEnd} to set
         * @return a reference to this Builder
         */
        public Builder withLineEnd(String val) {
            lineEnd = val;
            return this;
        }

        /**
         * Returns a {@code CsvWriterConfig} built from the parameters previously set.
         *
         * @return a {@code CsvWriterConfig} built with parameters of this {@code CsvWriterConfig.Builder}
         */
        public CsvWriterConfig build() {
            return new CsvWriterConfig(this);
        }
    }
}
