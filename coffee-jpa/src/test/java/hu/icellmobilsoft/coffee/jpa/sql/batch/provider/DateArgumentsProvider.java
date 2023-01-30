/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.jpa.sql.batch.provider;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import org.hibernate.type.BasicType;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import hu.icellmobilsoft.coffee.jpa.sql.batch.constants.TestBasicTypes;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

/**
 * {@link ArgumentsProvider} to providing {@link java.sql.Date} test cases for BatchService.
 * 
 * @author csaba.balogh
 * @version 2.0.0
 */
public class DateArgumentsProvider implements ArgumentsProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        LocalDate localDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        Date date = DateUtil.toDate(localDate);
        Calendar calendar = DateUtil.toCalendar(date);

        return Stream.of( //
                toArguments(TestBasicTypes.JDBC_DATE_BASIC_TYPE, sqlDate, sqlDate), //
                toArguments(TestBasicTypes.LOCAL_DATE_BASIC_TYPE, localDate, sqlDate), //
                toArguments(TestBasicTypes.UTIL_DATE_DATE_BASIC_TYPE, date, sqlDate), //
                toArguments(TestBasicTypes.CALENDAR_DATE_BASIC_TYPE, calendar, sqlDate) //
        );
    }

    private static Arguments toArguments(BasicType<?> basicType, Object value, Object expectedValue) {
        return Arguments.of(toNamed(basicType), value, expectedValue);
    }

    private static Named<BasicType<?>> toNamed(BasicType<?> basicType) {
        return Named.of(basicType.getJavaType().getSimpleName(), basicType);
    }

}
