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
package hu.icellmobilsoft.coffee.quarkus.extension.deltaspike.data.deployment;

import java.text.MessageFormat;
import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Vetoed;

import org.apache.deltaspike.data.api.AbstractEntityRepository;
import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.impl.handler.QueryHandler;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.deltaspike.data.extension.RepositoryExtension;
import hu.icellmobilsoft.coffee.quarkus.extension.deltaspike.data.DeltaspikeDataRecorder;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;

/**
 * Deltaspike data processor
 * 
 * @author speter555
 * @since 2.6.0
 */
class DeltaspikeDataProcessor {

    private static final Logger log = Logger.getLogger(DeltaspikeDataProcessor.class);
    private static final String FEATURE = "coffee-deltaspike-data-extension";

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
     * Build DeltaspikeDataBuidItem which contains Repository-s
     * 
     * @param combinedIndex
     *            combined index build item, which contains all indexes
     * @return build item which contains Repository-s
     */
    @BuildStep
    DeltaspikeDataBuidItem buildDeltaspikeDataBuidItem(CombinedIndexBuildItem combinedIndex) {

        ArrayList<Class<?>> repositoryClasses = new ArrayList<>();
        ArrayList<Class<?>> removeableClasses = new ArrayList<>();
        removeableClasses.add(AbstractEntityRepository.class);
        removeableClasses.add(AbstractFullEntityRepository.class);
        removeableClasses.add(QueryHandler.class);

        for (AnnotationInstance annotationInstance : combinedIndex.getComputingIndex().getAnnotations(DotName.createSimple(Repository.class))) {
            try {
                if (annotationInstance.target().hasAnnotation(DotName.createSimple(Repository.class))) {
                    Class<?> javaClass = Class
                            .forName(annotationInstance.target().asClass().name().toString(), false, Thread.currentThread().getContextClassLoader());
                    if (!removeableClasses.contains(javaClass)) {
                        repositoryClasses.add(javaClass);
                        log.info(MessageFormat.format("Repository annotation detected on [{0}]", javaClass));
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("Error during get javaClass: [" + annotationInstance.target().asClass().name() + "]...");
            }
        }
        return new DeltaspikeDataBuidItem(repositoryClasses, removeableClasses);
    }

    /**
     * Add Vetoed annotation to removeableClasses
     * 
     * @param deltaspikeDataBuidItem
     *            build item which contains Repository-s and removeable repositories
     * @return annotation transformer build iter which contains the transformation of add Vetoed annotation.
     */
    @BuildStep
    public AnnotationsTransformerBuildItem removeAbstractEntityRepositories(DeltaspikeDataBuidItem deltaspikeDataBuidItem) {

        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {
            @Override
            public boolean appliesTo(AnnotationTarget.Kind kind) {
                return kind == org.jboss.jandex.AnnotationTarget.Kind.CLASS;
            }

            @Override
            public void transform(AnnotationsTransformer.TransformationContext ctx) {
                // Remove the MicrometerDecorator that requires the Micrometer API
                deltaspikeDataBuidItem.getRemoveableClasses().stream().filter(aClass -> !aClass.equals(QueryHandler.class)).forEach(aClass -> {
                    if (ctx.getTarget().asClass().name().equals(DotName.createSimple(aClass))) {
                        ctx.transform().add(Vetoed.class).done();
                    }
                });
            }
        });
    }

    @BuildStep
    void registerProxies(BuildProducer<NativeImageProxyDefinitionBuildItem> nativeImageProxyDefinitionBuildItemBuildProducer, DeltaspikeDataBuidItem deltaspikeDataBuidItem) {
        for (Class<?> repositoryClass : deltaspikeDataBuidItem.getRepositoryClasses()) {
            nativeImageProxyDefinitionBuildItemBuildProducer.produce(new NativeImageProxyDefinitionBuildItem(repositoryClass.getName()));
        }

    }
    /**
     * Create Repository beans
     * 
     * @param deltaspikeDataBuidItem
     *            build item which contains Repository-s
     * @param syntheticBeanBuildItemBuildProducer
     *            producer of syncthetic bean
     */
    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void createBeanForRepositoryClasses(DeltaspikeDataRecorder recorder, DeltaspikeDataBuidItem deltaspikeDataBuidItem,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer) {

        syntheticBeanBuildItemBuildProducer.produce(
                SyntheticBeanBuildItem.configure(RepositoryExtension.class)
                        .scope(ApplicationScoped.class)
                        .unremovable()
                        .createWith(recorder.createRepositoryExtension(deltaspikeDataBuidItem.getRepositoryClasses()))
                        .done());

        for (Class<?> type : deltaspikeDataBuidItem.getRepositoryClasses()) {

            syntheticBeanBuildItemBuildProducer.produce(
                    SyntheticBeanBuildItem.configure(type)
                            .name("CoffeeRepository#" + type.getName())
                            .addType(type)
                            // .addType(Object.class)
                            // .addQualifier(Default.class)
                            // .addQualifier(Any.class)
                            .identifier("CoffeeRepository#" + type.getName())
                            .scope(ApplicationScoped.class)
                            .unremovable()
                            .createWith(recorder.createRepository(type))
                            .addInjectionPoint(ClassType.create(DotName.createSimple(QueryHandler.class)))
                            .done());
        }

    }

}
