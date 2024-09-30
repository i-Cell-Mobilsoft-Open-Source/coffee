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
package hu.icellmobilsoft.coffee.tool.jsonb.adapter;

import javax.xml.datatype.Duration;

import jakarta.json.bind.adapter.JsonbAdapter;

import hu.icellmobilsoft.coffee.tool.utils.date.DateXmlUtil;

/**
 * {@link Duration} jsonb adapter
 *
 * @author bucherarnold
 * @since 2.9.0
 */
public class DurationJsonbAdapter implements JsonbAdapter<Duration, String> {

    @Override
    public String adaptToJson(Duration duration) {
        return String.valueOf(duration);
    }

    @Override
    public Duration adaptFromJson(String duration) {
        return DateXmlUtil.getDatatypeFactory().newDuration(duration);
    }
}
