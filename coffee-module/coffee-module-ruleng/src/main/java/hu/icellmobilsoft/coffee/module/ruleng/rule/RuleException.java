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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Kiértékelést sértő hiba
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class RuleException extends BaseException {

    private static final long serialVersionUID = 1L;

    private Object ruleResult;

    /**
     * Konstruktor
     *
     * @param ruleResultCode
     *            kiértékelés hiba kódja
     * @param message
     *            hiba szöveg
     * @param ruleResult
     *            kiértékelést sértő hiba objektum
     */
    public <RULERESULT extends RuleResult> RuleException(Enum<?> ruleResultCode, String message, RULERESULT ruleResult) {
        super(ruleResultCode, message);
        this.setRuleResult(ruleResult);
    }

    /**
     * <p>Getter for the field <code>ruleResult</code>.</p>
     *
     * @return az értéket mappelni kell "extends RuleResult" típusúra
     */
    public Object getRuleResult() {
        return ruleResult;
    }

    /**
     * <p>Setter for the field <code>ruleResult</code>.</p>
     */
    public <RULERESULT extends RuleResult> void setRuleResult(RULERESULT ruleResult) {
        this.ruleResult = ruleResult;
    }
}
