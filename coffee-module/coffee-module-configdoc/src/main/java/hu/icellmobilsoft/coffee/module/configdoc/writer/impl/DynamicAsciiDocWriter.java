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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.configdoc.data.DynamicDocData;
import hu.icellmobilsoft.coffee.module.configdoc.writer.IDocWriter;

/**
 * Writes the collected {@link DynamicDocData} and corresponding templates to data in asciidoc format. With variables defined for the template.
 *
 * @author mark.petrenyi
 * @since 1.10.0
 */
public class DynamicAsciiDocWriter implements IDocWriter<DynamicDocData> {

    @Override
    public void write(List<DynamicDocData> dataList, Writer writer) throws IOException {
        for (DynamicDocData docHeader : dataList) {
            writeData(writer, docHeader);
        }
    }

    private void writeData(Writer writer, DynamicDocData docHeader) throws IOException {
        writer.write("\n");
        String[] templateVariables = docHeader.getTemplateVariables();
        if (templateVariables != null) {
            for (int i = 0; i < templateVariables.length; i++) {
                writer.write(":" + i + ": ");
                writer.write(templateVariables[i]);
                writer.write("\n");
            }
        }

        writer.write("\n");
        writer.write("== ");
        writer.write(StringUtils.isNotBlank(docHeader.getTitle()) ? docHeader.getTitle() : docHeader.getTemplateClassName() + " keys");
        writer.write("\n");
        if (StringUtils.isNotBlank(docHeader.getDescription())) {
            writer.write(docHeader.getDescription());
            writer.write("\n");
        }
        writer.write("\n");
        if (StringUtils.isNotBlank(docHeader.getTemplate())) {
            writer.write(docHeader.getTemplate());
        }
    }
}
