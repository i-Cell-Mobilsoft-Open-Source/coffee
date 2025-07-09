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
package hu.icellmobilsoft.coffee.module.docgen.sqlcatalog;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import hu.icellmobilsoft.coffee.module.docgen.common.writer.AsciiDocWriter;
import hu.icellmobilsoft.coffee.module.docgen.common.data.Column;
import hu.icellmobilsoft.coffee.module.docgen.common.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.config.SqlCatalogColumn;
import hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.data.SqlCatalogData;

/**
 * Writes the collected annotation data in asciidoc format
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class SqlCatalogAsciiDocWriter implements IDocWriter<SqlCatalogData> {

    /**
     * Creates a new sql catalog asciidoc writer
     */
    public SqlCatalogAsciiDocWriter() {
        super();
    }

    @Override
    public void write(List<SqlCatalogData> dataList, Writer writer) throws IOException {
        AsciiDocWriter asciiDocWriter = new AsciiDocWriter(writer);
        asciiDocWriter.writeTitle("Sql catalog", 1, "\n\n[cols=\"");
        asciiDocWriter.writeTableHeader(getColumns());

        for (SqlCatalogData data : dataList) {
            asciiDocWriter.writeLine(getLineValues(data));
        }

        asciiDocWriter.writeCloseTable();
    }

    private List<Column> getColumns() {
        return Arrays.stream(SqlCatalogColumn.values()).map(col -> new Column(this.getHeaderName(col), this.getColumnWidth(col))).toList();
    }

    private String getHeaderName(SqlCatalogColumn sqlCatalogColumn) {
        return switch (sqlCatalogColumn) {
        case CLASS_NAME -> "JAVA class identity";
        case METHOD_NAME -> "Method identity";
        case JPQL -> "JPQL text";
        case COMMENT -> "Comment";
        };
    }

    private int getColumnWidth(SqlCatalogColumn column) {
        return switch (column) {
            case CLASS_NAME -> 1;
            case METHOD_NAME -> 1;
            case JPQL -> 3;
            case COMMENT -> 1;
        };
    }

    private List<String> getLineValues(SqlCatalogData data) {
        return Arrays.stream(SqlCatalogColumn.values()).map(column -> this.getCellValue(column, data)).toList();
    }

    private String getCellValue(SqlCatalogColumn sqlCatalogColumn, SqlCatalogData data) {
        return switch (sqlCatalogColumn) {
            case CLASS_NAME -> data.className();
            case METHOD_NAME -> data.methodName();
            case JPQL -> data.jpql();
            case COMMENT -> data.comment();
        };
    }
}
