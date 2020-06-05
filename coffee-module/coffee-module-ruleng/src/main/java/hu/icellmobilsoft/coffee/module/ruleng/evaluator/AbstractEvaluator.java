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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.annotation.Range;
import hu.icellmobilsoft.coffee.cdi.annotation.Version;
import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.ruleng.rule.IRule;
import hu.icellmobilsoft.coffee.module.ruleng.rule.IRuleSelector;
import hu.icellmobilsoft.coffee.module.ruleng.rule.RuleException;
import hu.icellmobilsoft.coffee.module.ruleng.rule.RuleGroup;
import hu.icellmobilsoft.coffee.module.ruleng.rule.RuleResult;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.coffee.tool.utils.annotation.RangeUtil;

/**
 * Általános kiértékelés logika egyetlen objektum típusra. Minta hassználat:
 *
 * <pre>
 * &#64;Model
 * public class RuleTypeEvaluatorExampleObjectType extends AbstractEvaluator&lt;ExampleObjectType, CustomRuleResult&gt; {
 *
 *     &#64;Override
 *     protected Annotation cdiSelectLiteral() {
 *         return new RuleQualifier.Literal();
 *     }
 *
 *     &#64;Override
 *     protected TypeLiteral&lt;IRule&lt;ExampleObjectType, CustomRuleResult&gt;&gt; cdiTypeLiteral() {
 *         return new TypeLiteral&lt;IRule&lt;ExampleObjectType, CustomRuleResult&gt;&gt;() {
 *             private static final long serialVersionUID = 1L;
 *         };
 *     }
 *
 * }
 * </pre>
 *
 * @author imre.scheffer
 * @param <INPUT>
 *            kiértékelt objektum
 * @param <RULERESULT>
 *            kiértékelés eredmény típusa
 * @since 1.0.0
 */
public abstract class AbstractEvaluator<INPUT, RULERESULT extends RuleResult> implements IEvaluator<INPUT, RULERESULT> {

    @Inject
    @ThisLogger
    private AppLogger log;

    private Map<Enum<?>, List<IRule<INPUT, RULERESULT>>> groupedRules;

    private String currentVersion;

    /**
     * Qualifier annotacio, amivel jelolve vannak a Rule-k. Minta:
     *
     * <pre>
     * &#64;Override
     * protected Annotation cdiSelectLiteral() {
     *     return new RuleQualifier.Literal();
     * }
     * </pre>
     */
    protected abstract Annotation cdiSelectLiteral();

    /**
     * Rule-k származtatott típusa, abból TypeLiteral készítve. Minta:
     *
     * <pre>
     * &#64;Override
     * protected TypeLiteral&lt;IRule&lt;ExampleObjectType, CustomRuleResult&gt;&gt; cdiTypeLiteral() {
     *     return new TypeLiteral&lt;IRule&lt;ExampleObjectType, CustomRuleResult&gt;&gt;() {
     *         private static final long serialVersionUID = 1L;
     *     };
     * }
     * </pre>
     */
    protected abstract TypeLiteral<IRule<INPUT, RULERESULT>> cdiTypeLiteral();

    /**
     * Rule comparator a IRuleSelector és Rule class név alapján
     */
    protected Comparator<IRule<INPUT, RULERESULT>> ruleComparator = new Comparator<IRule<INPUT, RULERESULT>>() {
        @Override
        public int compare(IRule<INPUT, RULERESULT> o1, IRule<INPUT, RULERESULT> o2) {
            if (o1 instanceof IRuleSelector && o2 instanceof IRuleSelector) {
                int comp = ((IRuleSelector) o1).compareTo((IRuleSelector) o2);
                if (comp != 0) {
                    return comp;
                }
            }
            return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
        }
    };

    /**
     * Group funkció ami a IRuleSelector szerint csoportosít
     */
    protected Function<IRule<INPUT, RULERESULT>, Enum<?>> ruleGroupGetter = new Function<IRule<INPUT, RULERESULT>, Enum<?>>() {
        @Override
        public Enum<?> apply(IRule<INPUT, RULERESULT> r) {
            if (r instanceof IRuleSelector) {
                return ((IRuleSelector) r).group();
            }
            return RuleGroup.NONE;
        }
    };

    /**
     * Rule instance kikeresése a CDI konténerből
     *
     * @param type
     *            TypeLiteral, Rule-k implementációja
     * @param annotation
     *            Qualifier annotació amelyel a Rule-k jelölve vannak
     */
    protected Instance<IRule<INPUT, RULERESULT>> initRuleInstances(TypeLiteral<IRule<INPUT, RULERESULT>> type, Annotation annotation) {
        return CDI.current().select(type, annotation);
    }

    /**
     * Előkészítés a Rule listán, csoportosítás és sorbarendezés
     *
     * @param instance
     *            Rule lista
     */
    protected Map<Enum<?>, List<IRule<INPUT, RULERESULT>>> prepareRuleInstances(Instance<IRule<INPUT, RULERESULT>> instance) {
        // csoportositas
        Map<Enum<?>, List<IRule<INPUT, RULERESULT>>> groupedRules = instance.stream().collect(Collectors.groupingBy(ruleGroupGetter));
        // sorbarendezes
        groupedRules.values().forEach(l -> Collections.sort(l, ruleComparator));
        return groupedRules;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<RULERESULT> evaluate(INPUT input, Long inputIndex) throws BaseException {
        if (input == null) {
            throw new TechnicalException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, "input is null");
        }
        List<RULERESULT> evalResults = new ArrayList<>();

        if (groupedRules == null) {
            Instance<IRule<INPUT, RULERESULT>> instances = initRuleInstances(cdiTypeLiteral(), cdiSelectLiteral());
            // előkészítés
            groupedRules = prepareRuleInstances(instances);
        }

        for (Entry<Enum<?>, List<IRule<INPUT, RULERESULT>>> ruleEntry : groupedRules.entrySet()) {
            try {
                for (IRule<INPUT, RULERESULT> rule : ruleEntry.getValue()) {
                    // feldolgozas (validalas) megszakitas lehetosege az exception dobasaban van
                    if (StringUtils.isNotBlank(getCurrentVersion())) {
                        // verzio ellenorzes
                        Version ruleVersionAnnotation = AnnotationUtil.getAnnotation(rule.getClass(), Version.class);
                        if (ruleVersionAnnotation != null) {
                            Range[] ranges = ruleVersionAnnotation.include();
                            if (!RangeUtil.inRanges(ranges, getCurrentVersion())) {
                                continue;
                            }
                        }
                    }
                    List<RULERESULT> ruleResults = applyRule(rule, input, inputIndex);
                    evalResults.addAll(ruleResults);
                }
            } catch (RuleException re) {
                log.info("Validation break on [{0}] group with [{1}] message", ruleEntry.getKey(), re.getMessage());
                evalResults.add((RULERESULT) re.getRuleResult());
            }
        }
        return evalResults;
    }

    /**
     * Rule alkalmazása
     *
     * @param rule
     *            rule
     * @param input
     *            ellenőrzött adat
     * @param inputIndex
     *            ellenőrzött adat indexe (pl. ha listában szerepel)
     * @return Rule eredményei (akár sikeresség vagy szabály sértés)
     * @throws RuleException
     *             rule csoport sorrend végrehajtási megszakítás
     * @throws BaseException
     */
    protected List<RULERESULT> applyRule(IRule<INPUT, RULERESULT> rule, INPUT input, Long inputIndex) throws RuleException, BaseException {
        // ez eleg sokat kepes logolni
        // log.trace("apply rule [{0}] to input [{1}] on inputIndex [{2}]", rule.getClass(), input, inputIndex);

        List<RULERESULT> validationResults = new ArrayList<>();
        RULERESULT ruleResult = rule.apply(input);
        if (ruleResult != null) {
            if (inputIndex != null) {
                ruleResult.setIndex(inputIndex);
            }
            validationResults.add(ruleResult);
        }

        List<RULERESULT> ruleResults = rule.applyList(input);
        if (CollectionUtils.isNotEmpty(ruleResults)) {
            if (inputIndex != null) {
                ruleResults.stream().forEach(r -> r.setIndex(inputIndex));
            }
            validationResults.addAll(ruleResults);
        }
        return validationResults;
    }

    /**
     * Aktuális verzió mi szerint a Rule-k aktiválva vannak
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Aktuális verzió mi szerint a Rule-k aktiválva vannak
     *
     * @param currentVersion
     */
    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
