/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.configdoc.writer.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.configdoc.data.DocData;
import hu.icellmobilsoft.coffee.module.configdoc.writer.IDocWriter;

/**
 * Writes the collected annotation data in asciidoc format
 *
 * @author martin.nagy
 * @since 1.9.0
 */
public class AsciiDocWriter implements IDocWriter {
    private static final String KEY_DELIMITER = ".";

    @Override
    public void write(List<DocData> dataList, Writer writer) throws IOException {
        writer.write(":toc: left\n");
        writer.write(":sectnums:\n");

        String lastPrefix = null;
        for (DocData docData : dataList) {
            String prefix = StringUtils.substringBefore(docData.getKey(), KEY_DELIMITER);
            if (!Objects.equals(lastPrefix, prefix)) {
                if (lastPrefix != null) {
                    writer.write("|===\n\n");
                }

                writeHeader(writer, prefix);
                lastPrefix = prefix;
            }

            writeLine(docData, writer);
        }
        writer.write("|===\n");
    }

    private void writeHeader(Writer writer, String prefix) throws IOException {
        writer.write("=== ");
        writer.write(prefix);
        writer.write(" keys\n[cols=\"1,1,3,1\",options=header,stripes=even]\n|===\n");
        writer.write("|Key|Source|Description|Default value\n");
    }

    private void writeLine(DocData docData, Writer writer) throws IOException {
        writer.write('|');
        writer.write(docData.getKey());
        writer.write('|');
        writer.write(docData.getSource());
        writer.write('|');
        writer.write(StringUtils.defaultString(docData.getDescription(), ""));
        writer.write('|');
        writer.write(StringUtils.defaultString(docData.getDefaultValue(), ""));
        writer.write('\n');
    }
}
