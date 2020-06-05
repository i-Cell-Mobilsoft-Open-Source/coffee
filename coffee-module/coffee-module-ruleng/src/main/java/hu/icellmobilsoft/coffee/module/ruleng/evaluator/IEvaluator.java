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
package hu.icellmobilsoft.coffee.module.ruleng.evaluator;

import java.util.List;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Alap interfész a kiértékelés számára
 *
 * @author imre.scheffer
 * @param <INPUT>
 *            input objektum típus
 * @param <RULERESULT>
 *            alkalmazott rule-k kimeneti típusa
 * @since 1.0.0
 */
public interface IEvaluator<INPUT, RULERESULT> {

    /**
     * Kiértékelés elvégzése
     *
     * @param input
     *            objetum melyen a kiértékelést el kell végezni
     * @param inputIndex
     *            input adat melyik indexhez tartozik (ha például listában szerepel)
     * @return eredmény lista (akár siker és hiba esetén is)
     * @throws BaseException
     */
    List<RULERESULT> evaluate(INPUT input, Long inputIndex) throws BaseException;

    /**
     * Kiértékelés elvégzése
     *
     * @param input
     *            objetum melyen a kiértékelést el kell végezni
     * @return eredmény lista (akár siker és hiba esetén is)
     * @throws BaseException
     */
    default List<RULERESULT> evaluate(INPUT input) throws BaseException {
        return evaluate(input, null);
    }
}
