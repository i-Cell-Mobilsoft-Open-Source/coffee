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
package hu.icellmobilsoft.coffee.module.ruleng.rule;

import java.util.Comparator;

/**
 * Rule kiértékelését kiegészítő interfész
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface IRuleSelector extends Comparable<IRuleSelector> {

    /**
     * Sorrend, ez szerint sorrendezzünk
     *
     * @return alapból 0
     */
    default int order() {
        return 0;
    }

    /**
     * Csoportosítás
     *
     * @return alapból {@link RuleGroup#NONE}
     * @see RuleGroup
     */
    default Enum<?> group() {
        return RuleGroup.NONE;
    }

    /** {@inheritDoc} */
    @Override
    default int compareTo(IRuleSelector o) {
        return Comparator.comparingInt(IRuleSelector::order).compare(this, o);
    }
}
