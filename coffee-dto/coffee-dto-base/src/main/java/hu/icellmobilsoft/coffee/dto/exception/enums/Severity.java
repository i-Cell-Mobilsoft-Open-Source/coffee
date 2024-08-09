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
package hu.icellmobilsoft.coffee.dto.exception.enums;

import java.util.Objects;

/**
 * <p>
 * Severity class.
 * </p>
 *
 * @deprecated Instead, use {@link hu.icellmobilsoft.coffee.se.api.exception.enums.Severity}.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Deprecated(since = "2.7.0")
public enum Severity {
    /**
     * MINOR.
     */
    MINOR(hu.icellmobilsoft.coffee.se.api.exception.enums.Severity.MINOR),
    /**
     * MAJOR.
     */
    MAJOR(hu.icellmobilsoft.coffee.se.api.exception.enums.Severity.MAJOR),
    /**
     * CRITICAL.
     */
    CRITICAL(hu.icellmobilsoft.coffee.se.api.exception.enums.Severity.CRITICAL);

    private final hu.icellmobilsoft.coffee.se.api.exception.enums.Severity newValue;

    private Severity(hu.icellmobilsoft.coffee.se.api.exception.enums.Severity newValue) {
        this.newValue = newValue;
    }

    public hu.icellmobilsoft.coffee.se.api.exception.enums.Severity toNewValue() {
        return newValue;
    }

    public static Severity fromNewValue(hu.icellmobilsoft.coffee.se.api.exception.enums.Severity newValue) {
        if (Objects.isNull(newValue)) {
            return null;
        }
        switch (newValue) {
        case MINOR:
            return MINOR;
        case MAJOR:
            return MAJOR;
        case CRITICAL:
            return CRITICAL;
        default:
            return null;
        }
    }

}
