/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.quarkus.extension.module.mp.restclient.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.exception.annotation.FaultTypeCode;
import hu.icellmobilsoft.coffee.module.mp.restclient.exception.FaultTypeClasses;
import hu.icellmobilsoft.coffee.quarkus.extension.module.mp.restclient.FaultTypeClassesRecorder;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

/**
 * Greeting extension processor
 *
 * @author speter555
 * @since 2.6.0
 */
class MpRestclientProcessor {

    private static final Logger log = Logger.getLogger(MpRestclientProcessor.class);
    private static final String FEATURE = "coffee-module-mp-restclient-extension";

    private static final int DEFAULT_FAULT_TYPE_PRIORITY = 500;

    /**
     * Create Feature build item
     * 
     * @return Feature build item
     */
    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Get all FaultTypeCode annotated enums into buildStep
     * 
     * @param combinedIndex
     *            combined index build item, which contains all indexes
     * @return build item which contains FaultTypeCodes
     */
    @BuildStep
    @SuppressWarnings("unchecked")
    FaultTypeClassesBuildItem buildFaultTypeClassesBuildItem(CombinedIndexBuildItem combinedIndex) {

        @SuppressWarnings("rawtypes")
        List<Class<? extends Enum>> faultTypeClasses;

        @SuppressWarnings("rawtypes")
        final Map<Class<? extends Enum>, Integer> faultTypePriorityMap = new HashMap<>();

        try {

            for (AnnotationInstance annotationInstance : combinedIndex.getComputingIndex()
                    .getAnnotations(DotName.createSimple(FaultTypeCode.class))) {

                if (annotationInstance.target().hasAnnotation(DotName.createSimple(FaultTypeCode.class))) {

                    Class<? extends Enum> javaClass = (Class<? extends Enum>) Class.forName(annotationInstance.target().asClass().name().toString());
                    faultTypePriorityMap.put(javaClass, getPriority(javaClass));
                }
            }
            List<Class<? extends Enum>> sortedFaultTypes = faultTypePriorityMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            faultTypeClasses = List.copyOf(sortedFaultTypes);

            return new FaultTypeClassesBuildItem(faultTypeClasses);

        } catch (Exception e) {
            log.error("Error during search FaultyType codes.", e);
            return new FaultTypeClassesBuildItem(Collections.emptyList());
        }
    }

    /**
     * Use buildItem from previous step and create a bean for FaultTypeClasses interface
     * 
     * @param recorder
     *            recorder for FaultTypeClasses interface
     * @param faultTypeClassesBuildItem
     *            build item
     * @param syntheticBeanBuildItemBuildProducer
     *            producer of syncthetic bean
     */
    @BuildStep
    @Record(STATIC_INIT)
    void createBeanForFaultTypeClasses(FaultTypeClassesRecorder recorder, FaultTypeClassesBuildItem faultTypeClassesBuildItem,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer) {

        // Create an ApplicationScoped bean for FaultTypeClasses with create with recorder
        syntheticBeanBuildItemBuildProducer.produce(
                SyntheticBeanBuildItem.configure(FaultTypeClasses.class)
                        .scope(ApplicationScoped.class)
                        .unremovable()
                        .createWith(recorder.createFaultTypeClasses(faultTypeClassesBuildItem.getFaultTypeClasses()))
                        .done());
    }

    private static <T> int getPriority(Class<T> tClass) {
        int priority = 0;
        if (tClass.isAnnotationPresent(Priority.class)) {
            Priority priorityAnnotation = tClass.getAnnotation(Priority.class);
            priority = priorityAnnotation.value();
        }
        return priority > 0 ? priority : DEFAULT_FAULT_TYPE_PRIORITY;
    }
}
