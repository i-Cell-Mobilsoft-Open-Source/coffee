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
package hu.icellmobilsoft.coffee.module.docgen.config.writer.impl;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.docgen.common.writer.AsciiDocWriter;
import hu.icellmobilsoft.coffee.module.docgen.common.data.Column;
import hu.icellmobilsoft.coffee.module.docgen.common.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.docgen.config.config.ConfigDocColumn;
import hu.icellmobilsoft.coffee.module.docgen.config.config.ConfigDocConfig;
import hu.icellmobilsoft.coffee.module.docgen.config.data.DocData;

/**
 * Writes the collected annotation data in asciidoc format
 *
 * @author martin.nagy
 * @since 1.9.0
 */
public class ConfigDocAsciiDocWriter implements IDocWriter<DocData> {
    private final ConfigDocConfig config;
    private final String startupParamEmoji = "🚀";
    private final String runtimeOverridableParamEmoji = "⏳";
    private final String emojiInfo = "The meanings of the emojis used in the table:\n\n" + startupParamEmoji + " - meaning that it is a startup parameter.\n\n"
            + runtimeOverridableParamEmoji + " - meaning that this parameter can be overridden during runtime\n\n";
    private static final int DEFAULT_TITLE_HEADING_LEVEL = 3;
    private static final String KEY_DELIMITER = ".";


    /**
     * Constructor with the config object
     * 
     * @param config
     *            the config object
     */
    public ConfigDocAsciiDocWriter(ConfigDocConfig config) {
        this.config = config;
    }

    @Override
    public void write(List<DocData> dataList, Writer writer) throws IOException {
        AsciiDocWriter asciiDocWriter = new AsciiDocWriter(writer);

        Map<String, List<DocData>> docDataMap = dataList.stream().collect(Collectors.groupingBy(data -> StringUtils.substringBefore(data.getKey(), KEY_DELIMITER)));
        verifyTitles(docDataMap);
        asciiDocWriter.write(emojiInfo);

        String lastTitle = null;
        for (DocData docData : dataList) {
            String keyPrefixForTitle = StringUtils.substringBefore(docData.getKey(), KEY_DELIMITER);
            String title = findTitleForKey(docDataMap.get(keyPrefixForTitle)).orElse(keyPrefixForTitle);
            if (!Objects.equals(lastTitle, title)) {
                if (lastTitle != null) {
                    asciiDocWriter.writeCloseTable();
                }
                asciiDocWriter.writeTitle(title, getTitleHeadingLevelForKey(docDataMap.get(keyPrefixForTitle)), getTitlePostFix(title, keyPrefixForTitle));
                lastTitle = title;

                asciiDocWriter.writeTableHeader(getColumns());
            }
            asciiDocWriter.writeLine(getLineValues(docData));
        }
        asciiDocWriter.writeCloseTable();
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

    private String getTitlePostFix(String title, String keyPrefixForTitle) {
        if (StringUtils.equals(title, keyPrefixForTitle)) {
            return " keys\n[cols=\"";
        }

        return "\n[cols=\"";
    }

    private List<Column> getColumns() {
        return Arrays.stream(config.getColumns()).map(col -> new Column(getColumnDisplayName(col), getColumnWidth(col))).toList();
    }

    private List<String> getLineValues(DocData docData) {
        return Arrays.stream(config.getColumns()).map(col -> getColumnValue(docData, col)).toList();
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
