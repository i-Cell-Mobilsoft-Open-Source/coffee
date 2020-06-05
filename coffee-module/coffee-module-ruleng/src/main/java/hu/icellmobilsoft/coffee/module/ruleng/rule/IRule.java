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

import java.util.Collections;
import java.util.List;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Általános rule interfész
 *
 * @author imre.scheffer
 * @param <INPUT>
 *            input objektum osztály típus, melyre a rule alkalmazható
 * @param <RULERESULT>
 *            kiértékelés eredmény class típus
 * @since 1.0.0
 */
public interface IRule<INPUT, RULERESULT extends RuleResult> {

    /**
     * Rule kiértékelése
     *
     * @param input
     *            kiértékelésre váró objektum
     * @return kiértékelés eredménye
     * @throws RuleException
     *             rule kiértékelését sértő hiba
     * @throws BaseException
     *             általános, nemvárt hiba a kiértékelésnél
     */
    RULERESULT apply(INPUT input) throws RuleException, BaseException;

    /**
     * Kiértékelés több eredményel
     *
     * @param input
     *            kiértékelésre váró objektum
     * @return kiértékelések eredménye
     * @throws RuleException
     *             rule kiértékelését sértő hiba
     * @throws BaseException
     *             általános, nemvárt hiba a kiértékelésnél
     */
    default List<RULERESULT> applyList(INPUT input) throws RuleException, BaseException {
        return Collections.emptyList();
    }
}
