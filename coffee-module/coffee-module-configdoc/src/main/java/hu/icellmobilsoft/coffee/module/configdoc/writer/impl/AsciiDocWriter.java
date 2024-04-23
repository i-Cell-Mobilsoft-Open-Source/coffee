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
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.configdoc.config.ConfigDocColumn;
import hu.icellmobilsoft.coffee.module.configdoc.config.ConfigDocConfig;
import hu.icellmobilsoft.coffee.module.configdoc.data.DocData;
import hu.icellmobilsoft.coffee.module.configdoc.writer.IDocWriter;

/**
 * Writes the collected annotation data in asciidoc format
 *
 * @author martin.nagy
 * @since 1.9.0
 */
public class AsciiDocWriter implements IDocWriter<DocData> {
    private final ConfigDocConfig config;
    private final String startupParamEmoji = "🚀";
    private final String runtimeOverridableParamEmoji = "⏳";
    private final String emojiInfo = "== The meainings of the emojis used in the table:\n" + startupParamEmoji + " - meaning that it is a startup parameter.\n\n"
            + runtimeOverridableParamEmoji + " - meaning that this parameter can be overridden during runtime\n\n";
    private static final int DEFAULT_TITLE_HEADING_LEVEL = 3;


    /**
     * Constructor with the config object
     * 
     * @param config
     *            the config object
     */
    public AsciiDocWriter(ConfigDocConfig config) {
        this.config = config;
    }

    @Override
    public void write(List<DocData> dataList, Writer writer) throws IOException {
        writer.write(emojiInfo);
        Map<String, List<DocData>> docDataMap = dataList.stream().collect(Collectors.groupingBy(DocData::getKey));
        for (String key : docDataMap.keySet()) {
            String title = getTitleForKey(docDataMap.get(key));
            int titleHeadingLevel = getTitleHeadingLevelForKey(docDataMap.get(key));
            writeHeader(writer, title, titleHeadingLevel);
            for (DocData docData : docDataMap.get(key)) {
                writeLine(docData, writer);
            }
            writer.write("|===\n");
        }
    }

    private String getTitleForKey(List<DocData> docData) {
        String title = docData.get(0).getTitle();
        docData.stream().filter(data -> StringUtils.isNotBlank(data.getTitle()))
                .collect(Collectors.groupingBy(DocData::getTitle))
                .forEach((tmpTitle, data) -> {
                    if (data.size() > 1 && data.stream().map(DocData::getTitle).distinct().count() > 1) {
                        throw new IllegalStateException(MessageFormat.format("Different titles given for same key: [{0}]", docData.get(0).getKey()));
                    }
                });
        return StringUtils.defaultString(title, docData.get(0).getKey());
    }

    private int getTitleHeadingLevelForKey(List<DocData> docData) {
        return docData.stream().filter(data -> data.getTitleHeadingLevel() >= 0 || data.getTitleHeadingLevel() <= 5)
                .collect(Collectors.toList()).stream().mapToInt(DocData::getTitleHeadingLevel).min().orElse(DEFAULT_TITLE_HEADING_LEVEL);
    }

    private void writeHeader(Writer writer, String prefix, int titleHeadingLevel) throws IOException {
        writeTitleLevel(writer, titleHeadingLevel);
        writer.write(prefix);
        writer.write(" keys\n[cols=\"");
        ConfigDocColumn[] columns = config.getColumns();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                writer.write(',');
            }
            writer.write(String.valueOf(getColumnWidth(columns[i])));
        }
        writer.write("\",options=header,stripes=even]\n|===\n");

        for (ConfigDocColumn column : config.getColumns()) {
            writer.write('|');
            writer.write(getColumnDisplayName(column));
        }
        writer.write("\n");
    }

    private void writeTitleLevel(Writer writer, int titleHeadingLevel) throws IOException {
        for (int i = 0; i < titleHeadingLevel; i++) {
            writer.write("=");
            if (i == titleHeadingLevel - 1) {
                writer.write(" ");
            }
        }
    }

    private void writeLine(DocData docData, Writer writer) throws IOException {
        for (ConfigDocColumn column : config.getColumns()) {
            writer.write('|');
            writer.write(getColumnValue(docData, column));
        }
        writer.write('\n');
    }

    private String getColumnValue(DocData docData, ConfigDocColumn column) {
        switch (column) {
        case KEY:
            return docData.getKey();
        case SOURCE:
            return docData.getSource();
        case DESCRIPTION:
            return StringUtils.defaultString(docData.getDescription(), "");
        case DEFAULT_VALUE:
            return StringUtils.defaultString(docData.getDefaultValue(), "");
        case SINCE:
            return StringUtils.defaultString(docData.getSince(), "");
        case FEATURES:
            return getFeaturesString(docData);
        default:
            throw newInvalidColumnException(column);
        }
    }

    private String getColumnDisplayName(ConfigDocColumn column) {
        switch (column) {
        case KEY:
            return "Key";
        case SOURCE:
            return "Source";
        case DESCRIPTION:
            return "Description";
        case DEFAULT_VALUE:
            return "Default value";
        case SINCE:
            return "Since";
        case FEATURES:
            return "Features";
        default:
            throw newInvalidColumnException(column);
        }
    }

    private int getColumnWidth(ConfigDocColumn column) {
        switch (column) {
        case KEY:
        case SOURCE:
        case DEFAULT_VALUE:
        case SINCE:
            return 1;
        case DESCRIPTION:
            return 3;
        case FEATURES:
            return 1;
        default:
            throw newInvalidColumnException(column);
        }
    }

    private IllegalStateException newInvalidColumnException(ConfigDocColumn column) {
        return new IllegalStateException("Invalid column: " + column);
    }

    private String getFeaturesString(DocData docData) {
        StringBuilder features = new StringBuilder();
        if (docData.isStartupParam())
            features.append(startupParamEmoji);
        if (docData.isRuntimeOverridable())
            features.append(runtimeOverridableParamEmoji);
        return features.toString();
    }

}
