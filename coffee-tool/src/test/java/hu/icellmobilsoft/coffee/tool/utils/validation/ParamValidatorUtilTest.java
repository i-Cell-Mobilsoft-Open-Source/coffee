/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.validation;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * @author attila.kiss
 */
class ParamValidatorUtilTest {

    @Test
    @DisplayName("Testing requireNonBlank() - null")
    void requireNonBlankTestNull() {

        // given
        String param = null;

        // when
        InvalidParameterException invalidParameterException = Assertions.assertThrows(InvalidParameterException.class, () -> {
            ParamValidatorUtil.requireNonBlank(param, "param");
        });

        // then
        assertInvalidParameterException(invalidParameterException);
    }

    @Test
    @DisplayName("Testing requireNonBlank() - blank")
    void requireNonBlankTestBlank() {

        // given
        String param = " ";

        // when
        InvalidParameterException invalidParameterException = Assertions.assertThrows(InvalidParameterException.class, () -> {
            ParamValidatorUtil.requireNonBlank(param, "param");
        });

        // then
        assertInvalidParameterException(invalidParameterException);
    }

    @Test
    @DisplayName("Testing requireNonBlank() - non blank")
    void requireNonBlankTestNonBlank() throws BaseException {

        // given
        String param = "value";

        // when
        String actual = ParamValidatorUtil.requireNonBlank(param, "param");

        // then
        Assertions.assertEquals(param, actual);
    }

    @Test
    @DisplayName("Testing requireNonEmpty() - null")
    void requireNonEmptyCollectionTestNull() {

        // given
        Collection<String> param = null;

        // when
        InvalidParameterException invalidParameterException = Assertions.assertThrows(InvalidParameterException.class, () -> {
            ParamValidatorUtil.requireNonEmpty(param, "param");
        });

        // then
        assertInvalidParameterException(invalidParameterException);
    }

    @Test
    @DisplayName("Testing requireNonEmpty() - empty")
    void requireNonEmptyCollectionTestEmpty() {

        // given
        Collection<String> param = Collections.emptyList();

        // when
        InvalidParameterException invalidParameterException = Assertions.assertThrows(InvalidParameterException.class, () -> {
            ParamValidatorUtil.requireNonEmpty(param, "param");
        });

        // then
        assertInvalidParameterException(invalidParameterException);
    }

    @Test
    @DisplayName("Testing requireNonEmpty() - non empty")
    void requireNonEmptyCollectionTestNonEmpty() throws BaseException {

        // given
        Collection<String> param = Collections.singleton("value");

        // when
        Collection<String> actual = ParamValidatorUtil.requireNonEmpty(param, "param");

        // then
        Assertions.assertEquals(param, actual);
    }

    @Test
    @DisplayName("Testing requireNonEmpty() - null")
    void requireNonEmptyOptionalTestNull() {

        // given
        Optional<String> param = null;

        // when
        InvalidParameterException invalidParameterException = Assertions.assertThrows(InvalidParameterException.class, () -> {
            ParamValidatorUtil.requireNonEmpty(param, "param");
        });

        // then
        assertInvalidParameterException(invalidParameterException);
    }

    @Test
    @DisplayName("Testing requireNonEmpty() - empty")
    void requireNonEmptyOptionalTestEmpty() {

        // given
        Optional<String> param = Optional.empty();

        // when
        InvalidParameterException invalidParameterException = Assertions.assertThrows(InvalidParameterException.class, () -> {
            ParamValidatorUtil.requireNonEmpty(param, "param");
        });

        // then
        assertInvalidParameterException(invalidParameterException);
    }

    @Test
    @DisplayName("Testing requireNonEmpty() - non empty")
    void requireNonEmptyOptionalTestNonEmpty() throws BaseException {

        // given
        Optional<String> param = Optional.of("value");

        // when
        String actual = ParamValidatorUtil.requireNonEmpty(param, "param");

        // then
        Assertions.assertEquals(param.get(), actual);
    }

    @Test
    @DisplayName("Testing requireNonNull() - null")
    void requireNonNullTestNull() {

        // given
        BigDecimal param = null;

        // when
        InvalidParameterException invalidParameterException = Assertions.assertThrows(InvalidParameterException.class, () -> {
            ParamValidatorUtil.requireNonNull(param, "param");
        });

        // then
        assertInvalidParameterException(invalidParameterException);
    }

    @Test
    @DisplayName("Testing requireNonNull() - non null")
    void requireNonNullTestNonNull() throws BaseException {

        // given
        BigDecimal param = BigDecimal.ONE;

        // when
        BigDecimal actual = ParamValidatorUtil.requireNonNull(param, "param");

        // then
        Assertions.assertEquals(param, actual);
    }

    private void assertInvalidParameterException(InvalidParameterException invalidParameterException) {
        Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, invalidParameterException.getFaultTypeEnum());
    }

}
