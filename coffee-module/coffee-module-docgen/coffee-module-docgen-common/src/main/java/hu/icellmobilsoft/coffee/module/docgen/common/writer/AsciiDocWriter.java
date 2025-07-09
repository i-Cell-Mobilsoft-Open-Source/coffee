/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.docgen.common.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import hu.icellmobilsoft.coffee.module.docgen.common.data.Column;

/**
 * Writes content with the specified writer in asciidoc format
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class AsciiDocWriter {

    private final Writer writer;

    /**
     * Creates the object with the specified {@link Writer}
     * 
     * @param writer
     *            the writer
     */
    public AsciiDocWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * Writes the input value
     * 
     * @param value
     *            writable value
     * @throws IOException
     *             If an I/O error occurs
     */
    public void write(String value) throws IOException {
        writer.write(value);
    }

    /**
     * Writes the title with its heading level and postfix
     * 
     * @param title
     *            title main text
     * @param titleHeadingLevel
     *            title heading level
     * @param titlePostFix
     *            title postfix
     * @throws IOException
     *             If an I/O error occurs
     */
    public void writeTitle(String title, int titleHeadingLevel, String titlePostFix) throws IOException {
        writeTitleLevel(titleHeadingLevel);
        writer.write(title);
        writer.write(titlePostFix);
    }

    private void writeTitleLevel(int titleHeadingLevel) throws IOException {
        for (int i = 0; i < titleHeadingLevel; i++) {
            writer.write("=");
            if (i == titleHeadingLevel - 1) {
                writer.write(" ");
            }
        }
    }

    /**
     * Writes table headers based on columns
     * 
     * @param columns
     *            column configs for table
     * @throws IOException
     *             If an I/O error occurs
     */
    public void writeTableHeader(List<Column> columns) throws IOException {
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                writer.write(',');
            }
            writer.write(String.valueOf(columns.get(i).width()));
        }
        writer.write("\",options=header,stripes=even]\n|===\n");

        for (Column column : columns) {
            writer.write('|');
            writer.write(column.headerName());
        }
        writer.write("\n");
    }

    /**
     * Writes lineValues into the line of table
     * 
     * @param lineValues
     *            column values for one line
     * @throws IOException
     *             If an I/O error occurs
     */
    public void writeLine(List<String> lineValues) throws IOException {
        for (String value : lineValues) {
            writer.write('|');
            writer.write(value);
        }
        writer.write('\n');
    }

    /**
     * Writes table close tag
     * 
     * @throws IOException
     *             If an I/O error occurs
     */
    public void writeCloseTable() throws IOException {
        writer.write("|===\n\n");
    }
}
