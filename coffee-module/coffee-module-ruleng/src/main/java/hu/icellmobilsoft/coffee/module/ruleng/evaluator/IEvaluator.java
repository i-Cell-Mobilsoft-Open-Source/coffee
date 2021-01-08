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
 * Base interface for evaluations.
 *
 * @author imre.scheffer
 * @param <INPUT>
 *            input object type
 * @param <RULERESULT>
 *            output type of executed Rules
 * @since 1.0.0
 */
public interface IEvaluator<INPUT, RULERESULT> {

    /**
     * Executes evaluation on given input.
     *
     * @param input
     *            object to execute the evaluation on
     * @param inputIndex
     *            index of the input object (e.g. if it's in a list)
     * @return esult of evaluation (either in successful or exceptional case)
     * @throws BaseException
     *             if any exception occurs
     * @see #evaluate(Object)
     */
    List<RULERESULT> evaluate(INPUT input, Long inputIndex) throws BaseException;

    /**
     * Executes evaluation on given input.
     *
     * @param input
     *            object to execute the evaluation on
     * @return result of evaluation (either in successful or exceptional case)
     * @throws BaseException
     *             if any exception occurs
     * @see #evaluate(Object, Long)
     */
    default List<RULERESULT> evaluate(INPUT input) throws BaseException {
        return evaluate(input, null);
    }
}
