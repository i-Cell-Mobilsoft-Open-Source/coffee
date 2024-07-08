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
import java.util.Objects;
import java.util.Optional;
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
    private final String emojiInfo = "The meainings of the emojis used in the table:\n" + startupParamEmoji + " - meaning that it is a startup parameter.\n\n"
            + runtimeOverridableParamEmoji + " - meaning that this parameter can be overridden during runtime\n\n";
    private static final int DEFAULT_TITLE_HEADING_LEVEL = 3;
    private static final String KEY_DELIMITER = ".";


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
        Map<String, List<DocData>> docDataMap = dataList.stream().collect(Collectors.groupingBy(data -> StringUtils.substringBefore(data.getKey(), KEY_DELIMITER)));
        verifyTitles(docDataMap);
        writer.write(emojiInfo);

        String lastTitle = null;
        for (DocData docData : dataList) {
            String keyPrefixForTitle = StringUtils.substringBefore(docData.getKey(), KEY_DELIMITER);
            String title = findTitleForKey(docDataMap.get(keyPrefixForTitle)).orElse(keyPrefixForTitle);
            if (!Objects.equals(lastTitle, title)) {
                if (lastTitle != null) {
                    writer.write("|===\n\n");
                }
                if (StringUtils.equals(title, keyPrefixForTitle)) {
                    writeHeader(writer, title, getTitleHeadingLevelForKey(docDataMap.get(keyPrefixForTitle)), " keys\n[cols=\"");
                } else {
                    writeHeader(writer, title, getTitleHeadingLevelForKey(docDataMap.get(keyPrefixForTitle)), "\n[cols=\"");
                }
                lastTitle = title;
            }
            writeLine(docData, writer);
        }
        writer.write("|===\n");
    }

    private void verifyTitles(Map<String, List<DocData>> docDataMap) {
        for (String key : docDataMap.keySet()) {
            List<String> titleList = docDataMap.get(key).stream().map(DocData::getTitle)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            if (titleList.size() > 1 && titleList.stream().distinct().count() > 1) {
                throw new IllegalStateException(MessageFormat.format("Different titles given for same keyPrefixForTitle: [{0}]", key));
            }
        }
    }

    private Optional<String> findTitleForKey(List<DocData> docDataList) {
        return docDataList.stream().filter(docData -> StringUtils.isNotBlank(docData.getTitle()))
                .findFirst()
                .map(DocData::getTitle);
    }

    private int getTitleHeadingLevelForKey(List<DocData> docData) {
        return docData.stream().filter(data -> data.getTitleHeadingLevel() >= 0 || data.getTitleHeadingLevel() <= 5)
                .collect(Collectors.toList()).stream().mapToInt(DocData::getTitleHeadingLevel).min().orElse(DEFAULT_TITLE_HEADING_LEVEL);
    }

    private void writeHeader(Writer writer, String title, int titleHeadingLevel, String titlePostFix) throws IOException {
        writeTitleLevel(writer, titleHeadingLevel);
        writer.write(title);
        writer.write(titlePostFix);
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
