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

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.util.TypeLiteral;
import jakarta.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.annotation.Range;
import hu.icellmobilsoft.coffee.cdi.annotation.Version;
import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.ruleng.rule.IRule;
import hu.icellmobilsoft.coffee.module.ruleng.rule.IRuleSelector;
import hu.icellmobilsoft.coffee.module.ruleng.rule.RuleException;
import hu.icellmobilsoft.coffee.module.ruleng.rule.RuleGroup;
import hu.icellmobilsoft.coffee.module.ruleng.rule.RuleResult;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.coffee.tool.utils.annotation.RangeUtil;

/**
 * Base evaluation logic for a single object type. Eg.:
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
 *            evaluated object type
 * @param <RULERESULT>
 *            output type of executed evaluations
 * @since 1.0.0
 */
public abstract class AbstractEvaluator<INPUT, RULERESULT extends RuleResult> implements IEvaluator<INPUT, RULERESULT> {

    @Inject
    @ThisLogger
    private AppLogger log;

    private Map<Enum<?>, List<IRule<INPUT, RULERESULT>>> groupedRules;

    private String currentVersion;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractEvaluator() {
        super();
    }

    /**
     * Qualifier which the Rules are annotated with. Eg.:
     *
     * <pre>
     * &#64;Override
     * protected Annotation cdiSelectLiteral() {
     *     return new RuleQualifier.Literal();
     * }
     * </pre>
     *
     * @return {@code Annotation}
     */
    protected abstract Annotation cdiSelectLiteral();

    /**
     * Extended Rule type, {@link TypeLiteral}. Eg.:
     *
     * <pre>
     * &#64;Override
     * protected TypeLiteral&lt;IRule&lt;ExampleObjectType, CustomRuleResult&gt;&gt; cdiTypeLiteral() {
     *     return new TypeLiteral&lt;IRule&lt;ExampleObjectType, CustomRuleResult&gt;&gt;() {
     *         private static final long serialVersionUID = 1L;
     *     };
     * }
     * </pre>
     *
     * @return {@code TypeLiteral}
     */
    protected abstract TypeLiteral<IRule<INPUT, RULERESULT>> cdiTypeLiteral();

    /**
     * Rule comparator by {@link IRuleSelector} and Rule class name
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
     * Function which groups by {@link IRuleSelector}.
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
     * Initializes Rule instance from CDI container.
     *
     * @param type
     *            TypeLiteral, implementation of Rules
     * @param annotation
     *            Qualifier which the Rules are annotated with
     * @return CDI Rule instance
     */
    protected Instance<IRule<INPUT, RULERESULT>> initRuleInstances(TypeLiteral<IRule<INPUT, RULERESULT>> type, Annotation annotation) {
        return CDI.current().select(type, annotation);
    }

    /**
     * Prepares a Rule list by grouping and ordering.
     *
     * @param instance
     *            Rule list
     * @return prepared Rule list
     */
    protected Map<Enum<?>, List<IRule<INPUT, RULERESULT>>> prepareRuleInstances(Instance<IRule<INPUT, RULERESULT>> instance) {
        // Grouping
        Map<Enum<?>, List<IRule<INPUT, RULERESULT>>> groupedRules = instance.stream().collect(Collectors.groupingBy(ruleGroupGetter));
        // sorting
        groupedRules.values().forEach(l -> Collections.sort(l, ruleComparator));
        return groupedRules;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public List<RULERESULT> evaluate(INPUT input, Long inputIndex) throws BaseException {
        if (input == null) {
            throw new InvalidParameterException("input is null");
        }
        List<RULERESULT> evalResults = new ArrayList<>();

        if (groupedRules == null) {
            Instance<IRule<INPUT, RULERESULT>> instances = initRuleInstances(cdiTypeLiteral(), cdiSelectLiteral());
            // preparation
            groupedRules = prepareRuleInstances(instances);
        }

        for (Entry<Enum<?>, List<IRule<INPUT, RULERESULT>>> ruleEntry : groupedRules.entrySet()) {
            try {
                for (IRule<INPUT, RULERESULT> rule : ruleEntry.getValue()) {
                    // The possibility of interrupting processing (validation) is handled by throwing an exception.
                    if (StringUtils.isNotBlank(getCurrentVersion())) {
                        // Version checking
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
     * Applies rule to given input.
     *
     * @param rule
     *            rule to apply
     * @param input
     *            input data to apply the rule on
     * @param inputIndex
     *            index of {@code input}, eg. if it's in a list
     * @return result of {@code rule} application (either in successful or exceptional case)
     * @throws RuleException
     *             if rule group ordered execution is interrupted
     * @throws BaseException
     *             if case of an unexpected error
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
     * Dispose evaluator
     */
    @PreDestroy
    public void dispose() {
        preDestroy(groupedRules);
    }

    /**
     * Rule disposal method, does nothing by default, child classes can override it if manual rule destroy is necessary (ie.: in case of Dependent
     * scoped rule implementations)
     *
     * @param groupedRules
     *            the rules to dispose
     */
    protected void preDestroy(Map<Enum<?>, List<IRule<INPUT, RULERESULT>>> groupedRules) {
        // do nothing by default
    }

    /**
     * Getter for the field {@code currentVersion}. Rules are activated in accordance with the current version.
     *
     * @return currentVersion
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Setter for the field {@code currentVersion}. Rules are activated in accordance with the current version.
     *
     * @param currentVersion
     *            currentVersion
     */
    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
