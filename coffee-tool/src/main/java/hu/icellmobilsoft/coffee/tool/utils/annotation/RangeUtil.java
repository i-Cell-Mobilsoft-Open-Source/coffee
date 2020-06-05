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
package hu.icellmobilsoft.coffee.tool.utils.annotation;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.annotation.Range;
import hu.icellmobilsoft.coffee.tool.version.ComparableVersion;

/**
 * A @Range annotációban használt értékek a ComparableVersion.class szerinti kezelését kiszolgáló eszköz gyűjtemény
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class RangeUtil {

    /**
     * Érték keresés a Range annotációban
     *
     * @param range
     *            range annotáció
     * @param value
     *            keresett érték bele-e tartozik a Range annotáció megjelölésben
     * @return true, ha a Range annotáció tartalmazza a keresett értéket
     */
    public static boolean inRange(Range range, String value) {
        if (range == null || StringUtils.isBlank(value)) {
            return false;
        }

        ComparableVersion current = new ComparableVersion(value);

        boolean geFrom;
        if (StringUtils.isNotBlank(range.from())) {
            ComparableVersion from = new ComparableVersion(range.from());
            geFrom = current.compareTo(from) >= 0;
        } else {
            geFrom = true;
        }
        // ha mar a from-nal is korabbi ne nezzuk tovabb
        if (!geFrom) {
            return false;
        }

        boolean leTo;
        if (StringUtils.isNotBlank(range.to())) {
            ComparableVersion to = new ComparableVersion(range.to());
            leTo = current.compareTo(to) <= 0;
        } else {
            leTo = true;
        }
        return geFrom && leTo;
    }

    /**
     * Érték keresés a Range annotációban tömb-ben
     *
     * @param ranges
     *            Range annotáció tömb
     * @param value
     *            keresett érték bele-e tartozik a Range annotációkban
     * @return true, ha a Range annotáció tömb tartalmazza a keresett értéket
     */
    public static boolean inRanges(Range[] ranges, String value) {
        if (ranges == null || StringUtils.isBlank(value)) {
            return false;
        }
        for (Range range : ranges) {
            boolean inRange = inRange(range, value);
            if (inRange) {
                return true;
            }
        }
        return false;
    }
}
