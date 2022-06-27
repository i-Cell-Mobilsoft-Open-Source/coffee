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
package hu.icellmobilsoft.coffee.module.configdoc.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Writes the collected annotation data
 *
 * @param <T>
 *            the type parameter for data POJO
 * @author martin.nagy
 * @since 1.9.0
 */
public interface IDocWriter<T> {

    /**
     * Writes the collected annotation data to the passed {@link Writer}.
     *
     * @param dataList
     *            the collected data to be written
     * @param writer
     *            the writer
     * @throws IOException
     *             If an I/O error occurs
     */
    void write(List<T> dataList, Writer writer) throws IOException;
}
